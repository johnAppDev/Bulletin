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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private val URL = "100.103.6.83"
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
            addFriends(friendName.text.toString())
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        dataList = arrayListOf<DataClass>()
        getFriends()


    }
    private fun addFriends(friendName:String){
        Thread{
            try{
                println("Adding friend")
                val mSocket = Socket(URL, 8000);
                val inputStream = InputStreamReader(mSocket.getInputStream());
                val outputStreamW = OutputStreamWriter(mSocket.getOutputStream());
                val bufferedReader = BufferedReader(inputStream);
                val bufferedWriter = BufferedWriter(outputStreamW);
                bufferedWriter.write("addfriend $userName $friendName")
                bufferedWriter.flush();
                bufferedReader.close();
                bufferedWriter.close();
                outputStreamW.close();
                inputStream.close();
                mSocket.close();
                }catch(e:Exception){
                    e.message?.let { Log.d("Oh no", it) };

                }
        }.start()
    }
    private fun getFriends(){

        Thread {
            try {

                var reading = true;
                val mSocket = Socket(URL, 8000);
                val inputStream = InputStreamReader(mSocket.getInputStream());
                val outputStreamW = OutputStreamWriter(mSocket.getOutputStream());
                val bufferedReader = BufferedReader(inputStream);
                val bufferedWriter = BufferedWriter(outputStreamW);
                bufferedWriter.write("getuserinfo $userName \n");
                bufferedWriter.flush();
                Log.d("Process", "Process started as $userName");
                var i = 0;
                while(reading) {

                    println("Waiting for response; currently reading "+i);
                    if (bufferedReader.ready()) {
                        println("Response ready");
                        val response = bufferedReader.readLine()
                        Log.d("Response", " $response");

                        println("Response: " + response);
                        if (!response.equals("User not found")) {
                            val words = response.split("(", ")")
                            println(words[1])
                            val friends = words[1].split(",")
                            titleList = friends.toTypedArray()
                            getData()


                        }else{
                            Log.d("Friend", "Friend Acquisition failed");
                        }
                        println("exiting loop");
                        break;
                    }
                    i++;
                }

                bufferedReader.close();
                bufferedWriter.close();
                outputStreamW.close();
                inputStream.close();
                mSocket.close();





            } catch (e: Exception) {
                e.message?.let { Log.d("Oh no", it) };
            }
        }.start();


    }
    private fun getData(){
        for(i in titleList.indices){
            val dataClass = DataClass(titleList[i])
            dataList.add(dataClass)
        }
        recyclerView.adapter = AdapterClass(dataList)
    }
}