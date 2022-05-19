package fi.tuni.weather_app_kotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


/*
CustomAdapter class is a customized adapter for the ListView to use.
This class takes Activity and a MutableList as parameters and returns an ArrayAdapter object.

To get the customized items to ListView, the getView function is overridden
and it returns a desired View object.
 */
class CustomAdapter(
    private val context: Activity,
    private val list: MutableList<ForecastListObj>?
) : ArrayAdapter<ForecastListObj>(context, R.layout.item, list!!) {
    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        val inflater: LayoutInflater = LayoutInflater.from(context)


        val view: View = inflater.inflate(R.layout.item, null)

        val wIcon: ImageView = view.findViewById(R.id.weatherImageF)
        val date: TextView = view.findViewById(R.id.forecastDate)
        val temp: TextView = view.findViewById(R.id.forecastTemp)

        val desc: TextView = view.findViewById(R.id.forecastDesc)



        // This function is used to format the Date object so that date is converted to weekday
        // and that seconds are omitted from the display.
        fun dateFormat(timestamp: Long): String? {
            val simple = SimpleDateFormat(
                "EEEE, HH:mm",
                Locale("en", "FI")
            )
            val date = (timestamp * 1000)
            return simple.format(date)

        }

        date.text = dateFormat(list?.get(position)?.dt!!).toString()
        temp.text = "${list?.get(position)?.main?.temp?.roundToInt()} °C"
        desc.text = "${list?.get(position)?.weather?.get(0)?.description}"

        //Picasso is used to load the weather icon to the view.
        Picasso.get().load("https://openweathermap.org/img/w/" +
                list?.get(position)?.weather?.get(0)?.icon +
                ".png").into(wIcon)


        return view
    }
}