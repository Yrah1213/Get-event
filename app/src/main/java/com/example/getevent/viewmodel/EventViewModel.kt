package com.example.getevent.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.getevent.data.Event
import com.example.getevent.data.EventDatabase
import com.example.getevent.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EventRepository
    val allEvents: LiveData<List<Event>>

    init {
        val eventDao = EventDatabase.getDatabase(application).eventDao()
        repository = EventRepository(eventDao)
        allEvents = repository.allEvents
    }

    fun getUpcomingEvents(startDate: Date): LiveData<List<Event>> {
        return repository.getUpcomingEvents(startDate)
    }

    fun getEventById(eventId: Long): LiveData<Event> {
        return repository.getEventById(eventId)
    }

    fun insert(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(event)
    }

    fun update(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(event)
    }

    fun delete(event: Event) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(event)
    }
} 