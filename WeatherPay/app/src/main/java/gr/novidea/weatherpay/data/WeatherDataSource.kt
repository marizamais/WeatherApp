package gr.novidea.weatherpay.data

import gr.novidea.shared.data.GeoResponse
import gr.novidea.shared.data.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherDataSource
@Inject constructor(
    private val api: WeatherApi
) {

    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherResponse>  {
//        return api.getWeatherData(latitude, longitude)

        return withContext(Dispatchers.IO) {
            try {
                val response = api.getWeatherData(latitude, longitude)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)  // Propagate the exception as a failure
            }
        }
    }

    suspend fun getCity(cityName: String): Result<GeoResponse> {
//        return api.getCityData(cityName).first()

        return withContext(Dispatchers.IO) {
            try {
                val response = api.getCityData(cityName).first()
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}