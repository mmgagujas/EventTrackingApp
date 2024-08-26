package com.mobile2app.eventtracker.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Event object that holds an id, title, time, and date. Uses
 * Room Persistence Library to interact with SQLite table
 *
 * @author Michael gagujas
 * @since 2024-08-18
 */
@Entity
public class Event {

    // Real-time Database makes unique key from time
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    private String mId;
    @NonNull
    @ColumnInfo(name = "title")
    private String mEventTitle;
    @ColumnInfo(name = "time")
    private String mEventTime;
    @ColumnInfo(name = "date")
    private String mEventDate;

    // Constructor for Room
    public Event(){
    }

    // Constructor assigning title, date, and time
    public Event(String id, @NonNull String title, String date, String time) {
        this.mId = id;
        mEventTitle = title;
        mEventDate = date;
        mEventTime = time;
    }

    // Get the Id of an event
    public String getId() {
        return mId;
    }
    // Set the Id of an event
    public void setId(String id) {
        mId = id;
    }

    // Get the title of an event
    public String getEventTitle() { return mEventTitle; }
    // Set the title of an event
    public void setEventTitle(String title) {
        mEventTitle = title;
    }

    // Get the date of an event
    public String getEventDate() { return mEventDate; }
    // Set the date of an event
    public void setEventDate(String date) {
        mEventDate = date;
    }

    // Get the time of an event
    public String getEventTime() { return mEventTime; }
    // Set the time of an event
    public void setEventTime(String time) {
        mEventTime = time;
    }
}