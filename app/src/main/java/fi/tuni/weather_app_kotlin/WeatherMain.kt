package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherMain(var temp: Double? = 0.0, var feels_like : Double? = 0.0) {


}