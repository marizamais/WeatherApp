package gr.novidea.weatherpay.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import eu.cpm.connector.utils.currencies.parser.CurrencyParse
import gr.novidea.shared.utils.CurrencyUtils
import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.databinding.FragmentHomeBinding
import gr.novidea.weatherpay.ui.components.Keyboard


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var currencyLocales: CurrencyParse.CurrencyLocales
    private lateinit var defaultAmount: String
    lateinit var amount: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        currencyLocales = CurrencyUtils.getCurrencyLocales()
        defaultAmount = CurrencyUtils.longToCurrency(0L, currencyLocales)
        amount = defaultAmount

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mid.text = getString(R.string.mid, Constants.MID)
        binding.sn.text = getString(R.string.sn, Constants.SN)
        binding.tid.text = getString(R.string.tid, Constants.TID)

        binding.amount.setValue(amount)

        val toolbar = requireActivity().findViewById<View>(R.id.toolbar)
        val topNav = requireActivity().findViewById<View>(R.id.topNav)

        binding.keyboard.setOnKeyClickedListener(object : Keyboard.OnKeyClickedListener {
            override fun onKeyClicked(key: String) {
                when (key) {
                    "clear" -> {
                        if (amount == defaultAmount) return
                        amount = amount.dropLast(1)
                    }

                    "ok" -> {
                        if (amount == defaultAmount) return
                        val bundle = Bundle().apply {
                            putString("amount", amount)
                        }
                        findNavController().navigate(
                            R.id.action_homeFragment_to_tipFragment, bundle
                        )
                        toolbar.visibility = View.VISIBLE
                        topNav.visibility = View.GONE
                    }

                    else -> {
                        amount += key
                    }
                }

                amount = CurrencyUtils.longToCurrency(
                    CurrencyUtils.currencyToLong(amount), currencyLocales
                )
                binding.amount.setValue(amount)
            }
        })
    }

}