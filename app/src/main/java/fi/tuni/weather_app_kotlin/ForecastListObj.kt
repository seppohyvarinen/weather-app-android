package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastListObj(var dt: Date? = null, var main : ForecastMainObj? = null, var weather : MutableList<String>? = null)
