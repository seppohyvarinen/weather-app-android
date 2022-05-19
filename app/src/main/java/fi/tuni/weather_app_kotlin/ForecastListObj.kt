package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastListObj(var dt: Long? = null,  var main : ForecastMainObj? = null, var weather : MutableList<ForecastDescObject>? = null) {


}
