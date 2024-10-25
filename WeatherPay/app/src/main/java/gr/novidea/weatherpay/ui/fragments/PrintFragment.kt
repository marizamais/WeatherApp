package gr.novidea.weatherpay.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.cpm.connector.cpm.CpmControllerClient
import eu.cpm.connector.cpm.CpmPrintListener
import eu.cpm.connector.cpm.model.printer.PrintResponse
import gr.novidea.weatherpay.R

import gr.novidea.weatherpay.data.ButtonItem
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.databinding.FragmentPrintListBinding
import gr.novidea.weatherpay.ui.adapters.ButtonItemRecyclerViewAdapter
import gr.novidea.weatherpay.util.TestReceiptUtils

/**
 * A fragment representing a list of Items.
 */
class PrintFragment : Fragment(), CpmPrintListener {

    private lateinit var binding: FragmentPrintListBinding
    private var columnCount = 1
    val TAG = "PrintFragment"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrintListBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences(
            Constants.SharedPrefKeys.RECEIPTS, Context.MODE_PRIVATE
        )
        // Set the adapter
        binding.printList.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }

        val adapter = ButtonItemRecyclerViewAdapter(Constants.getPrintItems(requireContext()))
        adapter.setOnClickListener(object : ButtonItemRecyclerViewAdapter.OnClickListener {
            override fun onClick(position: Int, item: ButtonItem) {
                val bitmap = when (item.label) {
                    requireContext().getString(R.string.text_receipt) -> {
                        TestReceiptUtils.textReceipt(requireContext())
                    }
                    requireContext().getString(R.string.multiline_text_receipt) -> {
                        TestReceiptUtils.multiLineReceipt(requireContext())
                    }
                    requireContext().getString(R.string.bars_receipt) -> {
                        TestReceiptUtils.barsReceipt(BitmapFactory.decodeResource(resources, R.drawable.bar))
                    }
                    else -> {
                        TestReceiptUtils.logoReceipt(BitmapFactory.decodeResource(resources, R.drawable.layer), BitmapFactory.decodeResource(resources, R.drawable.black_person))
                    }
                }
                val burnValue =
                    sharedPreferences.getString(Constants.SharedPrefKeys.GREY_LEVEL, "150")?.toInt()
                val cutPaperIfSupport = true
                if (burnValue != null) {
                    CpmControllerClient.instance().print(
                        bitmap,
                        burnValue,
                        Constants.ACTIONCODE_MERCHANT,
                        cutPaperIfSupport,
                        this@PrintFragment
                    )
                }
            }
        })
        binding.printList.adapter = adapter
        return binding.root
    }

    private fun updateGreyLevel(greyLevel: Int, sharedPref: SharedPreferences?) {
        with(sharedPref?.edit()) {
            this?.putString(Constants.SharedPrefKeys.GREY_LEVEL, greyLevel.toString())
            this?.apply()
        }
        binding.greyLevelValue.text = getString(R.string.grey_level_value, greyLevel.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var greyLevel = sharedPreferences.getString(Constants.SharedPrefKeys.GREY_LEVEL, "150")
        updateGreyLevel(greyLevel.toString().toInt(), sharedPreferences)
        binding.greyLevelSlider.value = greyLevel.toString().toFloat()

        binding.greyLevelSlider.addOnChangeListener { _, value, _ ->
            greyLevel = value.toInt().toString()
            updateGreyLevel(value.toInt(), sharedPreferences)
        }
    }

    override fun onPrintResponse(printResponse: PrintResponse) {
        if (printResponse.resultCode == "0") {
            Log.v(TAG, "SUCCESS: ${printResponse.resultMessage}")
        } else {
            Log.v(TAG, "ERROR: ${printResponse.resultMessage}")
        }
    }
}