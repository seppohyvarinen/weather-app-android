package fi.tuni.weather_app_kotlin

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationServices

class ForecastActivity : AppCompatActivity(){

    lateinit var extraBox : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        extraBox = findViewById<TextView>(R.id.extraTest)
        var extras : Bundle? = intent.extras



        if (extras != null) {
            val extraTest : String? = extras.getString("latitude")
            if (extraTest != null) {
                extraBox.text = extraTest
            }
        }

    }
}