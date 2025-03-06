package com.example.bulletin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bulletin.Schedule.Companion.userId
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
val userName = MainActivity.user;
class Friends : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var titleList:Array<String>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_friends)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button = findViewById<Button>(R.id.submitButton)
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, Schedule::class.java)
            startActivity(intent)
        }
        val friendName = findViewById<EditText>(R.id.username_input)
        button.setOnClickListener {
            lifecycleScope.launch {
                addFriends(friendName.text.toString())
            }
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        dataList = arrayListOf<DataClass>()
        lifecycleScope.launch {
            getFriends()
        }


    }
    private fun addFriends(friendName:String) = runBlocking{
        val responseDeferred = async { NetworkManager().serverCaller("addfriend $userName $friendName") }
        val response = responseDeferred.await()
        Log.d("addFriend", response)
        lifecycleScope.launch {
            getFriends()
        }
    }
    private fun getFriends() = runBlocking{
        val responseDeferred = async { NetworkManager().serverCaller("getuserinfo $userName ") }
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
        for(i in titleList.indices){
            val dataClass = DataClass(titleList[i])
            dataList.add(dataClass)
        }
        recyclerView.adapter = AdapterClass(dataList)
    }
}