package com.example.bulletin

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.time.LocalDate
@RequiresApi(Build.VERSION_CODES.O)
class FindTime : AppCompatActivity() {
    private lateinit var beforeFriendsRecyclerView: RecyclerView
    private lateinit var afterFriendsRecyclerView: RecyclerView
    private lateinit var possibleEventsRecyclerView: RecyclerView
    private lateinit var monthText: TextView
    private var beforeFriends: MutableList<DataClass> = arrayListOf()
    private var afterFriends: MutableList<DataClass> = arrayListOf()
    private lateinit var titleList:Array<String>;
    private val userName = MainActivity.user;
    private var currentMonth = LocalDate.now().month.value
    private var currentYear = LocalDate.now().year
    private lateinit var possibleEventsAdapter: PossibleEventAdapter
    var possibleDays:MutableList<EventItem> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find_time)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Start of My Code:
        val monthLeftButton = findViewById<Button>(R.id.monthLeft)
        val monthRightButton = findViewById<Button>(R.id.monthRight)
        val calculateEventsButton = findViewById<Button>(R.id.calculateEventsButton)
        monthText = findViewById<TextView>(R.id.monthText)
        monthText.text = LocalDate.of(LocalDate.now().year,currentMonth, 1 ).month.toString()
        beforeFriendsRecyclerView = findViewById<RecyclerView>(R.id.friendsBefore)
        afterFriendsRecyclerView = findViewById<RecyclerView>(R.id.friendsAfter)
        possibleEventsRecyclerView = findViewById<RecyclerView>(R.id.possibleEvents)
        afterFriendsRecyclerView.layoutManager = GridLayoutManager(this, 5)
        beforeFriendsRecyclerView.layoutManager = GridLayoutManager(this, 5)
        val afterFriendsAdapter = AdapterClass(afterFriends)
        val beforeFriendsAdapter = AdapterClass(beforeFriends)
        afterFriendsRecyclerView.adapter = afterFriendsAdapter
        beforeFriendsRecyclerView.adapter = beforeFriendsAdapter
        possibleEventsRecyclerView.layoutManager = LinearLayoutManager(this)
        possibleEventsAdapter = PossibleEventAdapter(possibleDays)
        possibleEventsRecyclerView.adapter = possibleEventsAdapter


        lifecycleScope.launch(Dispatchers.IO) {
            getFriends()
        }

        //Listen to see what friends are added
        beforeFriendsAdapter.setOnClickListener(object : AdapterClass.OnClickListener{
            override fun onClick(position: Int, model: DataClass) {
                addFriend(position, model)
            }
        })
        calculateEventsButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                findAvailableTime()
            }
        }
        monthLeftButton.setOnClickListener{
            if(currentMonth > 1 ){
                currentMonth -= 1
            }else{
                currentYear -= 1
                currentMonth = 12
            }
            monthText.text = LocalDate.of(LocalDate.now().year,currentMonth, 1 ).month.toString()
        }
        monthRightButton.setOnClickListener{
            if(currentMonth < 12 ){
                currentMonth += 1
            }else{
                currentYear += 1
                currentMonth = 1
            }
            monthText.text = LocalDate.of(LocalDate.now().year,currentMonth, 1 ).month.toString()
        }


    }
    fun findAvailableTime() = runBlocking{

            for(friend in afterFriends){
                val responseDeferred =
                    async { NetworkManager().serverCaller("getbusytimeinmonth|3|${friend.dataTitle}|$currentYear|$currentMonth ") }
                val response = responseDeferred.await()
                val jsonObject = JSONObject("""{"busy": $response}""")
                val jsonObjectArray= jsonObject.getJSONArray("busy")

                for ( i in 0 until jsonObjectArray.length()){
                    Log.d("JSON Object", "$i : ${jsonObjectArray.getJSONArray(i)}")
                    val friends:MutableList<String> = arrayListOf()
                    if(jsonObjectArray.getJSONArray(i).length() == 0){
                        Log.d("JsonDays", "Day $i is clear")

                        friends.add("${friend.dataTitle}")
                        possibleDays.add(EventItem("", "$currentYear/$currentMonth/$i", "0","2400", "", "", friends, "", "temp"))
                    }else{
                        Log.d("JsonDays","Day $i is kinda busy")
                        for(j in 0 until jsonObjectArray.getJSONArray(i).length()){
                            val tempArray = jsonObjectArray.getJSONArray(i)
                            Log.d("JsonDays", "On $i we have ${tempArray.getJSONArray(j)}")
                            Log.d("JsonDays", "On $i we have ${tempArray.getJSONArray(j)[1]} and temp array has a length of ${tempArray.getJSONArray(j).length()}")
                           /* if(tempArray.getJSONArray(j)[0].toString().toInt() > 0){
                                possibleDays.add(EventItem("", "$currentYear/$currentMonth/$i", "0","${tempArray.getJSONArray(j)[0]}", "", "", friends, "", "temp"))
                            }else if (tempArray.length() > 1){
                                possibleDays.add(EventItem("", "$currentYear/$currentMonth/$i", "","2400", "", "", friends, "", "temp"))
                            }*/
                            if( j==0){
                                if(tempArray.getJSONArray(j)[0].toString().toInt() > 0) { // if it is on the first of a set of arrays and the first value is greater than 0
                                    possibleDays.add(
                                        EventItem(
                                            "",
                                            "$currentYear/$currentMonth/$i",
                                            "0", //start at the smallest time
                                            "${tempArray.getJSONArray(j)[0]}",// end at the first index
                                            "",
                                            "",
                                            friends,
                                            "",
                                            "temp"
                                        )
                                    )

                                }
                                if (tempArray.length() == 1 && tempArray.getJSONArray(j)[1].toString().toInt() < 2400)
                                {
                                    possibleDays.add(
                                        EventItem(
                                            "",
                                            "$currentYear/$currentMonth/$i",
                                            "${tempArray.getJSONArray(j)[1]}", //start at the smallest time
                                            "2400",// end at the first index
                                            "",
                                            "",
                                            friends,
                                            "",
                                            "temp"
                                        )
                                    )

                                }
                            }else if(j < tempArray.length() -1){
                                possibleDays.add(EventItem("", "$currentYear/$currentMonth/$i", "${tempArray.getJSONArray(j-1)[1]}","${tempArray.getJSONArray(j)[0]}", "", "", friends, "", "temp"))
                            }else if (tempArray.getJSONArray(j)[1].toString().toInt() < 2400){
                                Log.d("JsonDays", "On $i we have ${tempArray.getJSONArray(j)[1]} with index j of $j")
                                possibleDays.add(EventItem("", "$currentYear/$currentMonth/$i", "${tempArray.getJSONArray(j-1)[1]}","${tempArray.getJSONArray(j)[0]}", "", "", friends, "", "temp"))
                                possibleDays.add(EventItem("", "$currentYear/$currentMonth/$i", "${tempArray.getJSONArray(j)[1]}","2400", "", "", friends, "", "temp"))
                            }
                        }
                    }
                }
                Log.d("Busy time", response)
            }
       possibleEventsRecyclerView.adapter?.notifyDataSetChanged()
        for(possibleDay in possibleDays) {
            Log.d("PossibleDays","Start: ${possibleDay.startTime}, End: ${possibleDay.endTime}")
        }
    }
    private fun getFriends() = runBlocking{
        val responseDeferred = async { NetworkManager().serverCaller("getuserinfo|$userName ") }
        val response = responseDeferred.await()
        Log.d("addFriend", response)
        if (response != "User not found") {
            val words = response.split("(", ")")
            println(words[1])
            val friends = words[1].split(",")
            titleList = friends.toTypedArray()
            getData()
        }else{
            Log.d("Friend", "Friend Acquisition failed");
        }
    }
    private fun addFriend(position: Int, model: DataClass){
        afterFriends.add(model)
        beforeFriends.removeAt(position)
        beforeFriendsRecyclerView.adapter?.notifyDataSetChanged()
        afterFriendsRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun getData(){
        beforeFriends.clear()
        for(i in titleList.indices){
            val dataClass = DataClass(titleList[i])
            beforeFriends.add(dataClass)
        }

        beforeFriendsRecyclerView.adapter?.notifyDataSetChanged()

    }
}