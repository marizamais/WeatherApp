package gr.novidea.shared.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneOffset

object WeatherMap {

    val weatherIcons = mapOf(
        // Thunderstorm Group
        200 to "11d", 201 to "11d", 202 to "11d", 210 to "11d",
        211 to "11d", 212 to "11d", 221 to "11d", 230 to "11d",
        231 to "11d", 232 to "11d",
        // Drizzle Group
        300 to "09d", 301 to "09d", 302 to "09d", 310 to "09d",
        311 to "09d", 312 to "09d", 313 to "09d", 314 to "09d",
        321 to "09d",
        // Rain Group
        500 to "10d", 501 to "10d", 502 to "10d", 503 to "10d",
        504 to "10d", 511 to "13d", 520 to "09d", 521 to "09d",
        522 to "09d", 531 to "09d",
        // Snow Group
        600 to "13d", 601 to "13d", 602 to "13d", 611 to "13d",
        612 to "13d", 613 to "13d", 615 to "13d", 616 to "13d",
        620 to "13d", 621 to "13d", 622 to "13d",
        // Atmosphere Group
        701 to "50d", 711 to "50d", 721 to "50d", 731 to "50d",
        741 to "50d", 751 to "50d", 761 to "50d", 762 to "50d",
        771 to "50d", 781 to "50d",
        // Clear Group
        800 to "01d",
        // Clouds Group
        801 to "02d", 802 to "03d", 803 to "04d", 804 to "04d"
    )

    fun getWeatherIconUrl(weatherId: Int, isNight: Boolean = false): String? {
        val iconCode = weatherIcons[weatherId]?.let {
            if (isNight) it.replace("d", "n") else it
        }
        return iconCode?.let { "https://openweathermap.org/img/wn/${it}@2x.png" }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isNight(sunrise: Long, sunset: Long, timezoneOffset: Long): Boolean {
        val offset = ZoneOffset.ofTotalSeconds(timezoneOffset.toInt())
        val currentTime = LocalDateTime.now(offset)
        val sunriseTime = LocalDateTime.ofEpochSecond(sunrise, 0, offset)
        val sunsetTime = LocalDateTime.ofEpochSecond(sunset, 0, offset)

        return currentTime.isBefore(sunriseTime) || currentTime.isAfter(sunsetTime)
    }

}