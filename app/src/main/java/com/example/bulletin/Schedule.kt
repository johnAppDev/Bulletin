package com.example.bulletin

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
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
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime


private const val URL = "100.103.6.83"

@RequiresApi(Build.VERSION_CODES.O)
class Schedule : AppCompatActivity() {
    companion object{
        var userId: String? = null;
        var events: MutableList<String> = arrayListOf()
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var dateItems:MutableList<DateItem>
    private var currentMonth = LocalDate.now().month.value
    private var currentYear = LocalDate.now().year
    private var firstDayOfWeekforMonth: Int = 0
    private lateinit var monthText: TextView

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
        val eventViewFragment = EventView()
        val friendButton = findViewById<Button>(R.id.friendButton)
        val eventButton = findViewById<Button>(R.id.eventButton)
        val monthLeft = findViewById<Button>(R.id.monthLeft)
        val monthRight = findViewById<Button>(R.id.monthRight)
        monthText = findViewById<TextView>(R.id.monthText)
        monthText.text = LocalDate.of(LocalDate.now().year,currentMonth, 1 ).month.toString()
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
            getEvents()
        }
       initializeCalendar()
        recyclerView.layoutManager = GridLayoutManager(this, 7)
        val calendarAdapter = CalendarAdapter(dateItems)
        recyclerView.adapter = calendarAdapter
       calendarAdapter.setOnClickListener(object : CalendarAdapter.OnClickListener {
            override fun onClick(position: Int, model: DateItem) {
               Log.d("Schedule", "Clicked on item at $position")
                val eventList = dateItems[position].eventItems
                if (eventList != null) {

                    for (i in 0..<eventList.size) {
                        Log.d("ScheduleEvents", "${eventList[i].title}")
                    }
                }else{
                    Log.d("EventList", "Null")
                }
               Log.d("Schedule", "Item has a description of: ${dateItems[position].eventItems?.get(0)?.title}")
                supportFragmentManager.beginTransaction().apply {
                    if (eventList != null) {
                        EventView.events = eventList
                    }else{
                        EventView.events = arrayListOf()
                    }
                    replace(R.id.flFragment, EventView())
                    commit()

                }
            }
        })

        monthLeft.setOnClickListener{
            if(currentMonth > 1 ){
                currentMonth -= 1
            }else{
                currentYear -= 1
                currentMonth = 12
            }
            monthText.text = LocalDate.of(LocalDate.now().year,currentMonth, 1 ).month.toString()
            initializeCalendar()
            getEvents()
        }
        monthRight.setOnClickListener{
            if(currentMonth < 12 ){
                currentMonth += 1
            }else{
                currentYear += 1
                currentMonth = 1
            }
            monthText.text = LocalDate.of(LocalDate.now().year,currentMonth, 1 ).month.toString()
            initializeCalendar()
            getEvents()
            Log.d("Initialize Calendar", "monthRight")
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeCalendar(){
        firstDayOfWeekforMonth = LocalDate.of(currentYear, currentMonth, 1).dayOfWeek.value
        Log.d("First Day of week for month ", firstDayOfWeekforMonth.toString())
        dateItems.clear()
        for(nums in 1- firstDayOfWeekforMonth..LocalDate.of(currentYear,currentMonth,1).lengthOfMonth()){
            var day: DateItem
            if(nums < 1){
               day = DateItem(null, null)
            }else {
                day = DateItem(nums.toString(), null)
            }

            dateItems.add(day)
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }
    private fun getUserId() = runBlocking {
        val responseDeferred = async { NetworkManager().serverCaller("getuserinfo|$user") }
        val response = responseDeferred.await()
        Log.d("EventAttempt", "Server responded with: $response")
        if (response != "User not found") {

            userId = response.split(",")[0];

        }

    }



    @RequiresApi(Build.VERSION_CODES.O)
    public fun getEvents()= runBlocking{
        Log.d("Schedule", "Getting events")
        if (userId != null) {
            val responseDeferred =
                async { NetworkManager().serverCaller("getmonthevents|$userId|${currentYear}|${currentMonth}") }
            val response = responseDeferred.await()
            Log.d("EventAttempt", "Server responded with: $response")
            if (response != "No events found" && response != "Unknown Command") {
                val jsonObject = JSONObject("""{"Events": $response }""")
                val jsonObjectArray = jsonObject.getJSONArray("Events");
                for (i in 0 until(jsonObjectArray.length())){

                    val title = jsonObjectArray.getJSONObject(i).getString("title")
                    val date = jsonObjectArray.getJSONObject(i).getString("date")
                    val startTime = jsonObjectArray.getJSONObject(i).getString("startTime")
                    val endTime = jsonObjectArray.getJSONObject(i).getString("endTime")
                    val location = jsonObjectArray.getJSONObject(i).getString("location")
                    val publicityType = jsonObjectArray.getJSONObject(i).getString("publicityType")
                    val invitees: List<String> = jsonObjectArray.getJSONObject(i).getString("invitees").split(",")
                    val details = jsonObjectArray.getJSONObject(i).getString("details")
                    val eventId = jsonObjectArray.getJSONObject(i).getString("eventid")
                    val datePieces = date.split("/")
                    val index = datePieces[datePieces.size-1].toInt() - 1 + firstDayOfWeekforMonth
                    val tempEvent=  EventItem(title,date,startTime,endTime,location,publicityType,invitees,details,eventId)

                    Log.d("JSON Object Title", title)
                    if(dateItems[index].eventItems != null){
                        dateItems[index].eventItems?.add(tempEvent)
                    }else{
                        dateItems[index].eventItems = arrayListOf()
                        dateItems[index].eventItems?.add(tempEvent)
                    }

                    Log.d("Calendar Index: " , index.toString())

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