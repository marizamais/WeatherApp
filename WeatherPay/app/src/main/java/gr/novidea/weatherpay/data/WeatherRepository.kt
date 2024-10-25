package gr.novidea.weatherpay.data

import gr.novidea.shared.data.GeoResponse
import gr.novidea.shared.data.WeatherResponse
import javax.inject.Inject

class WeatherRepository
@Inject constructor(
    private val weatherDataSource: WeatherDataSource
) {
    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherResponse> {
        return weatherDataSource.getWeather(latitude, longitude)
    }

    suspend fun getCity(city: String): Result<GeoResponse> {
        return weatherDataSource.getCity(city)
    }
}