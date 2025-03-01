package com.example.bulletin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button = findViewById<Button>(R.id.signUpButton)
        val homeButton = findViewById<Button>(R.id.homeButton)
        val username = findViewById<View>(R.id.username_input) as EditText
        val password = findViewById<View>(R.id.password_input) as EditText
        val email = findViewById<View>(R.id.email_input) as EditText
        button.setOnClickListener {
            CreateUser(email.text.toString(), username.text.toString(), password.text.toString(), this@SignUp)
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
    fun CreateUser(email:String, userName:String, pass:String, Activity: ComponentActivity) {
        var success = false
        Log.d("Login", "Logging in " + userName)
        Thread {
            try {
                var reading = true;
                val mSocket = Socket(URL, 22);
                val inputStream = InputStreamReader(mSocket.getInputStream());
                val outputStreamW = OutputStreamWriter(mSocket.getOutputStream());
                val bufferedReader = BufferedReader(inputStream);
                val bufferedWriter = BufferedWriter(outputStreamW);
                bufferedWriter.write("createuser $userName $email $pass \n");
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
                        if (response.equals("User created")) {
                            success = true;
                            Activity.runOnUiThread(Runnable {
                                // Stuff that updates the UI
                                Log.d("UI", "Updating UI in main thread")
                                val intent = Intent(Activity, MainActivity::class.java)
                                Activity.startActivity(intent)
                            })

                        }else{
                            Log.d("SignUp", "Account creation failed");
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
}