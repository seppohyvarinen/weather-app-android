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
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import kotlin.math.roundToInt

/*
MainActivity class is the default Activity that launches to user.
This activity handles getting the user location, fetching weather by location, fetching weather by city name
and starting the Forecast Activity when user wants to see the forecast.
 */

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQ_CODE = 1000;

    private lateinit var myFusedLocationClient: FusedLocationProviderClient
    private var lat: Double = 0.0
    private var lon: Double = 0.0
    private var url : String = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=metric&appid=bc2d40bf4e1d09c80f0383a56d873af0"
    lateinit var cityName : TextView
    lateinit var temperature : TextView
    lateinit var desc : TextView
    lateinit var searchBar : EditText
    lateinit var wImg : ImageView
    lateinit var forecastBtn : Button
    lateinit var locBtn : Button
    lateinit var feelsLike : TextView
    lateinit var windSpeed : TextView


    // Called when app is created. Initializes UI Components, the fusedproviderclient and checks the savedInstanceState Bundle.
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
        wImg = findViewById<ImageView>(R.id.weatherImage)
        forecastBtn = findViewById<Button>(R.id.Get_forecast)
        feelsLike = findViewById<TextView>(R.id.feels_like)
        windSpeed = findViewById<TextView>(R.id.wSpeed)
        locBtn = findViewById<Button>(R.id.Current_Location)
        locBtn.setOnClickListener {
            getCurrentLocation()
        }
        // If there is data by this key in the savedInstanceState Bundle,
        // the app won't call the getCurrentLocation function, since it
        // would cause undesired fetch when user has searched weather from somewhere else.
        if(savedInstanceState?.getString("city") == null) {
            getCurrentLocation()
        }

    }


    /*

    getCurrentLocation handles getting the device location. It checks whether user has granted permission
    to use location, and if user hasn't it prompts user with request to use location.

    The function uses fusedlocationprovider to achieve this.
    */

    private fun getCurrentLocation() {
        // Check if there's a permission to use location
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Requesting the location permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);

            return
        }

        myFusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // Current or last known location
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


    //This function is called when getWeather function is called and when the user returns from forecast activity
    // to ensure the latest fetched location is re-fetched.
    // This function calls the downloadUrlAsync function, which accepts a Unit returning function as lambda,
    // where the function maps the received json object and updates the UI (in UI thread)
    private fun fetchAndUpdateUI() {
        downloadUrlAsync(this, url) {
            if (it != null) {

                val mp = ObjectMapper()
                val myObject: WeatherJsonObject = mp.readValue(
                    it,
                    WeatherJsonObject::class.java
                )

                lat = myObject.coord!!.lat
                lon = myObject.coord!!.lon
                val loc: String? = myObject.name
                val mainData : WeatherMain? = myObject.main
                val windData : WeatherWindObject? = myObject.wind

                val descObj : MutableList<WeatherDescriptionObject>? = myObject.weather

                runOnUiThread() {
                    cityName.text = loc.toString()
                    if (mainData != null) {
                        temperature.text = mainData.temp?.roundToInt().toString() + "°C"
                        feelsLike.text = "Feels like ${mainData.feels_like?.roundToInt()}°C"
                        windSpeed.text = "${windData?.speed.toString()} m/s"
                    }
                    if (descObj != null) {
                        desc.text = descObj.get(0).description.toString().replaceFirstChar { it.uppercase() }
                        Picasso.get().load("https://openweathermap.org/img/w/" +
                                descObj.get(0).icon +
                                ".png").into(wImg)
                    }
                }
            }


        }
    }

    // This function is called when user presses the "Get Weather" button. It changes the url strings "q=" part to match searchbars
    // text and then calls fetchAndUpdateUI() function to fetch data from API and update the UI. Keyboard is also hidden when
    // button is pressed.
    fun getWeather(btn : View) {
        url = "https://api.openweathermap.org/data/2.5/weather?q=${searchBar.text}&units=metric&appid=bc2d40bf4e1d09c80f0383a56d873af0"
        fetchAndUpdateUI()
        hideKeyboard()
    }

    // Functions for hiding the android keyboard

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

    // This function asynchronously fetches data from API. It creates a new thread and inside it calls
    // getUrl method. The Unit returning function called "function" is then called in the UI thread.
    // it is used in the fetchAndUpdateUI function as lambda.
    private fun downloadUrlAsync(a: Activity, s: String, function: (l: String?) -> Unit) {
        thread() {
            val lines: String? = getUrl(s)

            a.runOnUiThread() {
                function(lines)
            }
        }

    }

    // This function creates the HttpURLConnection and does the fetching from the API.
    // The function has error handling, so if the response code is not 200 ("OK") then
    // the UI is updated with error message. Get Forecast button is also hidden if response code is not 200.

    fun getUrl(url: String): String? {

        val myUrl = URL(url)
        val conn = myUrl.openConnection() as HttpURLConnection

        if (conn.responseCode == 200) {
            val reader = BufferedReader(InputStreamReader(conn.getInputStream()));

            runOnUiThread() {
                forecastBtn.visibility = View.VISIBLE

            }
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
                forecastBtn.visibility = View.INVISIBLE
                cityName.text = "Can't find this location"
                temperature.text = "-"
                desc.text = "-"
                feelsLike.text = "-"
                windSpeed.text = "-"
            }
            return null
        }


    }

    // This function is called when user presses the Get Forecast button.
    // The function creates a new Intent and puts as extra the latitude and longitude information
    // and then starts the Forecast Activity.
    fun getForecast(btn: View) {
        val intent = Intent(this, ForecastActivity::class.java)
        intent.putExtra("latitude", lat.toString())
        intent.putExtra("longitude", lon.toString())
        startActivity(intent)
    }


    // This function is called when the Activity is switched to Forecast Activity
    // Here the app saves city name and the current url so the app can re-fetch the same location
    // when user returns from the Forecast Activity.
    override fun onSaveInstanceState(outState: Bundle) {
        var saveThis = cityName.text
        var saveUrl = url
        outState.putString("city", saveThis.toString())
        outState.putString("url", saveUrl.toString())
        super.onSaveInstanceState(outState)
    }


    //This function is called when user returns from the Forecast Activity.
    // The function restores the saved data the app saved in the onSaveInstanceState
    // and then calls the fetchAndUpdateUI again to update the UI.
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoreCity : String? = savedInstanceState.getString("city")
        val restoreUrl : String? = savedInstanceState.getString("url")

        if (restoreUrl != null && restoreCity != null) {
            cityName.text = restoreCity
            url = restoreUrl
            Log.d("Getting", "first")
            fetchAndUpdateUI()
        }
    }




}