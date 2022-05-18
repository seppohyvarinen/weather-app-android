package fi.tuni.weather_app_kotlin
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherDescriptionObject(var description : String? = null, var icon : String? = null)
