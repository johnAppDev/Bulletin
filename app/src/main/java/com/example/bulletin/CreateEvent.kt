package com.example.bulletin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

val user = MainActivity.user
private const val URL = "100.103.6.83"
class CreateEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_event)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button = findViewById<Button>(R.id.submitButton)
        val title = findViewById<View>(R.id.eventTitle) as EditText
        val date = findViewById<View>(R.id.date) as EditText
        val startTime = findViewById<View>(R.id.startTime) as EditText
        val endTime = findViewById<View>(R.id.endTime) as EditText
        val checkBox = findViewById<View>(R.id.checkBox) as CheckBox
        val invitees = findViewById<View>(R.id.invitees) as EditText

        button.setOnClickListener {
            createEvent(title.text.toString(),date.text.toString(),startTime.text.toString(),endTime.text.toString(), checkBox.isChecked,invitees.text.toString(), this)
        }

    }

    fun createEvent(title:String, date:String, startTime:String, endTime:String, publicity:Boolean, invitees:String, Activity:AppCompatActivity) {
        var success = false

        Thread {
            try {
                println("UserId: ${Schedule.userId}")
                var reading = true;
                val mSocket = Socket(URL, 22);
                val inputStream = InputStreamReader(mSocket.getInputStream());
                val outputStreamW = OutputStreamWriter(mSocket.getOutputStream());
                val bufferedReader = BufferedReader(inputStream);
                val bufferedWriter = BufferedWriter(outputStreamW);
                bufferedWriter.write("createevent ${Schedule.userId} $title $date $startTime $endTime ${if(publicity) "FriendsOnly" else "Private"} $invitees .\n");
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
                        if (response.equals("Event created")) {
                            success = true;
                            Activity.runOnUiThread {
                                val intent = Intent(this, Schedule::class.java)
                                startActivity(intent)
                            }

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





            } catch (e: Exception) {
                e.message?.let { Log.d("Oh no", it) };
            }
        }.start();


    }
}