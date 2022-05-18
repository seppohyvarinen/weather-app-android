package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastListObj(var dt: Long? = null,  var main : ForecastMainObj? = null, var weather : MutableList<ForecastDescObject>? = null) {


    private fun dateFormat(timestamp: Long): String? {
        val simple = SimpleDateFormat(
            "dd.MM, HH:mm",
            Locale("fi", "FI")
        )
        val date = (timestamp * 1000)
        return simple.format(date)

    }
    override fun toString(): String {
        return "${dateFormat(dt!!)}, ${main!!.temp}, ${weather!!.get(0).description}"
    }
}
