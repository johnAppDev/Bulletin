package com.example.bulletin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
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
class Friends : Fragment(R.layout.activity_friends) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var titleList:Array<String>;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_friends, container, false)
    }
    override fun onStart() {
        super.onStart()

/*
        ViewCompat.setOnApplyWindowInsetsListener(getView()?.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        val button = requireView().findViewById<Button>(R.id.submitButton)
       // val backButton = getView()?.findViewById<Button>(R.id.backButton)
       /* backButton.setOnClickListener {
            val intent = Intent(this, Schedule::class.java)
            startActivity(intent)
        }*/
        val friendName = view?.findViewById<EditText>(R.id.username_input)
        button.setOnClickListener {
            lifecycleScope.launch {
                addFriends(friendName!!.text.toString())
            }
        }
        recyclerView = requireView().findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.setHasFixedSize(true)
        dataList = arrayListOf<DataClass>()
        recyclerView.adapter = AdapterClass(dataList)
        lifecycleScope.launch {
            getFriends()
        }


    }
    private fun addFriends(friendName:String) = runBlocking{
        val responseDeferred = async { NetworkManager().serverCaller("addfriend|$userName|$friendName") }
        val response = responseDeferred.await()
        Log.d("addFriend", response)
        lifecycleScope.launch {
            getFriends()
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
    private fun getData(){
        dataList.clear()
        for(i in titleList.indices){
            val dataClass = DataClass(titleList[i])
            dataList.add(dataClass)
        }

        recyclerView.adapter?.notifyDataSetChanged()

    }
}