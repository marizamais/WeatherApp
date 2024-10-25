package gr.novidea.transaction.ui.activities

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import eu.cpm.connector.base.transactionparameter.CPMTransactionParameter
import eu.cpm.connector.base.transactionparameter.EReaderType
import eu.cpm.connector.base.transactionparameter.PinMode
import eu.cpm.connector.base.transactionparameter.TransactionMode
import eu.cpm.connector.cpm.CpmControllerClient
import eu.cpm.connector.cpm.CpmPinStatusListener
import eu.cpm.connector.cpm.CpmTransactionListener
import eu.cpm.connector.cpm.model.cardreader.ApplicationSelectionResponse
import eu.cpm.connector.cpm.model.cardreader.CardDataResponse
import eu.cpm.connector.cpm.model.cardreader.CardErrorResponse
import eu.cpm.connector.cpm.model.cardreader.CardReaderResponse
import eu.cpm.connector.cpm.model.cardreader.PinStatusResponse
import eu.cpm.connector.cpm.model.cardreader.PleaseSeePhoneResponse
import eu.cpm.connector.cpm.model.cardreader.RemoveCardPromptResponse
import eu.cpm.connector.entity.cardreader.CurrentTransactionParams
import eu.cpm.connector.entity.cardreader.SetTransactionParams
import gr.novidea.shared.data.ApplicationData
import gr.novidea.shared.data.CardData
import gr.novidea.shared.data.IntentData
import gr.novidea.shared.data.MerchantData
import gr.novidea.shared.data.ResponseData
import gr.novidea.shared.data.TransactionData
import gr.novidea.shared.utils.CpmHandler
import gr.novidea.shared.utils.CurrencyUtils
import gr.novidea.shared.utils.LoadingScreenManager
import gr.novidea.shared.utils.UiUtils
import gr.novidea.transaction.R
import gr.novidea.transaction.data.Constants
import gr.novidea.transaction.databinding.ActivityTransactionBinding
import gr.novidea.transaction.util.CardPinScreenManager
import gr.novidea.transaction.util.formatMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class TransactionActivity : AppCompatActivity() {

    private var TAG = "TransactionActivity"
    private lateinit var binding: ActivityTransactionBinding
    private lateinit var cpmHandler: CpmHandler
    private lateinit var loadingScreenManager: LoadingScreenManager

    private lateinit var pinScreenManager: CardPinScreenManager
    private lateinit var transaction: IntentData
    private var readerType: String = ""
    private var cardNumber: String = "-"
    private var cardType: String = "-"
    private var activeReaders = Constants.READERS
    private val cardReaderTimeout = 30
    private var countdownTimer: CountDownTimer? = null
    private var errored = true
    private var pintTriesLeft: Int = 3

    private lateinit var cpmTransactionParameter: CPMTransactionParameter

    val cardTypeMap = mapOf(
        "PICC" to "Contactless",
        "ICC" to "Chip",
        "MAG" to "Magnetic"
    )

    private val cpmPinStatusListener = object : CpmPinStatusListener {
        override fun onPinStatusResponse(pinStatusResponse: PinStatusResponse) {
            when (pinStatusResponse.pinState.toString()) {
                Constants.TransactionPinStates.PIN_ENTRY -> {
                    // Also fires after the pin inserted was wrong - so we need to clear the dots
                    pinScreenManager.showPinScreen(true)

                    if (pintTriesLeft == 3) {
                        pinScreenManager.setMessage(getString(R.string.pin_entry_message))
                    } else {
                        pinScreenManager.setMessage(
                            getString(
                                R.string.pin_retry_message, pintTriesLeft.toString()
                            )
                        )
                    }
                }

                Constants.TransactionPinStates.PIN_STAR_MESSAGE -> {
                    pinScreenManager.updateDots(pinStatusResponse.pinCount)
                }

                Constants.TransactionPinStates.PIN_TRIES_LEFT -> {
                    pintTriesLeft = pinStatusResponse.pinCount
                }

                Constants.TransactionPinStates.PIN_VERIFY_FAILED -> {
                    pinScreenManager.hidePinScreen(true)
                }

                Constants.TransactionPinStates.PIN_OK -> {
                    pinScreenManager.hidePinScreen(true)
                }

                Constants.TransactionPinStates.PIN_CANCELED -> {
                    pinScreenManager.hidePinScreen(true)
                }
            }
        }
    }

    private val cpmTransactionListener = object : CpmTransactionListener {
        override fun onApplicationSelectionResponse(
            applicationSelectionResponse: ApplicationSelectionResponse
        ) {
            Log.i(TAG, "onApplicationSelectionResponse")
        }

        override fun onCardDataResponse(cardDataResponse: CardDataResponse) {

            for (emv in cardDataResponse.emvData) {
                if (emv.key == Constants.EmvTags.CARD_NUMBER_MASKED) {
                    cardNumber = emv.value
                } else if (emv.key == Constants.EmvTags.APP_LABEL) {
                    cardType = emv.value
                }
            }

            pinScreenManager.hidePinScreen(true)
            transacting()
        }

        override fun onCardErrorResponse(cardErrorResponse: CardErrorResponse) {
            if (cardErrorResponse.errorCode == "-1") {
                activeReaders = EReaderType.MAG.toString()
            } else if (cardErrorResponse.errorCode == "-23") {
                activeReaders = EReaderType.MAG_ICC.toString()
            } else {
                activeReaders = EReaderType.MAG_ICC_PICC.toString()
            }
            errored = true
        }

        override fun onCardReaderResponse(cardReaderResponse: CardReaderResponse) {
            if (cardReaderResponse.cardReaderChipInsert) {
                toggleLeds(true)
            } else {
                toggleLeds(false)
            }

            if (!cardReaderResponse.cardReaderChipIdleThread) {
                Log.i(TAG, "POS IN TRANSACTION STATE!")
                if (cardReaderResponse.cardReaderChip) {
                    readerType = EReaderType.ICC.toString()
                }
                if (cardReaderResponse.cardReaderMag) {
                    readerType = EReaderType.MAG.toString()
                }
                if (cardReaderResponse.cardReaderCtls) {
                    readerType = EReaderType.PICC.toString()
                }
                stopCountdownTimer()
                CpmControllerClient.instance().startEmvProcess()

                if (cardReaderResponse.cardTimeout) {
                    startCountdownTimer()
                }
            } else {
                if (errored) {
                    startCountdownTimer()
                    updateReadersUI()
                    errored = false
                }

            }
        }

        override fun onPleaseSeePhoneResponse(pleaseSeePhoneResponse: PleaseSeePhoneResponse) {
            Log.i(TAG, "onPleaseSeePhoneResponse")
        }

        override fun onRemoveCardPromptResponse(removeCardPromptResponse: RemoveCardPromptResponse) {
            Log.i(TAG, "onRemoveCardPromptResponse")
        }

        override fun onTransactionParams(currentTransactionParams: CurrentTransactionParams): SetTransactionParams {
            return SetTransactionParams(false, PinMode.DUKPT, 1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadingScreenManager = LoadingScreenManager(this@TransactionActivity)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UiUtils.applySystemBarInsets(binding.main)

        pinScreenManager = CardPinScreenManager(this)

        if (intent.extras == null) {
            // Just to avoid the app crashing
            transaction = IntentData(null, null)
            return
        }

        val json = intent.getStringExtra("data")

        // Parse the JSON using Gson
        val gson = Gson()
        transaction = gson.fromJson(json, IntentData::class.java)

        loadingScreenManager.showLoading(getString(R.string.initialise))

        val onInit = fun(cpm: CpmControllerClient) {
            val jsonTemplate = assets.open("cpm_parameters.json").bufferedReader().use {
                it.readText()
            }
            cpm.configureServiceRequest(jsonTemplate)

            val posInfo = cpm.posInformationRequest()
            Constants.READERS = posInfo.cardReadersAvailability.toString()
            Constants.SN = posInfo.posSerialNumber
            loadingScreenManager.hideLoading()
            startCardReaders()
        }
        cpmHandler = CpmHandler(this, TAG, onInit)
        cpmHandler.initialize()

        binding.amount.text = CurrencyUtils.longToCurrency(
            (transaction.transaction?.amount ?: 0) + (transaction.transaction?.tip ?: 0), CurrencyUtils.getCurrencyLocales()
        )

        binding.backBtn.setOnClickListener {
            goBack()
        }

        binding.exitBtn.setOnClickListener {
            goHome()
        }
    }

    private fun transacting() {
        lifecycleScope.launch {
            loadingScreenManager.showLoading(getString(R.string.connecting))

            val resultIntent = Intent()

            val temperature = intent.getDoubleExtra("temperature", 0.0)
            val amount = (transaction.transaction?.amount?.toDouble() ?: 0.0) / 100

            if (amount == temperature) {
                resultIntent.putExtra("result", "000")
            } else {
                resultIntent.putExtra("result", "111")
            }

            resultIntent.putExtra("version", 1)
            resultIntent.putExtra("terminalSN", Constants.SN)
            val maskePan = cardNumber.mapIndexed { index, c ->
                if (index < cardNumber.length - 4) '*' else c
            }.joinToString("")
            val cardData = CardData(cardTypeMap[readerType] ?: "Unknown", cardNumber.substring(0, 6), "A0000000031010", cardType, maskePan, "1224")
            val transactionData = TransactionData("PV1", transaction.transaction?.amount, 0, transaction.transaction?.tip,
                transaction.transaction?.amount?.plus(transaction.transaction?.tip!!), 0, 21, 40, "031040", "777777", "014",  "1224", false, false)
            val merchantData = MerchantData(Constants.MID, Constants.TID, Constants.NAME, Constants.ADDRESS, Constants.CITY, Constants.POSTCODE, Constants.COUNTRY, Constants.PHONE)
            val applicationData = ApplicationData("2.0.10")
            val data = ResponseData("4c9a5193-4a4d-4b7f-b842-501315a7beb4", 1706788954362, cardData, transactionData, merchantData, applicationData)

            val gson = Gson()
            val json = gson.toJson(data)
            resultIntent.putExtra("data", json)
            setResult(RESULT_OK, resultIntent)

            delay(3000)
            loadingScreenManager.hideLoading()
            finish()
        }
    }

    private fun goBack() {
        val resultIntent = Intent()
        setResult(RESULT_CANCELED, resultIntent)
        finish()
    }

    private fun goHome() {
        val homeIntent = Intent()
        homeIntent.component = ComponentName(
            "gr.novidea.weatherpay", "gr.novidea.weatherpay.HomeScreenActivity"
        )

        startActivity(homeIntent)
        finish()
    }

    fun toggleLeds(on: Boolean) {
        val leds = binding.leds
        for (i in 0 until leds.childCount) {
            val led = leds.getChildAt(i) as ImageView
            led.imageTintList = if (on) {
                null
            } else {
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lightGrey))
            }
        }

    }

    fun updateReadersUI() {
        val icons = binding.icons
        val setup = Constants.ReadersSetup.getSetup(activeReaders)
            ?: throw IllegalArgumentException("Invalid reader type")

        for (i in 0 until icons.childCount) {
            val icon = icons.getChildAt(i) as View
            val isAvailable = i in setup.icons

            if (isAvailable) {
                icon.visibility = View.VISIBLE
            } else {
                icon.visibility = View.GONE
            }
        }

        binding.description.text = setup.message
    }

    private fun startCountdownTimer() {
        val countdownTime = cardReaderTimeout * 1000L
        countdownTimer?.cancel()
        binding.progressBar.max = countdownTime.toInt()
        countdownTimer = object : CountDownTimer(countdownTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.text = millisUntilFinished.formatMillis()
                binding.progressBar.progress = millisUntilFinished.toInt()
            }

            override fun onFinish() {
                binding.timer.text = getString(R.string.time)
                binding.progressBar.progress = binding.progressBar.max
            }
        }.start()
    }

    private fun stopCountdownTimer() {
        countdownTimer?.cancel()
    }

    private fun startCardReaders() {
        val mode = TransactionMode.SALE

        cpmTransactionParameter = CPMTransactionParameter(
            mode,
            transactionAmount = (transaction.transaction?.amount ?: 0).toInt(),
            tipAmount = (transaction.transaction?.tip ?: 0).toInt(),
            panMaskingPrintRight = 8,
            panMaskingPrintLeft = 8,
            pinEncryptionKeyIndex = 1,
            pinEncryptionType = PinMode.DUKPT,
            cardReaderTimeout = cardReaderTimeout
        )

        CpmControllerClient.instance().startCardReader(
            cpmTransactionParameter = cpmTransactionParameter,
            cpmTransactionListener = cpmTransactionListener,
            cpmPinStatusListener = cpmPinStatusListener
        )
        startCountdownTimer()
        activeReaders = Constants.READERS
        updateReadersUI()
    }

    private fun stopCardReaders() {
        CpmControllerClient.instance().standBy()
        stopCountdownTimer()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                cpmHandler.initializeCpm()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                goBack()
            }
        }
    }

    override fun onPause() {
        stopCardReaders()
        super.onPause()
    }

    override fun onResume() {
        startCardReaders()
        super.onResume()
    }

    override fun onDestroy() {
        stopCardReaders()
        super.onDestroy()
    }
}