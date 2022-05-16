package fi.tuni.weather_app_kotlin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastJsonObject(var list: MutableList<ForecastListObj>? = null)
