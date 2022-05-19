package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/*
This dataclass stores the date, weather and main (temperature) information of the fetched forecast json.
Uses jackson to map the json.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastListObj(var dt: Long? = null,  var main : ForecastMainObj? = null, var weather : MutableList<ForecastDescObject>? = null) {


}
