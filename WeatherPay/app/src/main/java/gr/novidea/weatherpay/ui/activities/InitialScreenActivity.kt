package gr.novidea.weatherpay.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.AndroidEntryPoint
import eu.cpm.connector.cpm.CpmControllerClient
import gr.novidea.shared.utils.CpmHandler
import gr.novidea.shared.utils.LoadingScreenManager
import gr.novidea.shared.utils.UiUtils
import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.data.Constants.SharedPrefKeys.Companion.DEVICE
import gr.novidea.weatherpay.data.Constants.SharedPrefKeys.Companion.LANGUAGE
import gr.novidea.weatherpay.data.Constants.SharedPrefKeys.Companion.LATITUDE
import gr.novidea.weatherpay.data.Constants.SharedPrefKeys.Companion.LOCALIZATION
import gr.novidea.weatherpay.data.Constants.SharedPrefKeys.Companion.LONGITUDE
import gr.novidea.weatherpay.data.Constants.SharedPrefKeys.Companion.SERIAL_NUMBER
import gr.novidea.weatherpay.databinding.ActivityInitialScreenBinding
import kotlin.random.Random


@AndroidEntryPoint
class InitialScreenActivity : AppCompatActivity() {

    private val TAG = "InitialScreenActivity"

    private lateinit var binding: ActivityInitialScreenBinding
    private lateinit var cpmHandler: CpmHandler
    private lateinit var loadingScreenManager: LoadingScreenManager

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences(LOCALIZATION, Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString(LANGUAGE, "en")
        val localeList = LocaleListCompat.forLanguageTags(savedLanguage)
        AppCompatDelegate.setApplicationLocales(localeList)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityInitialScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val main = binding.main
        UiUtils.applySystemBarInsets(main)

        loadingScreenManager = LoadingScreenManager(this)
        loadingScreenManager.showLoading(getString(R.string.initialise))

        val onInit = fun(cpm: CpmControllerClient) {
            val posInfo = cpm.posInformationRequest()
            Constants.SN = posInfo.posSerialNumber

            val deviceSharedPref = applicationContext.getSharedPreferences(
                DEVICE, Context.MODE_PRIVATE
            )
            with(deviceSharedPref.edit()) {
                putString(SERIAL_NUMBER, posInfo.posSerialNumber)
                commit()
            }

            loadingScreenManager.hideLoading()
            binding.startBtn.isEnabled = true
        }

        val permissions = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        cpmHandler = CpmHandler(this, TAG, onInit)
        cpmHandler.initialize(permissions)

        binding.startBtn.setOnClickListener {
            val intent = Intent(this@InitialScreenActivity, HomeScreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                cpmHandler.initializeCpm()
                requestLocationData()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Suppress("MissingPermission")
    private fun requestLocationData() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        saveLocationCoords()

        if (!isGpsEnabled && !isNetworkEnabled) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                saveLocationCoords(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (isGpsEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000L, 0f, locationListener
            )
        }
        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000L, 0f, locationListener
            )
        }
    }

    private fun saveLocationCoords(location: Location? = null) {

        val latitude = location?.latitude ?: Random.nextDouble(-90.0, 90.0)
        val longitude = location?.longitude ?: Random.nextDouble(-180.0, 180.0)

        sharedPreferences.edit().putFloat(LATITUDE, latitude.toFloat()).apply()
        sharedPreferences.edit().putFloat(LONGITUDE, longitude.toFloat()).apply()

    }
}