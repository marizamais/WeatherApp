package gr.novidea.weatherpay.ui.fragments

import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint

import eu.cpm.connector.cpm.CpmPrintListener
import eu.cpm.connector.cpm.model.printer.PrintResponse

import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.data.TransactionsViewModel
import gr.novidea.weatherpay.databinding.FragmentTransactionsListBinding
import gr.novidea.weatherpay.ui.adapters.TransactionItemRecyclerViewAdapter
import gr.novidea.shared.utils.CurrencyUtils
import java.util.Date
import androidx.core.util.Pair
import eu.cpm.connector.cpm.CpmControllerClient
import gr.novidea.weatherpay.data.BatchReceiptData
import gr.novidea.weatherpay.util.BatchReceiptUtils
import java.util.Calendar

@AndroidEntryPoint
class TransactionsFragment : Fragment(), CpmPrintListener {

    private var columnCount = 1
    private val viewModel: TransactionsViewModel by activityViewModels()
    private lateinit var binding: FragmentTransactionsListBinding
    private lateinit var adapter: TransactionItemRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionsListBinding.inflate(inflater, container, false)

        binding.transactionsList.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }
        adapter = TransactionItemRecyclerViewAdapter()
        binding.transactionsList.adapter = adapter

        viewModel.transactions.observe(viewLifecycleOwner) {
            adapter.addItems(it)
            applyFilter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.batchTotal.observe(viewLifecycleOwner) {
            binding.totalAmount.text =
                CurrencyUtils.longToCurrency(it, CurrencyUtils.getCurrencyLocales())
        }

        // Set listener for button state changes
        binding.filters.addOnButtonCheckedListener { _, _, _ ->
            applyFilter()
        }

        binding.batchButton.setOnClickListener {
            findNavController().navigate(R.id.action_transactionsListFragment_to_batchManagementFragment)
        }

        binding.search.setOnClickListener {
            val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .build()

            dateRangePicker.addOnPositiveButtonClickListener { selection ->
                // selection is a Pair&lt;Long, Long&gt; representing the start and end dates in milliseconds since epoch
                val startDateMillis = selection.first
                val endDateMillis = selection.second

                var calendar = Calendar.getInstance().apply {
                    timeInMillis = startDateMillis
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val startDate = calendar.time
                calendar = Calendar.getInstance().apply {
                    timeInMillis = endDateMillis
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }

                val endDate = calendar.time

                datePicker(startDate, endDate)
            }
            dateRangePicker.show(parentFragmentManager, "datePicker")
        }
    }

    private fun applyFilter() {
        val settledChecked = binding.filters.checkedButtonIds.contains(R.id.settled)
        val unsettledChecked = binding.filters.checkedButtonIds.contains(R.id.unsettled)
        when {
            settledChecked && unsettledChecked -> adapter.filter("")
            settledChecked -> adapter.filter(Constants.TRANSACTION_STATUSES[0])
            unsettledChecked -> adapter.filter(Constants.TRANSACTION_STATUSES[1])
            else -> adapter.filter("")
        }
    }

    override fun onPrintResponse(printResponse: PrintResponse) {
        if (printResponse.resultCode == "0") {
            Log.v("Payment", "works")
        } else {
            Log.v("Payment", "don't work")
        }
    }

    private fun datePicker(startDate: Date, endDate: Date) {
        val drawable = GradientDrawable()
        drawable.setColor(ContextCompat.getColor(requireContext(), R.color.white))
        drawable.cornerRadius = resources.getDimension(R.dimen.corner_radius_2)
        val list = adapter.filterDates(startDate, endDate) as MutableList

        if (list.size > 0) {
            AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                .setMessage("Do you wish to print the transactions?")
                .setPositiveButton("Yes") { _, _ ->
                    val totalAmount = list.sumOf { it.amount }
                    val tipAmount = list.sumOf { it.tip }
                    val total = totalAmount + tipAmount

                    val data = BatchReceiptData(list, totalAmount, tipAmount, total)

                    val bitmap = BatchReceiptUtils.getBatchReceipt(data, requireContext())
                    val burnValue = 350
                    val cutPaperIfSupport = true
                    CpmControllerClient.instance().print(
                        bitmap,
                        burnValue,
                        "Transactions",
                        cutPaperIfSupport,
                        this
                    )
                }.setNegativeButton("No", null).create().apply {
                    window?.setBackgroundDrawable(drawable)
                    show()
                }
        }
    }
}
