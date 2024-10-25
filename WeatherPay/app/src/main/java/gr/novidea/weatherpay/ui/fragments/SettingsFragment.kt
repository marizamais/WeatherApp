package gr.novidea.weatherpay.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pax.neptunelite.api.NeptuneLiteUser
import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.ButtonItem
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.ui.adapters.ButtonItemRecyclerViewAdapter

/**
 * A fragment representing a list of Items.
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_list, container, false)

        val toolbar = requireActivity().findViewById<View>(R.id.toolbar)
        val topNav = requireActivity().findViewById<View>(R.id.topNav)

        // Set the adapter
        if (view is RecyclerView) {

            view.setLayoutManager(LinearLayoutManager(requireContext()))
            val adapter = ButtonItemRecyclerViewAdapter(Constants.getSettingsItems(requireContext()))

            adapter.setOnClickListener(object : ButtonItemRecyclerViewAdapter.OnClickListener {
                override fun onClick(position: Int, item: ButtonItem) {
                    when (item.action) {
                        -1 -> {
                            NeptuneLiteUser.getInstance().getDal(requireContext()).sys.reboot()
                        }
                        else -> {
                            findNavController().navigate(item.action)
                            toolbar.visibility = View.VISIBLE
                            topNav.visibility = View.GONE
                        }
                    }
                }
            })

            view.adapter = adapter

        }
        return view
    }

}