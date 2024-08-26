package com.mobile2app.eventtracker.repo;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.mobile2app.eventtracker.model.Event;

/**
 * Identifies the class as a Room database and lists the tables
 * within it. Provides access points to interact with the Event table.
 *
 * @author Michael Gagujas
 * @since 2024-08-18
 */
@Database(entities = {Event.class}, version = 1)
public abstract class EventDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
}