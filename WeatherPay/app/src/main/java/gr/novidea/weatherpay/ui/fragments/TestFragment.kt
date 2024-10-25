package gr.novidea.weatherpay.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.ButtonItem
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.ui.adapters.ButtonIconItemRecyclerViewAdapter

/**
 * A fragment representing a list of Items.
 */
class TestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_test_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {

            view.setNestedScrollingEnabled(true)
            view.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.SPACE_EVENLY
            }

            val toolbar = requireActivity().findViewById<View>(R.id.toolbar)
            val topNav = requireActivity().findViewById<View>(R.id.topNav)

            val adapter = ButtonIconItemRecyclerViewAdapter(Constants.getTestItems(requireContext()))
            adapter.setOnClickListener(object : ButtonIconItemRecyclerViewAdapter.OnClickListener {
                override fun onClick(position: Int, item: ButtonItem) {
                    findNavController().navigate(item.action)
                    toolbar.visibility = View.VISIBLE
                    topNav.visibility = View.GONE
                }
            })

            view.adapter = adapter

        }
        return view
    }
}