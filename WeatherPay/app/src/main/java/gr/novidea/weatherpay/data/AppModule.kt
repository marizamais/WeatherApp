package gr.novidea.weatherpay.data

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gr.novidea.weatherpay.util.NetworkMonitor
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideRealm(
        @ApplicationContext context: Context,
    ): Realm {
        val realmConfig = RealmConfiguration.create(
            schema = setOf(
                RealmTransaction::class, Message::class
            ),
        )
        return Realm.open(realmConfig)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context,
    ): NetworkMonitor {
        return NetworkMonitor(context)
    }

    private const val TRANSACTIONS = Constants.SharedPrefKeys.TRANSACTIONS

    @Provides
    @Singleton
    @Named(TRANSACTIONS)
    fun provideTransactionsSharedPreferences(
        @ApplicationContext app: Context
    ): SharedPreferences {
        return app.getSharedPreferences(TRANSACTIONS, Context.MODE_PRIVATE)
    }

    private const val LOCALIZATION = Constants.SharedPrefKeys.LOCALIZATION

    @Provides
    @Singleton
    @Named(LOCALIZATION)
    fun provideLocalizationSharedPreferences(
        @ApplicationContext app: Context
    ): SharedPreferences {
        return app.getSharedPreferences(LOCALIZATION, Context.MODE_PRIVATE)
    }

    private const val BASE_URL = "https://api.openweathermap.org"

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)  // Use your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Singleton
    @Provides
    fun provideWeatherRepository(weatherDataSource: WeatherDataSource): WeatherRepository {
        return WeatherRepository(weatherDataSource)
    }

}