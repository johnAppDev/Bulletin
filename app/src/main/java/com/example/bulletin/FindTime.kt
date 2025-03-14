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

                    if(jsonObjectArray.getJSONArray(i).length() == 0){
                        Log.d("JsonDays", "Day $i is clear")
                    }else{
                        Log.d("JsonDays","Day $i is kinda busy")
                        for(j in 0 until jsonObjectArray.getJSONArray(i).length()){
                            Log.d("JsonDays", "On $i we have ${jsonObjectArray.getJSONArray(i).getJSONArray(j)}")
                        }
                    }
                }
                Log.d("Busy time", response)
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