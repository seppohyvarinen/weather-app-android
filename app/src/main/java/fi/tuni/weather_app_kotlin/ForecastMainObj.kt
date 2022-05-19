package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
/*
This dataclass stores the temperature information of the fetched forecast json.
Uses jackson to map the json.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastMainObj(var temp: Double? = 0.0)
