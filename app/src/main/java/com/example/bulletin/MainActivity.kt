package com.example.bulletin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bulletin.databinding.MainlayoutBinding
import com.example.bulletin.ui.theme.BulletinTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket


private const val URL = "100.103.6.83"

private var loggedIn = false;
class MainActivity : ComponentActivity() {

    companion object{
        var user: String? = null;
    }
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.mainlayout);
            val button = findViewById<Button>(R.id.loginButton)
            val signUpButton = findViewById<Button>(R.id.signUpButton)
            val username = findViewById<View>(R.id.username_input) as EditText
            val password = findViewById<View>(R.id.password_input) as EditText
            button.setOnClickListener {
                user = username.text.toString();
                Login(user!!, password.text.toString(), this@MainActivity)
                //Login(username.text.toString(), password.text.toString(), this@MainActivity)

            }
            signUpButton.setOnClickListener {
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
            }
    }
   /* fun Login(userName:String, pass:String, Activity: ComponentActivity) {
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


    }*/
    fun Login(userName:String, pass:String, Activity: ComponentActivity) {
       var response = runBlocking { async{NetworkManager().serverCaller("login $userName $pass")}}
        if(response.equals("User logged in")){
            val intent = Intent(Activity, Schedule::class.java)
            Activity.startActivity(intent)
        }
   }
}


