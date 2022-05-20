package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
/*
This dataclass stores wind data of the fetched weather json.
Uses jackson to map the json.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherWindObject(var speed: Double = 0.0)