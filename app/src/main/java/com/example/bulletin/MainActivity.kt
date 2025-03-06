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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.bulletin.databinding.MainlayoutBinding
import com.example.bulletin.ui.theme.BulletinTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket


private var loggedIn = false;
class MainActivity : ComponentActivity()  {

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
                lifecycleScope.launch(Dispatchers.IO) {
                    login(user!!, password.text.toString(), this@MainActivity)
                }
                //Login(username.text.toString(), password.text.toString(), this@MainActivity)

            }
            signUpButton.setOnClickListener {
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
            }
    }

    private fun login(userName:String, pass:String, Activity: ComponentActivity) = runBlocking {
       Log.d("LoginAttempt", "Login attempted by $userName with $pass")
       val responseDeferred = async{ NetworkManager().serverCaller("login $userName $pass")}
       val response = responseDeferred.await()
       Log.d("LoginAttempt", "Server responded with: $response")
       if(response == "User logged in"){
            val intent = Intent(Activity, Schedule::class.java)
            Activity.startActivity(intent)
       }

   }
}


