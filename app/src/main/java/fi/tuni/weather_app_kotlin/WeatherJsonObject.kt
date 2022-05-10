package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherJsonObject(var name: String? = null, var main : WeatherMain? = null, var weather : MutableList<WeatherDescriptionObject>? = null)
