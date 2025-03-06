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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
            lifecycleScope.launch(Dispatchers.IO) {
                CreateUser(
                    email.text.toString(),
                    username.text.toString(),
                    password.text.toString(),
                    this@SignUp
                )
            }
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
    private fun CreateUser(email:String, userName:String, pass:String, Activity: ComponentActivity) = runBlocking {
        Log.d("AccountCreation", "Account creation attempted by $userName with $pass")
        val responseDeferred = async{ NetworkManager().serverCaller("createuser $userName $email $pass")}
        val response = responseDeferred.await()
        Log.d("AccountCreation", "Server responded with: $response")
        if(response == "User created"){
            Activity.runOnUiThread(Runnable {
                // Stuff that updates the UI
                Log.d("UI", "Updating UI in main thread")
                val intent = Intent(Activity, MainActivity::class.java)
                Activity.startActivity(intent)
            })
        }
    }
}