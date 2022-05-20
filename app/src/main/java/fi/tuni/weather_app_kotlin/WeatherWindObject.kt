package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
/*
This dataclass stores the main list object of the fetched forecast json.
Uses jackson to map the json.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherWindObject(var speed: Double = 0.0)