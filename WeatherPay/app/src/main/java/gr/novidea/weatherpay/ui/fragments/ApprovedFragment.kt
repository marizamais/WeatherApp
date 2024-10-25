package gr.novidea.weatherpay.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import eu.cpm.connector.cpm.CpmControllerClient
import eu.cpm.connector.cpm.CpmPrintListener
import eu.cpm.connector.cpm.model.printer.PrintResponse
import gr.novidea.shared.data.Transaction
import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.data.MessagesViewModel
import gr.novidea.weatherpay.data.ReceiptData
import gr.novidea.weatherpay.data.WeatherViewModel
import gr.novidea.weatherpay.databinding.FragmentApprovedBinding
import gr.novidea.weatherpay.ui.components.LabeledText
import gr.novidea.weatherpay.util.ReceiptUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class ApprovedFragment : Fragment(), CpmPrintListener {
    private lateinit var binding: FragmentApprovedBinding
    private val viewModel: MessagesViewModel by activityViewModels()
    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private var transactionInfo = Transaction()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentApprovedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val json = arguments?.getString("transaction")!!
        transactionInfo = Json.decodeFromString<Transaction>(json)
//        binding.amount.setValue(CurrencyUtils.longToCurrency(transactionInfo.amount+transactionInfo.tip, CurrencyUtils.getCurrencyLocales()))
        val toolbar = requireActivity().findViewById<View>(R.id.toolbar)
        val topNav = requireActivity().findViewById<View>(R.id.topNav)

        toolbar.visibility = View.VISIBLE
        topNav.visibility = View.GONE

        val activity = requireActivity() as AppCompatActivity
        val actionBar = activity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.title = " "

        addWeatherInfo()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.just_close, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_close -> {
                        toolbar.visibility = View.GONE
                        topNav.visibility = View.VISIBLE
                        actionBar?.setDisplayHomeAsUpEnabled(true)
                        findNavController().navigate(R.id.action_approvedFragment_to_homeFragment)
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.button.setOnClickListener {
            lifecycleScope.launch {
                Glide.with(requireContext())
                    .asBitmap()
                    .load(transactionInfo.icon)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            lifecycleScope.launch {
                                prepareReceipts(resource)
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
        }
    }

    private fun addWeatherInfo() {
        val weather = when(val currentWeatherState = weatherViewModel.weatherState.value) {
            is WeatherViewModel.WeatherState.Success -> currentWeatherState.data!!
            else -> null
        }

        val detailsValue = getString(R.string.temperature_details, weather?.weather?.get(0)?.description)
        binding.details.text = detailsValue

        val temperature = binding.weatherDataContainer.findViewById<LabeledText>(R.id.temperature)
        val tempValue = getString(R.string.temperature_value, weather?.main?.temperature.toString())
        temperature.setValue(tempValue)

        val feelsLike = binding.weatherDataContainer.findViewById<LabeledText>(R.id.feelsLike)
        val feelsLikeValue = getString(R.string.temperature_value, weather?.main?.feelsLike.toString())
        feelsLike.setValue(feelsLikeValue)

        val humidity = binding.weatherDataContainer.findViewById<LabeledText>(R.id.humidity)
        val humidityValue = getString(R.string.humidity_value, weather?.main?.humidity.toString(), "%")
        humidity.setValue(humidityValue)

        val windSpeed = binding.weatherDataContainer.findViewById<LabeledText>(R.id.windSpeed)
        val windSpeedValue = getString(R.string.wind_speed_value, weather?.wind?.speed.toString())
        windSpeed.setValue(windSpeedValue)

    }

    private suspend fun prepareReceipts(weatherIcon: Bitmap) {
        val bMap = BitmapFactory.decodeResource(resources, R.drawable.layer)
        val data = ReceiptData(
            true,
            transactionInfo.tip,
            bMap,
            weatherIcon,
            Constants.NAME,
            Constants.CITY,
            Constants.ADDRESS,
            Constants.PHONE,
            transactionInfo.amount,
            Constants.MID,
            Constants.TID,
            transactionInfo.number,
            "1.2.1",
            transactionInfo.batch,
            Constants.SN,
            transactionInfo.id
        )

        val sharedPreferences = requireContext().getSharedPreferences(
            Constants.SharedPrefKeys.RECEIPTS, Context.MODE_PRIVATE
        )

        if (sharedPreferences.getString(
                Constants.SharedPrefKeys.MERCHANT, null
            ) == "true"
        ) {
            val bitmap = ReceiptUtils.getProducerReceipt(requireContext(), data)
            val burnValue = 350
            val cutPaperIfSupport = true
            CpmControllerClient.instance().print(
                bitmap,
                burnValue,
                Constants.ACTIONCODE_MERCHANT,
                cutPaperIfSupport,
                this@ApprovedFragment
            )
            delay(3000)
        }

        if (sharedPreferences.getString(
                Constants.SharedPrefKeys.CUSTOMER, null
            ) == "true"
        ) {
            val bitmap = ReceiptUtils.getCustomerReceipt(requireContext(), data, lifecycleScope, viewModel)
            val burnValue = 350 // Value between 50 and 500
            // Identifier for this print job
            val cutPaperIfSupport = true
            CpmControllerClient.instance().print(
                bitmap,
                burnValue,
                Constants.ACTIONCODE_CUSTOMER,
                cutPaperIfSupport,
                this@ApprovedFragment
            )
        }
    }

    override fun onPrintResponse(printResponse: PrintResponse) {
        if (printResponse.resultCode == "0") {
            Log.v("Payment", "Printed Receipt")
        } else {
            Log.v("Payment", "Error in receipt's print")
        }
    }
}