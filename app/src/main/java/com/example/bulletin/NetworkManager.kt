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
   suspend fun serverCaller(call: String): String {
       var response: String = "";
            try {
                val mSocket = Socket(URL, 8000);
                val inputStream = InputStreamReader(mSocket.getInputStream());
                val outputStreamW = OutputStreamWriter(mSocket.getOutputStream());
                val bufferedReader = BufferedReader(inputStream);
                val bufferedWriter = BufferedWriter(outputStreamW);

                bufferedWriter.write(call + "\n");
                bufferedWriter.flush();
                Log.d("Process", "Process started");
                var i = 0;
                while (true) {
                    println("Waiting for response; currently reading $i");
                    if (bufferedReader.ready()) {
                        println("Response ready");
                        response = bufferedReader.readLine()
                        Log.d("Response", " $response");
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
        return response;

    }
}
