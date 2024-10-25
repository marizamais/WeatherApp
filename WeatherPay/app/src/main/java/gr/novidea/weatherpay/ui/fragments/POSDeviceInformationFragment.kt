package gr.novidea.weatherpay.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import eu.cpm.connector.cpm.CpmControllerClient

import gr.novidea.weatherpay.databinding.FragmentPOSDeviceInformationBinding


class POSDeviceInformationFragment : Fragment() {
    private lateinit var binding: FragmentPOSDeviceInformationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPOSDeviceInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val posInfo = CpmControllerClient.instance().posInformationRequest()

        binding.sn.text = posInfo.posSerialNumber
        binding.Os.text = posInfo.posOSName
        binding.modelName.text = posInfo.posModelName
        binding.PhysKey.text = if (posInfo.physicalKeyboardAvailable) {
            "YES"
        } else {
            "NO"
        }
        binding.Printer.text = if (posInfo.posPrinterAvailable) {
            "YES"
        } else {
            "NO"
        }
    }
}