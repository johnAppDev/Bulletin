package com.example.bulletin

import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.bulletin.Schedule.Companion.events
import com.example.bulletin.Schedule.Companion.userId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
private const val URL = "10.0.2.2"
class NetworkManager {
    suspend fun serverCaller(call: String): String {

            try {
                var response: String = "Hello";
                withContext(Dispatchers.IO) {
                    Log.d("Trying", "Gonna send something to the server")

                    val mSocket = Socket(URL, 8000);
                    Log.d("Socket", "Got past socket")
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

                }
                return response;
            } catch (e: Exception) {
                e.message.let { Log.d("Oh no", it.toString()) };
                e.printStackTrace()
                return "${e.message}";
            }


    }
}
