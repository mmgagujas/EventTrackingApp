package com.mobile2app.eventtracker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Primary Activity with a navigation bar for user events, search events,
 * and settings fragment. Contains a floating action bar to transition to
 * event create activity.
 *
 * @author Michael Gagujas
 * @since 2024-08-18
 */
public class MainActivity extends AppCompatActivity {
    private GetGoogleIdOption googleIdOption;

    /**
     * Called when the activity is starting.
     * <p>
     * This method sets up the layout for this activity, initializes the Floating Action Button (FAB)
     * and the bottom navigation menu, and configures the navigation controller.
     * </p>
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FAB and nav menu
        FloatingActionButton mFloatingButton = findViewById(R.id.floatingActionButton);
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        // Get the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        // Get the NavController from the NavHostFragment
        NavController navController = null;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Setup the BottomNavigationView with the NavController
        if (navController != null) {
            NavigationUI.setupWithNavController(navView, navController);
        }

        // Add the destination changed listener
        if (navController != null) {
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Shows FAB on user's events screen
                if(destination.getId() == R.id.nav_user_events) {
                    mFloatingButton.show();
                } else {
                    mFloatingButton.hide();
                }
            });
        }

        // Switch screens to event create activity on FAB click
        mFloatingButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EventCreateActivity.class);
            startActivity(intent);
        });

    }
}