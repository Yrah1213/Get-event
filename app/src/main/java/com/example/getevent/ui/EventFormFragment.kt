package com.example.getevent.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.getevent.R
import com.example.getevent.data.Event
import com.example.getevent.databinding.FragmentEventFormBinding
import com.example.getevent.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

class EventFormFragment : Fragment() {
    private var _binding: FragmentEventFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels()
    private val args: EventFormFragmentArgs by navArgs()
    private var selectedDate: Date = Date()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.eventId != -1L) {
            viewModel.getEventById(args.eventId).observe(viewLifecycleOwner) { event ->
                event?.let { populateForm(it) }
            }
        }

        setupDatePicker()
        setupSaveButton()
    }

    private fun setupDatePicker() {
        binding.editTextDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)

                    TimePickerDialog(
                        requireContext(),
                        { _, hour, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)
                            selectedDate = calendar.time
                            updateDateDisplay()
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            if (validateForm()) {
                val event = Event(
                    id = if (args.eventId == -1L) 0 else args.eventId,
                    title = binding.editTextTitle.text.toString(),
                    description = binding.editTextDescription.text.toString(),
                    date = selectedDate,
                    location = binding.editTextLocation.text.toString(),
                    organizer = binding.editTextOrganizer.text.toString()
                )

                if (args.eventId != -1L) {
                    viewModel.update(event)
                } else {
                    viewModel.insert(event)
                }

                Toast.makeText(context, R.string.event_saved, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(context, R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateForm(event: Event) {
        binding.editTextTitle.setText(event.title)
        binding.editTextDescription.setText(event.description)
        binding.editTextLocation.setText(event.location)
        binding.editTextOrganizer.setText(event.organizer)
        selectedDate = event.date
        updateDateDisplay()
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        binding.editTextDate.setText(dateFormat.format(selectedDate))
    }

    private fun validateForm(): Boolean {
        return binding.editTextTitle.text?.isNotBlank() == true &&
                binding.editTextDescription.text?.isNotBlank() == true &&
                binding.editTextLocation.text?.isNotBlank() == true &&
                binding.editTextOrganizer.text?.isNotBlank() == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 