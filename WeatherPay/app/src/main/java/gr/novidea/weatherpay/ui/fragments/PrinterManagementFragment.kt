package gr.novidea.weatherpay.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.databinding.FragmentPrinterManagementBinding

class PrinterManagementFragment : Fragment() {
    private var _binding: FragmentPrinterManagementBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrinterManagementBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences(
            Constants.SharedPrefKeys.RECEIPTS, Context.MODE_PRIVATE
        )

        binding.merchant.isChecked =
            sharedPreferences.getString(Constants.SharedPrefKeys.MERCHANT, null) == "true"
        binding.customer.isChecked =
            sharedPreferences.getString(Constants.SharedPrefKeys.CUSTOMER, null) == "true"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.merchant.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString(
                Constants.SharedPrefKeys.MERCHANT, binding.merchant.isChecked.toString()
            )
            editor.commit()
        }

        binding.customer.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putString(
                Constants.SharedPrefKeys.CUSTOMER, binding.customer.isChecked.toString()
            )
            editor.commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // To avoid memory leaks
    }
}