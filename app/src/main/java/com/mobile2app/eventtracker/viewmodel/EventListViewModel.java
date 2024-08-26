package com.mobile2app.eventtracker.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mobile2app.eventtracker.model.Event;
import com.mobile2app.eventtracker.repo.EventRepository;
import java.util.List;

/**
 * ViewModel class that prepares and manages the data for the UI related to Event List.
 * This class communicates with the EventRepository to get the data and then provide the UI with the necessary data.
 *
 * @author Michael Gagujas
 * @since 2024-08-18
 */
public class EventListViewModel extends AndroidViewModel {

    private final EventRepository mEventRepo;

    /**
     * Constructor for EventListViewModel. Initializes the EventRepository instance.
     *
     * @param application The application context, used to get the context for the EventRepository.
     */
    public EventListViewModel(Application application) {
        super(application);
        mEventRepo = EventRepository.getInstance(application.getApplicationContext());
    }

    /**
     * Retrieves a LiveData object containing a list of all Event objects.
     *
     * @return A LiveData object containing the list of Event objects.
     */
    public LiveData<List<Event>> getEvents() {
        return mEventRepo.getEvents();
    }

    /**
     * Adds an event to the database.
     *
     * @param event The Event object to be added to the database.
     */
    public void addEvent(Event event) {
        mEventRepo.addEvent(event);
    }


     /**
     * Updates an existing event in the database.
     *
     * @param event The Event object to be updated in the database.
     */
    public void updateEvent(Event event) {
        mEventRepo.updateEvent(event);
    }

    /**
     * Deletes an event from the database.
     *
     * @param event The Event object to be deleted from the database.
     */    public void deleteEvent(Event event) {
        mEventRepo.deleteEvent(event);
    }
}