package com.example.bulletin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
private const val URL = "100.103.6.83"
class CreateEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_event)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button = findViewById<Button>(R.id.submitButton)
        val title = findViewById<View>(R.id.eventTitle) as EditText
        val date = findViewById<View>(R.id.date) as EditText
        val startTime = findViewById<View>(R.id.startTime) as EditText
        val endTime = findViewById<View>(R.id.endTime) as EditText
        val checkBox = findViewById<View>(R.id.checkBox) as CheckBox
        val invitees = findViewById<View>(R.id.invitees) as EditText

        button.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
            createEvent(title.text.toString(),date.text.toString(),startTime.text.toString(),endTime.text.toString(), checkBox.isChecked,invitees.text.toString(), this@CreateEvent)
            }

        }

    }

    private fun createEvent(title:String, date:String, startTime:String, endTime:String, publicity:Boolean, invitees:String, Activity:AppCompatActivity) = runBlocking{
        Log.d("EventCreation", "EventCreation attempted ")
        val responseDeferred = async{ NetworkManager().serverCaller("createevent ${Schedule.userId} $title $date $startTime $endTime ${if(publicity) "FriendsOnly" else "Private"} $invitees")}
        val response = responseDeferred.await()
        Log.d("EventCreation", "Server responded with: $response")
        if (response == "Event created") {
            val intent = Intent(Activity, Schedule::class.java)
            Activity.startActivity(intent)
        }

    }
}