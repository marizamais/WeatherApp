package gr.novidea.weatherpay.ui.fragments

import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import eu.cpm.connector.cpm.CpmControllerClient
import eu.cpm.connector.cpm.CpmPrintListener
import eu.cpm.connector.cpm.model.printer.PrintResponse
import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.BatchReceiptData
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.data.RealmTransaction
import gr.novidea.weatherpay.data.TransactionsViewModel
import gr.novidea.weatherpay.databinding.FragmentBatchManagementBinding
import gr.novidea.weatherpay.util.BatchReceiptUtils

/**
 * A simple [Fragment] subclass.
 */

@AndroidEntryPoint
class BatchManagementFragment : Fragment(), CpmPrintListener {

    private val TAG = "BatchManagementFragment"
    private lateinit var binding: FragmentBatchManagementBinding
    private val viewModel: TransactionsViewModel by activityViewModels()
    private lateinit var batchTransactions: List<RealmTransaction>
    private var batchAmountTotal: Long = 0
    private var batchTipTotal: Long = 0
    private var batchTotal: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBatchManagementBinding.inflate(inflater, container, false)

        viewModel.latestClosedBatchTransactions.observe(viewLifecycleOwner) { list ->
            batchTransactions = list

            batchAmountTotal = list.sumOf { it.amount }
            batchTipTotal = list.sumOf { it.tip }
            batchTotal = batchAmountTotal + batchTipTotal
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drawable = GradientDrawable()
        drawable.setColor(ContextCompat.getColor(requireContext(), R.color.white))
        drawable.cornerRadius = resources.getDimension(R.dimen.corner_radius_2)

        binding.closeBatchButton.setOnClickListener {
            Log.i(TAG, "## Close batch button clicked")

            val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Warning")

            if (viewModel.batchTransactions.value?.size == 0) {
                Log.i(TAG, "No transactions to close")
                dialogBuilder.setMessage("Batch is empty. Please add transactions before closing the batch")
                    .setNeutralButton("OK", null)
            } else {
                dialogBuilder.setMessage("Do you wish to proceed with closing the batch?")
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.closeBatchAsync {
                            printBatchReceipt()
                        }
                    }
                    .setNegativeButton("No", null)
            }
            dialogBuilder.create().apply {
                window?.setBackgroundDrawable(drawable)
                show()
            }
        }

        binding.reprintBatchButton.setOnClickListener {
            AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme).setTitle("Warning")
                .setMessage("Do you wish to reprint the close batch receipt?")
                .setPositiveButton("Yes") { _, _ ->
                    printBatchReceipt()
                }.setNegativeButton("No", null).create().apply {
                    window?.setBackgroundDrawable(drawable)
                    show()
                }
        }
    }

    private fun printBatchReceipt() {
        val data = BatchReceiptData(batchTransactions, batchAmountTotal, batchTipTotal, batchTotal)

        val bitmap = BatchReceiptUtils.getBatchReceipt(data, requireContext())
        val burnValue = 350
        val cutPaperIfSupport = true
        CpmControllerClient.instance().print(
            bitmap,
            burnValue,
            Constants.ACTIONCODE_MERCHANT,
            cutPaperIfSupport,
            this@BatchManagementFragment
        )
    }

    override fun onPrintResponse(printResponse: PrintResponse) {
        if (printResponse.resultCode == "0") {
            Log.v(TAG, "SUCCESS: ${printResponse.resultMessage}")
        } else {
            Log.v(TAG, "ERROR: ${printResponse.resultMessage}")
        }
    }

}