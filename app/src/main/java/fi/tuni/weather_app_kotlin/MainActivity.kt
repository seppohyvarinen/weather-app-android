package fi.tuni.weather_app_kotlin

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQ_CODE = 1000;

    private lateinit var myFusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var url : String = "https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&units=metric&appid=bc2d40bf4e1d09c80f0383a56d873af0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize fused location client
        myFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
                latitude = location.latitude
                longitude = location.longitude
                url = "https://api.openweathermap.org/data/2.5/weather?lat=${latitude}&lon=${longitude}&units=metric&appid=bc2d40bf4e1d09c80f0383a56d873af0"
                downloadUrlAsync(this, url) {
                    if (it != null) {
                        Log.d("tagg", it)
                    }

                }
                Log.d( "Latitude:", latitude.toString())

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
        getCurrentLocation()

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