package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
/*
This dataclass stores the temperature and "feels like" information of the fetched weather json.
Uses jackson to map the json.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherMain(var temp: Double? = 0.0, var feels_like : Double? = 0.0) {

}