package fi.tuni.weather_app_kotlin
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/*
This dataclass stores the latitude and longitude information of the fetched weather json.
Uses jackson to map the json.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CoordinateObject(var lat : Double = 0.0, var lon : Double = 0.0)
