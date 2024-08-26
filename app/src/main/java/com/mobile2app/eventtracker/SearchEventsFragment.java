package com.mobile2app.eventtracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 *  * A fragment that provides a search events screen with a logout button.
 *
 * @author Michael Gagujas
 * @since 2024-08-18
 */
public class SearchEventsFragment extends Fragment {

    private Button logout;

    /**
     * Default constructor for the fragment.
     * <p>
     * This constructor is required and should be empty.
     * </p>
     */
    public SearchEventsFragment() {
        // empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * <p>
     * This method inflates the layout for this fragment and sets up the logout button.
     * When the logout button is clicked, the user is signed out, a toast message is displayed,
     * and the user is redirected to the login activity.
     * </p>
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_events, container, false);

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "Logged Out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });


        return view;
    }
}