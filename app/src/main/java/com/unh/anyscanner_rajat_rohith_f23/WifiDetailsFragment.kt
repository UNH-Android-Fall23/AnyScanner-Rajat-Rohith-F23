package com.unh.anyscanner_rajat_rohith_f23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.unh.anyscanner_rajat_rohith_f23.databinding.FragmentWiFiBinding
import com.unh.anyscanner_rajat_rohith_f23.databinding.FragmentWifiDetailsBinding

class WifiDetailsFragment : Fragment() {


    private var _binding: FragmentWifiDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var selectedCapability: String
    private lateinit var selectedSSID: String
    private lateinit var connectedSSID: String
    private val TAG="AnyScannerF23"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWifiDetailsBinding.inflate(inflater, container, false)
        connectedSSID = arguments?.getString("connectedSSID", "NA") ?: ""
        selectedSSID = arguments?.getString("selectedSSID", "NA") ?: ""
        selectedCapability = arguments?.getString("selectedCapability", "NA") ?: ""

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the position passed as an argument

        // Use position to get details from your data source

        // Display details in the UI
        binding.wifiSSIDTextView.text = "Wi-Fi SSID: $selectedSSID" ?: "No WiFi connected"
        binding.wifiSecurityTextView.text="Wi-Fi Security: $selectedCapability" ?: "No WiFi connected"
        //binding.wifiSecurityTextView.setText(wifiDetails.security)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
