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
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
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
        getUserId()

    }
    fun getEvents(Activity: ComponentActivity){
        Log.d("Schedule", "Getting events")
        Thread{
            try{
                println("Getting events")
                val mSocket = Socket(URL, 8000);
                val inputStream = InputStreamReader(mSocket.getInputStream());
                val outputStreamW = OutputStreamWriter(mSocket.getOutputStream());
                val bufferedReader = BufferedReader(inputStream);
                val bufferedWriter = BufferedWriter(outputStreamW);
                while(true){
                    if(userId != null){
                        bufferedWriter.write("getmonthevents $userId 2021 2")
                        bufferedWriter.flush()
                        break;
                    }

                }


                println("${userId}")
                while(true){
                    if(bufferedReader.ready()){
                        val response = bufferedReader.readLine()
                        Log.d("Response", " $response");
                        if(!response.equals("No events found")){
                            Activity.runOnUiThread{
                                val title = findViewById<TextView>(R.id.scheduleTitle)
                                title.text = "Schedule:"+  response;
                            }
                            val words = response.split(",")
                            var index =0;
                            for(i in 0..words.size){
                                events[index] += ','+ words[i]
                                if(i%6 == 0){
                                    index++
                                }
                            }
                            for(i in events){
                                Log.d("Event", i)
                            }
                        }
                    }
                }
                bufferedWriter.flush();
                bufferedReader.close();
                bufferedWriter.close();
                outputStreamW.close();
            }catch(e:Exception) {
                e.message?.let { Log.d("Oh no", it) };
            }
        }.start()
    }
    fun getUserId(){
        var success = false

        Thread {
            try {
                var reading = true;
                val mSocket = Socket(URL, 8000);
                val inputStream = InputStreamReader(mSocket.getInputStream());
                val outputStreamW = OutputStreamWriter(mSocket.getOutputStream());
                val bufferedReader = BufferedReader(inputStream);
                val bufferedWriter = BufferedWriter(outputStreamW);
                bufferedWriter.write("getuserinfo $user");
                bufferedWriter.flush();
                Log.d("Process", "Process started");
                var i = 0;
                while(reading) {

                    println("Waiting for response; currently reading "+i);
                    if (bufferedReader.ready()) {
                        println("Response ready");
                        val response = bufferedReader.readLine()
                        Log.d("Response", " $response");
                        println("Response: " + response);
                        if (!response.equals("User not found")) {
                            success = true;
                            userId = response.split(",")[0];

                        }else{

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
                getEvents(this@Schedule)

            } catch (e: Exception) {
                e.message?.let { Log.d("Oh no", it) };
            }
        }.start();

    }
}