package fi.tuni.weather_app_kotlin
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastDescObject(var description : String? = null, var icon : String? = null)
