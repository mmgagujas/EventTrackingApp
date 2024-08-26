package com.mobile2app.eventtracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Activity for creating events and providing details like title,
 * date, and time. Allows users to update an events date and time with
 * DatePicker and TimePicker widgets.
 *
 * @author Michael Gagujas
 * @since 2024-08-18
 */
public class EventCreateActivity extends AppCompatActivity {

    private EditText mEventTitle;
    private TextView mEventDate;
    private TextView mEventTime;

    private Button mCreateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_create);

        // Adjust layout based on system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.event_create_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mEventTitle = findViewById(R.id.event_title);
        mEventDate = findViewById(R.id.event_date);
        mEventTime = findViewById(R.id.event_time);
        mCreateButton = findViewById(R.id.create_button);


        // Listener for date picker dialog to display the month/date of an event
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            String month = monthOfYear < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
            String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
            String date = month + "/" + day + "/" + year;
            mEventDate.setText(date);
        };
        mEventDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            // Create a new date picker dialog with the current date as the default date
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EventCreateActivity.this,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Listener for time picker dialog to display the time of an event
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minutes) -> {
            String AM_PM = hourOfDay < 13 ? "AM" : "PM";
            int hour = hourOfDay < 13 ? hourOfDay : (hourOfDay - 12);
            String min = minutes < 10 ? ("0" + minutes) : String.valueOf(minutes);
            String time = hour + ":" + min + AM_PM;
            mEventTime.setText(time);
        };

        mEventTime.setOnClickListener(v -> {
            // Get current time
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            TimePickerDialog timePickerDialog = new TimePickerDialog(EventCreateActivity.this, timeSetListener, hour, minute, DateFormat.is24HourFormat(EventCreateActivity.this));
            timePickerDialog.show();
        });


        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_title = mEventTitle.getText().toString();
                String txt_date = mEventDate.getText().toString();
                String txt_time = mEventTime.getText().toString();
                if(txt_title.isEmpty() || txt_date.isEmpty() || txt_time.isEmpty()) {
                    Toast.makeText(EventCreateActivity.this, "Data missing!", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("title", txt_title);
                    map.put("date", txt_date);
                    map.put("time", txt_time);
                    DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("EventTracker").push();
                    String eventId = eventRef.push().getKey(); // This will create a unique ID for your event
                    eventRef.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(EventCreateActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                    Toast.makeText(EventCreateActivity.this, "Event Created!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}