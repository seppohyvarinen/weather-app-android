package fi.tuni.weather_app_kotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.gms.location.LocationServices
import fi.tuni.weather_app_kotlin.MainActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.math.roundToInt


/*
This activity is launched when user clicks "Get Forecast" button in the MainActivity.
This class handles fetching the forecast and showing the forecast to the user as ListView.
 */
class ForecastActivity : AppCompatActivity() {

    lateinit var listView: ListView

    // Called when the activity is created. Here the app checks if there are extras passed in the Intent object.
    // The extras should contain latitude and longitude information by which the forecast is then fetched.
    // onCreate calls the downloadUrlAsync function which accepts a Unit returning lambda, where the received
    // json is then mapped and displayed in the UI. The app uses a customized adapter with the ListView.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        listView = findViewById(R.id.listView)


        var extras: Bundle? = intent.extras



        if (extras != null) {
            val lat: String? = extras.getString("latitude")
            val lon: String? = extras.getString("longitude")
            if (lat != null && lon != null) {
                downloadUrlAsync(
                    this,
                    "https://api.openweathermap.org/data/2.5/forecast?lat=${lat}&lon=${lon}&units=metric&cnt=7&appid=bc2d40bf4e1d09c80f0383a56d873af0"
                ) {
                    if (it != null) {

                        val mp = ObjectMapper()
                        val myObject: ForecastJsonObject = mp.readValue(
                            it,
                            ForecastJsonObject::class.java
                        )
                        val data: MutableList<ForecastListObj>? = myObject.list


                        var adapter = CustomAdapter(this, data)
                        listView.setAdapter(adapter);



                    }


                }
            }


        }

    }
    // This function asynchronously fetches data from API. It creates a new thread and inside it calls
    // getUrl method. The Unit returning function called "function" is then called in the UI thread.
    // it is used in the onCreate function as lambda.
    private fun downloadUrlAsync(a: Activity, s: String, function: (l: String?) -> Unit) {
        thread() {
            val lines: String? = getUrl(s)

            a.runOnUiThread() {
                function(lines)
            }
        }

    }

    // This function creates the HttpURLConnection and does the fetching from the API.
    // The function returns a String constructed with buildString that contains the json data from API

    fun getUrl(url: String): String? {

        val myUrl = URL(url)
        val conn = myUrl.openConnection() as HttpURLConnection

        if (conn.responseCode != 404) {
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
        } else {

            return null
        }


    }
    override fun onBackPressed() {
        // Send name = jaska back to first activity
        val intent = Intent()
        var fetched : Boolean = true
        intent.putExtra("fetched", fetched)
        setResult(RESULT_OK, intent);
        super.onBackPressed()
    }
}