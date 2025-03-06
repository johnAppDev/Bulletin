package com.example.bulletin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
private const val URL = "100.103.6.83"

class Schedule : AppCompatActivity() {
    companion object{
        var userId: String? = null;
        var events: ArrayList<String> = arrayListOf()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val friendButton = findViewById<Button>(R.id.friendButton)
        val eventButton = findViewById<Button>(R.id.eventButton)
        friendButton.setOnClickListener {
            val intent = Intent(this, Friends::class.java)
            startActivity(intent)
        }
        eventButton.setOnClickListener {
            val intent = Intent(this, CreateEvent::class.java)
            startActivity(intent)
        }

        Log.d("Schedule", "Starting")
        Log.d("Schedule", "User: " + MainActivity.user);
        lifecycleScope.launch(Dispatchers.IO) {
            getUserId()
            getEvents(this@Schedule)
        }

    }

    private fun getUserId() = runBlocking{
        val responseDeferred = async { NetworkManager().serverCaller("getmonthevents $userId 2021 2") }
        val response = responseDeferred.await()
        Log.d("EventAttempt", "Server responded with: $response")
        if (response != "User not found") {

            userId = response.split(",")[0];

        }
    }

    private fun getEvents(Activity: ComponentActivity)= runBlocking{
        Log.d("Schedule", "Getting events")
        while (userId != null) {
            val responseDeferred =
                async { NetworkManager().serverCaller("getmonthevents $userId 2021 2") }
            val response = responseDeferred.await()
            Log.d("EventAttempt", "Server responded with: $response")
            if (response != "No events found") {
                Activity.runOnUiThread {
                    val title = findViewById<TextView>(R.id.scheduleTitle)
                    val responseText = "Schedule: $response"
                    title.text = responseText;
                }
                val words = response.split(",")
                var index = 0;
                for (i in 0..words.size) {
                    events[index] += ',' + words[i]
                    if (i % 6 == 0) {
                        index++
                    }
                }
            }
        }
    }
}