package com.mobile2app.eventtracker.repo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.mobile2app.eventtracker.model.Event;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton class for handling interactions involving
 * events in a database.
 *
 * @author Michael Gagujas
 * @since 2024-08-18
 */
public class EventRepository {
    private static EventRepository mEventRepo;
    private final EventDao mEventDao;
    private static final int NUMBER_OF_THREADS = 4;
    // Runs database operations in the background
    private static final ExecutorService mDatabaseExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Returns the single instance of EventRepository, creating it if necessary.
     *
     * @param context The application context.
     * @return The singleton instance of EventRepository.
     */
    public static EventRepository getInstance(Context context) {
        if (mEventRepo == null) {
            mEventRepo = new EventRepository(context);
        }
        return mEventRepo;
    }

    /**
     * Private constructor to set up the Room database and initialize the EventDao object.
     *
     * @param context The application context.
     */
    private EventRepository(Context context) {
        RoomDatabase.Callback databaseCallback = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }
        };
        // Build database labeled event.db
        EventDatabase database = Room.databaseBuilder(context, EventDatabase.class, "event.db")
                .addCallback(databaseCallback)
                .build();

        mEventDao = database.eventDao();
    }

    /**
     * Adds an event to the events table in the database.
     *
     * @param event The event to be added.
     */
    public void addEvent(Event event) {
        mDatabaseExecutor.execute(() -> mEventDao.addEvent(event));
    }

    /**
     * Updates an existing event in the events table in the database.
     *
     * @param event The event to be updated.
     */
    public void updateEvent(Event event) {
        mDatabaseExecutor.execute(() -> mEventDao.updateEvent(event));
    }

    /**
     * Deletes an event from the events table in the database.
     *
     * @param event The event to be deleted.
     */
    public void deleteEvent(Event event) {
        mDatabaseExecutor.execute(() -> mEventDao.deleteEvent(event));
    }

    /**
     * Returns a LiveData list of all events in the events table in the database.
     *
     * @return LiveData list of all events.
     */
    public LiveData<List<Event>> getEvents() {
        return mEventDao.getEvents();
    }
}
