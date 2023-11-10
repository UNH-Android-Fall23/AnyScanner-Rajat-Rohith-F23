package com.unh.anyscanner_rajat_rohith_f23

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.unh.anyscanner_rajat_rohith_f23.databinding.FragmentWifiDetailsBinding

class WifiDetailsFragment : Fragment() {


    private var _binding: FragmentWifiDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var wifiSSIDs: Array<String>
    private lateinit var wifiCapabilities: Array<String>
    private lateinit var connectedSSID: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        connectedSSID = arguments?.getString("connectedSSID", "") ?: ""
        wifiSSIDs = arguments?.getStringArray("SSIDArray") ?: emptyArray()
        wifiCapabilities = arguments?.getStringArray("capabilityArray") ?: emptyArray()


        return inflater.inflate(R.layout.fragment_wifi_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Access the position passed as an argument

        // Use position to get details from your data source

        // Display details in the UI
        binding.wifiSSIDTextView.text = connectedSSID ?: "No WiFi connected"
        //binding.wifiSecurityTextView.setText(wifiDetails.security)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
