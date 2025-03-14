package com.example.bulletin

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class FindTime : AppCompatActivity() {
    private lateinit var beforeFriendsRecyclerView: RecyclerView
    private lateinit var afterFriendsRecyclerView: RecyclerView
    private lateinit var possibleEventsRecyclerView: RecyclerView
    private var beforeFriends: MutableList<DataClass> = arrayListOf()
    private var afterFriends: MutableList<DataClass> = arrayListOf()
    private lateinit var titleList:Array<String>;
    private val userName = MainActivity.user;
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
        beforeFriendsRecyclerView = findViewById<RecyclerView>(R.id.friendsBefore)
        afterFriendsRecyclerView = findViewById<RecyclerView>(R.id.friendsAfter)
        possibleEventsRecyclerView = findViewById<RecyclerView>(R.id.possibleEvents)
        beforeFriendsRecyclerView.layoutManager = GridLayoutManager(this, 5)
        val beforeFriendsAdapter = AdapterClass(beforeFriends)
        beforeFriendsRecyclerView.adapter = beforeFriendsAdapter
        getFriends()



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
    private fun getData(){
        beforeFriends.clear()
        for(i in titleList.indices){
            val dataClass = DataClass(titleList[i])
            beforeFriends.add(dataClass)
        }

        beforeFriendsRecyclerView.adapter?.notifyDataSetChanged()

    }
}