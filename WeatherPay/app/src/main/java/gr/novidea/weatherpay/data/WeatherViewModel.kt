package gr.novidea.weatherpay.data

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gr.novidea.shared.data.GeoResponse
import gr.novidea.shared.data.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    @Named(Constants.SharedPrefKeys.LOCALIZATION) private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState?> get() = _weatherState

    private val _cityState = MutableStateFlow<CityState>(CityState.Loading)
    val cityState: StateFlow<CityState?> get() = _cityState

    fun fetchWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            val result = weatherRepository.getWeather(latitude, longitude)
            if (result.isSuccess) {
                _weatherState.value = WeatherState.Success(result.getOrNull())
            } else {
                _weatherState.value = WeatherState.Error(result.exceptionOrNull())
            }

        }
    }

    fun fetchCity(cityName: String) {
        viewModelScope.launch {
            _cityState.value = CityState.Loading
            val result = weatherRepository.getCity(cityName)
            if (result.isSuccess) {
                _cityState.value = CityState.Success(result.getOrNull())
            } else {
                _cityState.value = CityState.Error(result.exceptionOrNull())
            }

        }
    }

    sealed class WeatherState {
        object Loading : WeatherState()
        data class Success(val data: WeatherResponse?) : WeatherState()
        data class Error(val error: Throwable?) : WeatherState()
    }

    sealed class CityState {
        object Loading : CityState()
        data class Success(val data: GeoResponse?) : CityState()
        data class Error(val error: Throwable?) : CityState()
    }

}