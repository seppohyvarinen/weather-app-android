package fi.tuni.weather_app_kotlin
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
/*
This dataclass stores the description and icon code information of the fetched weather json.
Uses jackson to map the json.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherDescriptionObject(var description : String? = null, var icon : String? = null)
