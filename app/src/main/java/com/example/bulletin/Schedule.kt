package com.example.bulletin

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.json.JSONObject

private const val URL = "100.103.6.83"


class Schedule : AppCompatActivity() {
    companion object{
        var userId: String? = null;
        var events: MutableList<String> = arrayListOf()
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var dateItems:MutableList<DateItem>
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_schedule)
        Log.d("Setting View", "Schedule")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val createEventFragment = CreateEvent()
        val friendsFragment = Friends()
        val friendButton = findViewById<Button>(R.id.friendButton)
        val eventButton = findViewById<Button>(R.id.eventButton)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, friendsFragment)
            commit()
        }
        friendButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, friendsFragment)
                commit()
            }
        }
        eventButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, createEventFragment)
                commit()
            }
        }

        Log.d("Schedule", "Starting")
        Log.d("Schedule", "User: " + MainActivity.user);
        dateItems = ArrayList()
        val adapter = CalendarAdapter(dateItems)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        lifecycleScope.launch(Dispatchers.IO) {
            getUserId()
            getEvents(this@Schedule)
        }
       initializeCalendar()
        recyclerView.layoutManager = GridLayoutManager(this, 7)
        val calendarAdapter = CalendarAdapter(dateItems)
        recyclerView.adapter = calendarAdapter
       calendarAdapter.setOnClickListener(object : CalendarAdapter.OnClickListener {
            override fun onClick(position: Int, model: DateItem) {
               Log.d("Schedule", "Clicked on item at $position")
               Log.d("Schedule", "Item has a description of: ${dateItems[position].eventInfo}")
            }
        })

    }
    private fun initializeCalendar(){
        for(nums in 0..30){
            var day = DateItem(nums.toString(),"Event:$nums")

            dateItems.add(day)
        }
    }
    private fun getUserId() = runBlocking{
        val responseDeferred = async { NetworkManager().serverCaller("getuserinfo|$user") }
        val response = responseDeferred.await()
        Log.d("EventAttempt", "Server responded with: $response")
        if (response != "User not found") {

            userId = response.split(",")[0];

        }
    }




    private fun getEvents(Activity: ComponentActivity)= runBlocking{
        Log.d("Schedule", "Getting events")
        if (userId != null) {
            val responseDeferred =
                async { NetworkManager().serverCaller("getmonthevents|$userId|2021|2") }
            val response = responseDeferred.await()
            Log.d("EventAttempt", "Server responded with: $response")
            if (response != "No events found" && response != "Unknown Command") {
                val jsonObject = JSONObject("""{"Events": $response }""")
                val jsonObjectArray = jsonObject.getJSONArray("Events");
                for (i in 0 until(jsonObjectArray.length())){
                    val date = jsonObjectArray.getJSONObject(i).getString("date")
                    val datePieces = date.split("/")
                    val index = datePieces[datePieces.size-1].toInt()

                    val title = jsonObjectArray.getJSONObject(i).getString("title")
                    Log.d("JSON Object Title", title)
                    dateItems[index].eventInfo = title
                }
                /*
                val words = response.split(",")
                Log.d("Words", words.size.toString())
                var index = 0;
                Log.d("Schedule Response", response)
                for (i in 0..words.size) {
                  //  events.add( ",") //${words[i]}
                    if (i % 6 == 0) {
                        var day = DateItem(index.toString(),"Event:$i")
                        dateItems.add(day)
                        index++
                    }
                }*/

            }
        }

    }
}