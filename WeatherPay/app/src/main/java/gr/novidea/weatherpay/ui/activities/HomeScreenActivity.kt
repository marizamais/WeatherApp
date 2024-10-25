package gr.novidea.weatherpay.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import gr.novidea.shared.utils.UiUtils
import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.data.TransactionsViewModel
import gr.novidea.weatherpay.data.WeatherViewModel
import gr.novidea.weatherpay.data.WeatherViewModel.WeatherState
import gr.novidea.weatherpay.databinding.ActivityHomeScreenBinding
import gr.novidea.weatherpay.ui.adapters.ViewPagerAdapter
import gr.novidea.weatherpay.util.NavUtils
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding
    private val viewModel: TransactionsViewModel by viewModels()
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UiUtils.applySystemBarInsets(binding.main)
        UiUtils.applySystemBarInsets(binding.topNav, arrayOf("top", "bottom"))
        initializeViewPager()
        initializeWeatherInfo()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.hide()
    }

    private fun initializeViewPager() {

        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(1, false)
        binding.topNav.menu.getItem(1).isChecked = true
        binding.viewPager.offscreenPageLimit = 2

        // Temporarily prevent swipe while transacting - Until its a different activity
        viewModel.transacting.observe(this) {
            binding.viewPager.isUserInputEnabled = !it
        }

        binding.topNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.other -> {

                    binding.viewPager.currentItem = 0
                }

                R.id.home -> {
                    binding.viewPager.currentItem = 1
                }

                R.id.settings -> {
                    binding.viewPager.currentItem = 2
                }
            }
            true
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.topNav.menu.getItem(position).isChecked = true

                val navHostFragment =
                    supportFragmentManager.findFragmentByTag("f$position") as? NavHostFragment
                val navController = navHostFragment?.navController

                if (navController != null) {
                    NavUtils.setupActionBar(this@HomeScreenActivity, navController)

                    val isRoot = NavUtils.isNavRoot(navController)

                    if (isRoot) {
                        binding.topNav.visibility = View.VISIBLE
                        supportActionBar?.hide()
                    } else {
                        binding.topNav.visibility = View.GONE
                        supportActionBar?.show()
                    }
                }
            }
        })
    }

    private fun initializeWeatherInfo() {
        lifecycleScope.launch {
            weatherViewModel.weatherState.collect { state ->
                when (state) {
                    is WeatherState.Success -> println("Weather: ${state.data}")
                    else -> println("Failed weather state")
                }
            }
        }

        lifecycleScope.launch {
            weatherViewModel.cityState.collect { state ->
                when (state) {
                    is WeatherViewModel.CityState.Success -> {
                        println("## City: ${state.data}")
                        weatherViewModel.fetchWeather(
                            state.data?.lat ?: 0.0,
                            state.data?.lon ?: 0.0
                        )
                    }
                    else -> println("Failed city state")
                }
            }
        }

        weatherViewModel.fetchCity(Constants.GEO)
    }
}