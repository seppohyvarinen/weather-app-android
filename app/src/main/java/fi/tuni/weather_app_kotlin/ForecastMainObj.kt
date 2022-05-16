package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastMainObj(var temp: String? = null)
