package fi.tuni.weather_app_kotlin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQ_CODE = 1000;

    private lateinit var myFusedLocationClient: FusedLocationProviderClient
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var alreadyFetched : Boolean = false
    private var url : String = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=metric&appid=bc2d40bf4e1d09c80f0383a56d873af0"
    lateinit var cityName : TextView
    lateinit var temperature : TextView
    lateinit var desc : TextView
    lateinit var searchBar : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize fused location client
        myFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // initialize all TextViews and EditText
        cityName = findViewById<TextView>(R.id.cityName)
        temperature = findViewById<TextView>(R.id.temperature)
        desc = findViewById<TextView>(R.id.description)
        searchBar = findViewById<EditText>(R.id.search_bar)

        if (!alreadyFetched) {
            getCurrentLocation()
            alreadyFetched = true
        }

    }

    private fun getCurrentLocation() {
        // checking location permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);

            return
        }

        myFusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // getting the last known or current location
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                }

                url = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=metric&appid=bc2d40bf4e1d09c80f0383a56d873af0"
                fetchAndUpdateUI()


            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed on getting current location",
                    Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQ_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                } else {
                    // permission denied
                    Toast.makeText(this, "You need to grant permission to access location",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    override fun onResume() {
        super.onResume()


    }

    private fun fetchAndUpdateUI() {
        downloadUrlAsync(this, url) {
            if (it != null) {
                Log.d("ccc", it)
                val mp = ObjectMapper()
                val myObject: WeatherJsonObject = mp.readValue(
                    it,
                    WeatherJsonObject::class.java
                )

                lat = myObject.coord!!.lat
                lon = myObject.coord!!.lon
                val loc: String? = myObject.name
                val mainData : WeatherMain? = myObject.main
                val descObj : MutableList<WeatherDescriptionObject>? = myObject.weather
                Log.d("check", descObj.toString())
                runOnUiThread() {
                    cityName.text = loc.toString()
                    if (mainData != null) {
                        temperature.text = mainData.temp?.roundToInt().toString() + "°C"
                    }
                    if (descObj != null) {
                        desc.text = descObj.get(0).description.toString().replaceFirstChar { it.uppercase() }
                        Log.d("hghg", desc.text.toString())
                    }
                }
            }


        }
    }

    fun getWeather(btn : View) {
        url = "https://api.openweathermap.org/data/2.5/weather?q=${searchBar.text}&units=metric&appid=bc2d40bf4e1d09c80f0383a56d873af0"
        fetchAndUpdateUI()
        hideKeyboard()
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
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
        Log.d("koodi", conn.responseCode.toString())

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
        }  else {

            runOnUiThread(){
                cityName.text = "Can't find this location"
                temperature.text = "-"
                desc.text = "-"
            }
            return null
        }


    }

    fun getForecast(btn: View) {
        val intent = Intent(this, ForecastActivity::class.java)
        intent.putExtra("latitude", lat.toString())
        intent.putExtra("longitude", lon.toString())
        startActivity(intent)
    }


}