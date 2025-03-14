package com.example.bulletin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

val user = MainActivity.user
class CreateEvent : Fragment(R.layout.activity_create_event) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_create_event, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()

      /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        val date = requireView().findViewById<View>(R.id.date) as EditText
        val button = requireView().findViewById<Button>(R.id.submitButton)
        val title = requireView().findViewById<View>(R.id.eventTitle) as EditText
        val startTime = requireView().findViewById<View>(R.id.startTime) as EditText
        val endTime = requireView().findViewById<View>(R.id.endTime) as EditText
        val checkBox = requireView().findViewById<View>(R.id.checkBox) as CheckBox
        val invitees = requireView().findViewById<View>(R.id.invitees) as EditText

        button.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
            createEvent(title.text.toString(),date.text.toString(),startTime.text.toString(),endTime.text.toString(), checkBox.isChecked,invitees.text.toString())
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createEvent(title:String, date:String, startTime:String, endTime:String, publicity:Boolean, invitees:String) = runBlocking{
        Log.d("EventCreation", "EventCreation attempted ")
        val responseDeferred = async{ NetworkManager().serverCaller("createevent|${Schedule.userId}|$title|$date,$date|$startTime|$endTime|${if(publicity) "FriendsOnly" else "Private"}|$invitees")}
        val response = responseDeferred.await()
        Log.d("EventCreation", "Server responded with: $response")
        (activity as Schedule).getEvents()

    }

}