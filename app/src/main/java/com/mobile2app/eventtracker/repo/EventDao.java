package com.mobile2app.eventtracker.repo;

import androidx.room.*;
import com.mobile2app.eventtracker.model.Event;
import java.util.List;
import androidx.lifecycle.LiveData;

/**
 * Data Access Object (DAO) for performing CRUD operations on events.
 * Uses LiveData to handle lifecycle states.
 *
 * @author Michael Gagujas
 * @since 2024-08-18
 */
@Dao
public interface EventDao {
    // Return LiveData of an event by title
    @Query("SELECT * FROM Event WHERE title = :title")
    LiveData<Event> getEvent(String title);

    // Return LiveData of all events by date
    @Query("SELECT * FROM Event ORDER BY date COLLATE NOCASE")
    LiveData<List<Event>> getEvents();

    // Add an event
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addEvent(Event event);

    // Update an event
    @Update
    void updateEvent(Event event);

    // Delete an event
    @Delete
    void deleteEvent(Event event);
}