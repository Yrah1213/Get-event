package com.example.getevent.repository

import androidx.lifecycle.LiveData
import com.example.getevent.data.Event
import com.example.getevent.data.EventDao
import java.util.Date

class EventRepository(private val eventDao: EventDao) {
    val allEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    fun getUpcomingEvents(startDate: Date): LiveData<List<Event>> {
        return eventDao.getUpcomingEvents(startDate)
    }

    fun getEventById(eventId: Long): LiveData<Event> {
        return eventDao.getEventById(eventId)
    }

    suspend fun insert(event: Event) {
        eventDao.insertEvent(event)
    }

    suspend fun update(event: Event) {
        eventDao.updateEvent(event)
    }

    suspend fun delete(event: Event) {
        eventDao.deleteEvent(event)
    }
} 