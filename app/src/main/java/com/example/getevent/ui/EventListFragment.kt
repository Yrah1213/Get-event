package com.example.getevent.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.getevent.R
import com.example.getevent.data.Event
import com.example.getevent.databinding.FragmentEventListBinding
import com.example.getevent.viewmodel.EventViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class EventListFragment : Fragment() {
    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels()
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        observeEvents()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter { event ->
            showEventOptions(event)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@EventListFragment.adapter
        }
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            val action = EventListFragmentDirections.actionEventListToEventForm(-1L)
            findNavController().navigate(action)
        }
    }

    private fun observeEvents() {
        viewModel.allEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }
    }

    private fun showEventOptions(event: Event) {
        val options = arrayOf(
            getString(R.string.edit_event),
            getString(R.string.delete_event)
        )

        MaterialAlertDialogBuilder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editEvent(event)
                    1 -> confirmDelete(event)
                }
            }
            .show()
    }

    private fun editEvent(event: Event) {
        val action = EventListFragmentDirections.actionEventListToEventForm(event.id)
        findNavController().navigate(action)
    }

    private fun confirmDelete(event: Event) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.confirm_delete)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.delete(event)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 