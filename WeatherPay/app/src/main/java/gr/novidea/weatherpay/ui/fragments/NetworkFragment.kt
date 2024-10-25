package gr.novidea.weatherpay.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint

import gr.novidea.weatherpay.R
import gr.novidea.weatherpay.data.FlowerViewModel
import gr.novidea.weatherpay.databinding.FragmentNetworkBinding

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class NetworkFragment : Fragment() {

    private lateinit var binding: FragmentNetworkBinding

    private val viewModel: FlowerViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNetworkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline) {
                binding.networkStatus.text = getString(R.string.online)
            } else {
                binding.networkStatus.text = getString(R.string.offline)
                binding.networkType.text = "-"
            }
        }

        viewModel.connectionType.observe(viewLifecycleOwner) { connectionType ->
            binding.networkType.text = connectionType
        }

        viewModel.latency.observe(viewLifecycleOwner) { latency ->
            binding.latencyStatus.text = getString(R.string.ms, latency.toString())
        }

        viewModel.averageLatency.observe(viewLifecycleOwner) { averageLatency ->
            binding.avrLatencyStatus.text = getString(R.string.avr_ms, averageLatency.toString())
        }
    }
}