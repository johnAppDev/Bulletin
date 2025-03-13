package com.example.bulletin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



class EventView : Fragment(R.layout.fragment_event_view) {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_view, container, false)
    }

    override fun onStart(){

        super.onStart()
        val events:List<EventItem> = arrayListOf()
        val eventRecycler = requireView().findViewById<RecyclerView>(R.id.eventRecycler) as RecyclerView
        val adapter = EventAdapter(events)
        eventRecycler.layoutManager = LinearLayoutManager(activity)
        eventRecycler.adapter = adapter


    }

}