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
                NetworkManager().Login(user!!, password.text.toString(), this@MainActivity)
                //Login(username.text.toString(), password.text.toString(), this@MainActivity)

            }
            signUpButton.setOnClickListener {
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
            }
    }
}


