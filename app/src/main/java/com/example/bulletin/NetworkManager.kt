package com.example.bulletin

import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.bulletin.Schedule.Companion.events
import com.example.bulletin.Schedule.Companion.userId
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
private const val URL = "100.103.6.83"
class NetworkManager {
    fun Login(userName:String, pass:String, Activity: ComponentActivity) {
        var success = false
        Log.d("Login", "Logging in " + userName)
        Thread {
            try {
                var reading = true;
                val mSocket = Socket(URL, 8000);
                val inputStream = InputStreamReader(mSocket.getInputStream());
                val outputStreamW = OutputStreamWriter(mSocket.getOutputStream());
                val bufferedReader = BufferedReader(inputStream);
                val bufferedWriter = BufferedWriter(outputStreamW);
                bufferedWriter.write("login $userName $pass \n");
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
                        if (response.equals("User logged in")) {
                            success = true;
                            Activity.runOnUiThread(Runnable {
                                // Stuff that updates the UI
                                Log.d("UI", "Updating UI in main thread")
                                val intent = Intent(Activity, Schedule::class.java)
                                Activity.startActivity(intent)
                            })

                        }else{
                            Log.d("Login", "Login failed");
                        }
                        println("exiting loop");
                        break;
                    }
                    i++;
                }
                Log.d("USERLogin", "Logged in $success");
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


                println("$userId")
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
fun Login2(userName:String, pass:String, Activity: ComponentActivity){
    try {
        var reading = true;
        val mSocket = Socket(URL, 8000);
        val inputStream = InputStreamReader(mSocket.getInputStream());
        val outputStreamW = OutputStreamWriter(mSocket.getOutputStream());
        val bufferedReader = BufferedReader(inputStream);
        val bufferedWriter = BufferedWriter(outputStreamW);
        bufferedWriter.write("login $userName $pass \n");
        bufferedWriter.flush();
        bufferedReader.readLine();
    }catch(e:Exception){
        println("Error");
    }
}

}