package fi.tuni.weather_app_kotlin

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var url : String = "https://api.openweathermap.org/data/2.5/weather?lat=11&lon=11&units=metric&appid=bc2d40bf4e1d09c80f0383a56d873af0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onResume() {
        super.onResume()
        downloadUrlAsync(this, url) {
            print(it)
        }
    }

    private fun downloadUrlAsync(a: Activity, s: String, function: (l: String?) -> Unit) {
        thread() {
            val lines: String? = getUrl(s)

            a.runOnUiThread() {
                function(lines)
            }
        }

    }

    fun getUrl(url: String): String? {

        val myUrl = URL(url)
        val conn = myUrl.openConnection() as HttpURLConnection
        val reader = BufferedReader(InputStreamReader(conn.getInputStream()));

        return buildString {
            reader.use {
                var line: String? = null
                do {
                    line = it.readLine()
                    append(line)
                } while (line != null)
            }
        }

    }
}