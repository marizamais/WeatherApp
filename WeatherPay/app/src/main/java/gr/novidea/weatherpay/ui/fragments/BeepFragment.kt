package gr.novidea.weatherpay.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import eu.cpm.connector.cpm.CpmControllerClient

import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.ButtonItem
import gr.novidea.weatherpay.data.Constants
import gr.novidea.weatherpay.data.Sound
import gr.novidea.weatherpay.ui.adapters.ButtonItemRecyclerViewAdapter

/**
 * A fragment representing a list of Items.
 */
class BeepFragment : Fragment() {

    fun playMultipleSounds() = runBlocking {

        launch {
            for (i in Constants.getBeepItems(requireContext()).indices) {
                CpmControllerClient.instance().playSound(number = i, duration = 1000, sync = false)
                delay(1000L) // Non-blocking delay for 1000 milliseconds
            }
        }.join()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_beep_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {

            view.setLayoutManager(LinearLayoutManager(requireContext()))
            val adapter = ButtonItemRecyclerViewAdapter(Constants.getBeepItems(requireContext()))
            adapter.setOnClickListener(object : ButtonItemRecyclerViewAdapter.OnClickListener {
                override fun onClick(position: Int, item: ButtonItem) {

                    val data = item.data
                    if (data is Sound) {
                        if (data.position == -1) {
                            playMultipleSounds()
                        } else {
                            CpmControllerClient.instance().playSound(
                                number = data.position, duration = data.duration, sync = data.sync
                            )
                        }
                    }
                }
            })

            view.adapter = adapter
        }
        return view
    }
}