package gr.novidea.weatherpay.ui.fragments

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import eu.cpm.connector.utils.currencies.parser.CurrencyParse
import gr.novidea.shared.data.IntentData
import gr.novidea.shared.data.IntentProperties
import gr.novidea.shared.data.IntentTransaction
import gr.novidea.shared.data.ResponseData
import gr.novidea.shared.data.Transaction
import gr.novidea.shared.data.WeatherMap.getWeatherIconUrl
import gr.novidea.shared.data.WeatherMap.isNight
import gr.novidea.shared.data.WeatherResponse
import gr.novidea.shared.utils.CurrencyUtils
import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.data.RealmTransaction
import gr.novidea.weatherpay.data.TransactionsViewModel
import gr.novidea.weatherpay.data.WeatherViewModel
import gr.novidea.weatherpay.databinding.FragmentTipBinding
import gr.novidea.weatherpay.ui.components.Keyboard
import gr.novidea.weatherpay.util.NavUtils
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@AndroidEntryPoint
class TipFragment : Fragment() {
    private lateinit var binding: FragmentTipBinding
    private val viewModel: TransactionsViewModel by activityViewModels()
    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var currencyLocales: CurrencyParse.CurrencyLocales
    private lateinit var defaultAmount: String
    lateinit var tip: String
    lateinit var amount: String
    private lateinit var transaction: Transaction

    private var weather: WeatherResponse? = null
    private var temperature: Double = 0.0
    private var iconUrl: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTipBinding.inflate(inflater, container, false)
        currencyLocales = CurrencyUtils.getCurrencyLocales()
        defaultAmount = CurrencyUtils.longToCurrency(0L, currencyLocales)
        amount = arguments?.getString("amount").toString()
        tip = defaultAmount

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val resData = it.data?.getStringExtra("data") ?: ""
                val gson = Gson()
                val intentData = gson.fromJson(resData, ResponseData::class.java)
                val resCode = it.data?.getStringExtra("result")

                if (resCode == "000") {
                    transaction.cardType = intentData.cardData.type
                    transaction.cardNumber = intentData.cardData.maskedPan
                    transaction.reader = intentData.cardData.captureType
                    viewModel.addTransaction(RealmTransaction(transaction))

                    val json = Json.encodeToString(transaction)
                    val bundle = Bundle().apply {
                        putString("transaction", json)
                    }

                    findNavController().navigate(
                        R.id.action_tipFragment_to_approvedFragment, bundle
                    )
                } else {
                    val dialogBuilder =
                        AlertDialog.Builder(requireContext(), R.style.InformationDialogTheme)
                            .setTitle("Wrong Guess")
                    val drawable = GradientDrawable()
                    drawable.setColor(ContextCompat.getColor(requireContext(), R.color.white))
                    drawable.cornerRadius = resources.getDimension(R.dimen.corner_radius_2)

                    dialogBuilder.setMessage("Try again!")
                    dialogBuilder.setNeutralButton("OK") { _, _ ->
                        findNavController().navigate(
                            R.id.action_tipFragment_to_homeFragment
                        )
                    }.create().apply {
                        window?.setBackgroundDrawable(drawable)
                        show()
                    }
                }

            }
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NavUtils.setupActionBar(requireActivity() as AppCompatActivity, findNavController())

        binding.amount.setValue(tip)

        binding.keyboard.setOnKeyClickedListener(object : Keyboard.OnKeyClickedListener {
            override fun onKeyClicked(key: String) {
                when (key) {
                    "clear" -> {
                        if (tip == defaultAmount) return
                        tip = tip.dropLast(1)
                    }

                    "ok" -> {
                        goToTransactionApp()
                    }

                    else -> {
                        tip += key
                    }
                }

                tip =
                    CurrencyUtils.longToCurrency(CurrencyUtils.currencyToLong(tip), currencyLocales)
                binding.amount.setValue(tip)
            }
        })

        val currentWeatherState = weatherViewModel.weatherState.value
        weather = when(currentWeatherState) {
            is WeatherViewModel.WeatherState.Success -> currentWeatherState.data!!
            else -> null
        }

        temperature = weather?.main?.temperature ?: 0.0

        val weatherIcon = binding.weatherIcon
        val weatherId = weather?.weather?.get(0)?.id ?: 0

        val sunset = weather?.sys?.sunset ?: 0
        val sunrise = weather?.sys?.sunrise ?: 0
        val timezoneOffset = weather?.timezone ?: 0

        val isNight = isNight(sunrise, sunset, timezoneOffset)
        iconUrl = getWeatherIconUrl(weatherId, isNight) ?: ""

        Glide.with(this)
            .load(iconUrl)
            .override(300, 300)
            .fitCenter()
            .into(weatherIcon)
    }

    fun goToTransactionApp() {
        val intent = Intent()
        intent.component = ComponentName(
            "gr.novidea.transaction", "gr.novidea.transaction.ui.activities.TransactionActivity"
        )

        intent.putExtra("temperature", temperature)
        intent.putExtra("version", 1)
        val intentData = IntentData(
            IntentTransaction(
                0,
                CurrencyUtils.currencyToLong(amount),
                CurrencyUtils.currencyToLong(tip), 1
            ), IntentProperties("el", "DEFAULT", -1)
        )
        val gson = Gson()
        val json = gson.toJson(intentData)
        intent.putExtra("data", json)

        transaction = Transaction(
            iconUrl,
            viewModel.batch.value ?: 0,
            viewModel.batchTransactions.value?.size!! + 1,
            CurrencyUtils.currencyToLong(amount),
            CurrencyUtils.currencyToLong(tip),
            "cardNumber",
            "cardType",
            Constants.TRANSACTION_TYPES[0],
            "Unsettled",
            "readerType"
        )

        try {
            activityResultLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            println("Activity not found: " + e.message)
        }
    }

}