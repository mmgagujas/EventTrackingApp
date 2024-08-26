package com.mobile2app.eventtracker;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile2app.eventtracker.model.Event;
import com.mobile2app.eventtracker.viewmodel.EventListViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * UserEventsFragment is a Fragment subclass that represents the events associated with a user.
 * It contains a RecyclerView for listing the events, an EventListViewModel for managing the event data,
 * a constant for SMS permissions, and a logout button.
 *
 * @author Michael Gagujas
 * @since 2024-08-18
 */
public class UserEventsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private Button logout;


    public UserEventsFragment() {
        // Empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This method inflates the layout for this fragment, initializes the ViewModel,
     * sets up the RecyclerView with an adapter and a layout manager.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     *                           The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return                   The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_events, container, false);

        ArrayList<Event> list = new ArrayList<>();
        EventAdapter adapter = new EventAdapter(list);
        RecyclerView recyclerView = view.findViewById(R.id.event_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /**
         * Sets up a Firebase Database reference to the "EventTracker" node and adds a ValueEventListener to it.
         * The listener fetches event data from the database, clears the existing list of events, and repopulates it with the new data.
         * The adapter is then notified of the data change to update the UI.
         */
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("EventTracker");
        reference.addValueEventListener(new ValueEventListener() {
            /**
             * This method is called whenever data at the specified database reference changes.
             * It clears the current list of events, iterates through the children of the DataSnapshot,
             * extracts event details, creates Event objects, and adds them to the list.
             * Finally, it notifies the adapter that the data has changed.
             *
             * @param dataSnapshot The DataSnapshot containing data from the database reference.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey(); // This will get the key of the child node, which is the ID of the event
                    String title = snapshot.child("title").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    Event event = new Event(id, title, date, time);
                    list.add(event);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log the error
                Log.w(TAG, "Failed to read value.", error.toException());

                // Show a toast message to the user
                Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Initializes the logout button and sets an OnClickListener to handle the logout process.
         * When the button is clicked, the user is signed out from Firebase and Google Sign-In,
         * a toast message is displayed, and the user is redirected to the LoginActivity.
         */
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click event for the logout button.
             * Signs the user out from Firebase and Google Sign-In, displays a toast message,
             * and starts the LoginActivity.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                GoogleSignIn.getClient(
                        getActivity(),
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                ).signOut();

                Toast.makeText(getActivity(), "Logged Out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        checkPermissions();
        return view;
    }

    /**
     * Checks for the necessary permissions used in the app.
     * If the SEND_SMS permission is not granted, it shows a custom permission screen.
     * If the permission is granted, it proceeds to prepare a message with upcoming events.
     */
    private void checkPermissions() {
        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            showCustomPermissionScreen();
        } else {
            // Hard-coded number, associate with account phone number or user-input
            String phoneNumber = "15551234567";
            final String[] message = {"Your upcoming events:\n"};

            /**
             * Retrieves event data from the Firebase database and sends it via SMS.
             * The method gets a reference to the "EventTracker" node in the Firebase database,
             * listens for changes, and sends the event details as an SMS message.
             */
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("EventTracker");
            reference.addValueEventListener(new ValueEventListener() {
                /**
                 * Called when data at the "EventTracker" location is updated.
                 * Iterates through the children of the dataSnapshot, retrieves event details,
                 * and appends them to the message. Sends the message via SMS.
                 *
                 * @param dataSnapshot The snapshot of the data at the "EventTracker" location.
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        String title = snapshot.child("title").getValue(String.class);
                        String date = snapshot.child("date").getValue(String.class);
                        String time = snapshot.child("time").getValue(String.class);
                        message[0] += title + ": " + date + "\n";
                    }
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message[0], null, null);
                }

                /**
                 * Called when the read operation is canceled.
                 * Logs the error code to the console.
                 *
                 * @param databaseError The error that caused the read to fail.
                 */
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }

    /**
     * Displays a custom dialog to request SMS permissions from the user.
     * The dialog includes buttons to approve or deny the permission request.
     * If the user approves, the app will request the SEND_SMS permission.
     */
    private void showCustomPermissionScreen() {
        final Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.request_permission_layout);
        // Find approve/deny buttons in layout
        Button btnApprove = dialog.findViewById(R.id.grantPermissionButton);
        TextView btnDeny = dialog.findViewById(R.id.denyPermissionButton);

        // Set an onClick listener for the button
        btnApprove.setOnClickListener(v -> {
            dialog.setOnDismissListener(dialogInterface -> {
                // When the dialog is dismissed, request the permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            });
            dialog.dismiss();
        });
        btnDeny.setOnClickListener(v -> {
            // Dismiss the dialog
            dialog.dismiss();
        });
        dialog.show();
    }

    /**
     * Updates the user interface (UI) with a list of events.
     * This method creates a new EventAdapter with the provided list of events,
     * and sets it as the adapter for the RecyclerView.
     *
     * @param eventList The list of events to display in the UI.
     */
    private void updateUI(List<Event> eventList) {
        EventAdapter mEventAdapter = new EventAdapter(eventList);
        mRecyclerView.setAdapter(mEventAdapter);
    }

    /**
     * Adapter class that bridges the data to be displayed in a RecyclerView.
     * This adapter handles a list of Event objects and binds them to the RecyclerView.
     */
    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {

        private final List<Event> mEventList;

        /**
         * Constructor for the EventAdapter.
         *
         * @param events The list of Event objects to be displayed in the RecyclerView.
         */
        public EventAdapter(List<Event> events) {
            mEventList = events;
        }

        /**
         * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
         *
         * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
         * @param viewType The view type of the new View.
         * @return A new EventHolder that holds a View of the given view type.
         */
        @NonNull
        @Override
        public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the layout for the ViewHolder
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            return new EventHolder(layoutInflater, parent);
        }

        /**
         * Called by RecyclerView to display the data at the specified position.
         * This method should update the contents of the ViewHolder to reflect the item at the given position.
         *
         * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position.
         * @param position The position of the item within the adapter's data set.
         */        @Override
        public void onBindViewHolder(EventHolder holder, int position){
            holder.bind(mEventList.get(position));
        }

        // Return number of items in data set
        @Override
        public int getItemCount() {
            return mEventList.size();
        }

        /**
         * ViewHolder class for the RecyclerView that holds references to the views for each item.
         * This class is responsible for initializing the views and handling any specific view-related logic.
         */
        private class EventHolder extends RecyclerView.ViewHolder {

            private final TextView mEventTextView;
            private final TextView mEventDateView;
            private final TextView mEventTimeView;

            /**
             * Constructor for the EventHolder.
             * Initializes the views for each item in the RecyclerView.
             *
             * @param inflater The LayoutInflater used to inflate the item layout.
             * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
             */
            public EventHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.recycler_view_items, parent, false));
                mEventTextView = itemView.findViewById(R.id.event_text_view);
                mEventDateView = itemView.findViewById(R.id.event_date_view);
                mEventTimeView = itemView.findViewById(R.id.event_time_view);
                ImageButton mDeleteButton = itemView.findViewById(R.id.delete_button);

                DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
                    // Add leading zeros to single digits
                    String month = monthOfYear < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                    String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                    String date = month + "/" + day + "/" + year;

                    // Get the position of the current item
                    int position = getAdapterPosition();
                    Event event = mEventList.get(position);
                    // Update event's date with DatePicker
                    event.setEventDate(date);
                    // Get a reference to the specific event node
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("EventTracker").child(event.getId());
                    // Update the event in the database
                    reference.child("date").setValue(date);
                };

                mEventDateView.setOnClickListener(v -> {
                    Calendar calendar = Calendar.getInstance();
                    // Set the current date as the default
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            requireActivity(),
                            dateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));

                    datePickerDialog.show();
                });

                TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minutes) -> {
                    // Format time as string
                    String AM_PM = hourOfDay < 13 ? "AM" : "PM";
                    int hour = hourOfDay < 13 ? hourOfDay : (hourOfDay - 12);
                    String min = minutes < 10 ? ("0" + minutes) : String.valueOf(minutes);
                    String time = hour + ":" + min + AM_PM;

                    // Get position of current item
                    int position = getAdapterPosition();
                    Event event = mEventList.get(position);
                    // Update event's time with TimePicker
                    event.setEventTime(time);
                    // Get a reference to the specific event node
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("EventTracker").child(event.getId());
                    // Update the event in the database
                    reference.child("time").setValue(time);
                };

                mEventTimeView.setOnClickListener(v -> {
                    // Get current time
                    final Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    // Create a new instance of TimePickerDialog and return it
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), timeSetListener, hour, minute, DateFormat.is24HourFormat(getActivity()));
                    timePickerDialog.show();
                });

                mDeleteButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    // Check position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        Event event = mEventList.get(position);
                        mEventList.remove(position);
                        notifyItemRemoved(position);
                        // Delete event from Firebase Realtime Database
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("EventTracker");
                        mDatabase.child(event.getId()).removeValue();
                    }
                });
            }

            // Bind data of event object to views
            public void bind(Event event) {
                mEventTextView.setText(event.getEventTitle());
                mEventDateView.setText(event.getEventDate());
                mEventTimeView.setText(event.getEventTime());
            }
        }
    }
}