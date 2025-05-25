package com.example.getevent.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.Date

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE date >= :startDate ORDER BY date ASC")
    fun getUpcomingEvents(startDate: Date): LiveData<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: Event)

    @Update
    suspend fun updateEvent(event: Event)

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getEventById(eventId: Long): LiveData<Event>
} 