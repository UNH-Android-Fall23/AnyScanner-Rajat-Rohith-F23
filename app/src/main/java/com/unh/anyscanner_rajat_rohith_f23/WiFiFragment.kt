package com.unh.anyscanner_rajat_rohith_f23

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.unh.anyscanner_rajat_rohith_f23.databinding.FragmentWiFiBinding

class WiFiFragment : Fragment() {

    private lateinit var wifiManager: WifiManager
    private val TAG="AnyScannerF23"
    private var connectedSSID: String = ""
    private var wifiScanResults: List<ScanResult> =listOf(
        ScanResult().apply {
            SSID = "SSID1"
            capabilities = "Capabilities1"
        }
    )

    private var _binding: FragmentWiFiBinding? = null

    // with the backing property of the kotlin we extract
    // the non null value of the _binding
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWiFiBinding.inflate(inflater, container, false)
        //TODO this might cause error add check
        updateConnectedSSID()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wifiManager = activity?.getSystemService(Context.WIFI_SERVICE) as WifiManager
       askPermission()
        val scanButton= binding.scanBtn
        scanButton.setOnClickListener {
            scanWifi()
        }
        val recyclerView= binding.wifiRecycler
        val customAdapter = WIFIRecyclerAdapter(arrayOf(), arrayOf(),0)
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
    @SuppressLint("MissingPermission")
    private fun scanSuccess() {
        val filteredResults = wifiManager.scanResults
            .filter { it.SSID.isNotEmpty() } // Filter out blank SSIDs
            .distinctBy { it.SSID } // Remove duplicate SSIDs

        wifiScanResults = filteredResults
        Log.d(TAG, "result is ${wifiScanResults.toString()}")
        updateConnectedSSID()

        val ssids = wifiScanResults.map { it.SSID }.toTypedArray()
        val capabilities = wifiScanResults.map { it.capabilities }.toTypedArray()
        val connectedPosition = ssids.indexOf(connectedSSID)
        val customAdapter = WIFIRecyclerAdapter(ssids, capabilities, connectedPosition)


        val recyclerView= binding.wifiRecycler
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        Log.d(TAG,"result is ${wifiScanResults.toString()}")
    }

    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
      //  val results = wifiManager.scanResults
      Log.d(TAG,"Scan failed")
    }
    fun scanWifi(){

        val wifiScanReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {
                    scanSuccess()
                } else {
                    scanFailure()
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        requireContext().registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling
            scanFailure()
        }

    }
    fun askPermission(){

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Location not permitted")
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.CHANGE_WIFI_STATE) !==
            PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Wifi not permitted")
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.CHANGE_WIFI_STATE)) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.CHANGE_WIFI_STATE), 1)
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.CHANGE_WIFI_STATE), 1)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun updateConnectedSSID() {
        val wifiManager = requireContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        connectedSSID = wifiInfo.ssid.replace("\"", "")
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Log.d(TAG,"Location permitted")
                        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(requireActivity(),
                            Manifest.permission.CHANGE_WIFI_STATE) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Log.d(TAG,"WiFi permitted")
                        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

}

