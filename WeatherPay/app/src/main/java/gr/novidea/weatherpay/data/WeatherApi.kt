package gr.novidea.weatherpay.data

import gr.novidea.shared.data.GeoResponse
import gr.novidea.shared.data.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "23684fba1a44936a38dc1fe7144033f1"

interface WeatherApi {

    @GET("/data/2.5/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude", encoded = true) exclude: String = "minutely,alerts",
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("/geo/1.0/direct")
    suspend fun getCityData(
        @Query("q", encoded = true) cityName: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String = API_KEY
    ): List<GeoResponse>

}