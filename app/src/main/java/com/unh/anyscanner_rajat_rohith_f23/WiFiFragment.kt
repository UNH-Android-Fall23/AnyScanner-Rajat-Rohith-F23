package com.unh.anyscanner_rajat_rohith_f23

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WiFiFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var wifiManager: WifiManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val WIFI_PERMISSION_REQUEST_CODE = 2
    private var isScanning = false
    private val circleList: MutableList<Pair<Circle, ScanResult>> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityResultLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))
        val view = inflater.inflate(R.layout.fragment_wi_fi, container, false)
         if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
       ) {
             ActivityCompat.requestPermissions(
                 requireActivity(),
                 arrayOf(
                     Manifest.permission.ACCESS_FINE_LOCATION,
                     Manifest.permission.ACCESS_COARSE_LOCATION
                 ),
                 LOCATION_PERMISSION_REQUEST_CODE
             )
         }else {
             val mapFragment =
                 childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
             mapFragment.getMapAsync(this)

             fusedLocationClient =
                 LocationServices.getFusedLocationProviderClient(requireActivity())
             wifiManager =
                 requireActivity().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

             val scanButton = view.findViewById<Button>(R.id.scanButton)
             scanButton.setOnClickListener {
                 if (ActivityCompat.checkSelfPermission(
                         requireContext(),
                         Manifest.permission.ACCESS_FINE_LOCATION
                     ) == PackageManager.PERMISSION_GRANTED &&
                     ActivityCompat.checkSelfPermission(
                         requireContext(),
                         Manifest.permission.ACCESS_COARSE_LOCATION
                     ) == PackageManager.PERMISSION_GRANTED
                 ) {
                     mMap.clear()
                     getDeviceLocation()
                 } else {
                     ActivityCompat.requestPermissions(
                         requireActivity(),
                         arrayOf(
                             Manifest.permission.ACCESS_FINE_LOCATION,
                             Manifest.permission.ACCESS_COARSE_LOCATION
                         ),
                         LOCATION_PERMISSION_REQUEST_CODE
                     )
                 }
             }
         }
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getDeviceLocation() {
        if (::fusedLocationClient.isInitialized &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        if (!isScanning) {
                            displayWifiOnMap(wifiManager.scanResults, LatLng(location.latitude, location.longitude))
                            moveCameraToLocation(LatLng(location.latitude, location.longitude))
                        }
                    }
                }
        }
    }
    private fun moveCameraToLocation(location: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f)) // You can adjust the zoom level as needed
    }


    private fun displayWifiOnMap(scanResults: List<ScanResult>, liveLocation: LatLng) {
        mMap.clear()

        val circleList = mutableListOf<Pair<Circle, ScanResult>>()
        val uniqueSSIDs = mutableSetOf<String>()

        for (result in scanResults) {
            val wifiName = result.SSID
            if (wifiName.isNotEmpty() && !uniqueSSIDs.contains(wifiName)) {
                uniqueSSIDs.add(wifiName)

                val wifiRange = calculateRadius(Math.abs(result.level))
                val offsetLatitude = (Math.random() - 0.5) * 0.009
                val offsetLongitude = (Math.random() - 0.5) * 0.009
                val wifiLatitude = liveLocation.latitude + offsetLatitude
                val wifiLongitude = liveLocation.longitude + offsetLongitude
                val wifiLatLng = LatLng(wifiLatitude, wifiLongitude)
                val circleOptions = CircleOptions()
                    .center(wifiLatLng)
                    .radius(wifiRange.toDouble())
                    .strokeWidth(2f)
                if (result.BSSID == wifiManager.connectionInfo.bssid) {
                    circleOptions.strokeColor(ContextCompat.getColor(requireContext(), R.color.red))
                    circleOptions.fillColor(ContextCompat.getColor(requireContext(), R.color.red))
                } else {
                    circleOptions.strokeColor(ContextCompat.getColor(requireContext(), R.color.green))
                    circleOptions.fillColor(ContextCompat.getColor(requireContext(), R.color.green))
                }
                val circle = mMap.addCircle(circleOptions)
                circleList.add(Pair(circle, result))
            }
        }

        mMap.setOnMapClickListener { latLng ->
            for ((circle, scanResult) in circleList) {
                val circleCenter = circle.center
                val distance = FloatArray(1)
                Location.distanceBetween(
                    circleCenter.latitude,
                    circleCenter.longitude,
                    latLng.latitude,
                    latLng.longitude,
                    distance
                )
                if (distance[0] < circle.radius) {
                    val wifiName = scanResult.SSID
                    val wifiSecurity = scanResult.capabilities
                    lifecycleScope.launch {
                        val closedPortsInfo = getClosedPortsInfo(scanResult)
                        val snippet =
                            "SSID: $wifiName\nSecurity: $wifiSecurity\nClosed Ports: $closedPortsInfo"
                        val truncatedSnippet = truncateTextToMaxLines(snippet, 5)
                        val markerOptions = MarkerOptions().position(circleCenter)
                            .title(wifiName)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .snippet(truncatedSnippet)
                        val marker = mMap.addMarker(markerOptions)
                        marker?.showInfoWindow()
                        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(requireContext()))
                    }
                    return@setOnMapClickListener
                }
            }
        }
    }

    private fun truncateTextToMaxLines(text: String, maxLines: Int): String {
        val lines = text.split("\n")
        if (lines.size > maxLines) {
            return lines.take(maxLines).joinToString("\n") + "\n..."
        }
        return text
    }

    private fun calculateRadius(signalStrength: Int): Double {
        return when {
            signalStrength >= -50 -> 50.0
            signalStrength >= -70 -> 100.0
            else -> 150.0
        }
    }

    private suspend fun getClosedPortsInfo(scanResult: ScanResult): String {
        val localIpAddress = getLocalIpAddress()
        return if (localIpAddress != null) {
            val ports = listOf(80, 443, 22) // Ports to check
            val timeout = 5000 // Timeout in milliseconds

            val results = checkPorts(localIpAddress, ports, timeout)
            val resultStringBuilder = StringBuilder()

            for ((port, isOpen) in results) {
                val result = if (isOpen) {
                    "Port $port is open on $localIpAddress"
                } else {
                    "Port $port is closed on $localIpAddress"
                }
                resultStringBuilder.append(result).append(", ")
            }

            resultStringBuilder.toString().trimEnd(',')
        } else {
            "Failed to retrieve the local IP address."
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                } else {
                    Toast.makeText(
                        requireContext(), "Please enable permission to use this feature", Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

        override fun getInfoContents(marker: Marker): View {
            val infoView =
                (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                    .inflate(R.layout.custom_info_window, null)

            val titleTextView = infoView.findViewById<TextView>(R.id.titleTextView)
            val snippetTextView = infoView.findViewById<TextView>(R.id.snippetTextView)
            val title = marker.title
            val snippet = marker.snippet

            titleTextView.text = title
            snippetTextView.text = snippet

            return infoView
        }
    }

    private suspend fun checkPorts(
        ip: String,
        ports: List<Int>,
        timeout: Int
    ): Map<Int, Boolean> {
        val results = mutableMapOf<Int, Boolean>()

        for (port in ports) {
            results[port] = checkPort(ip, port, timeout)
        }

        return results
    }

    private suspend fun checkPort(ip: String, port: Int, timeout: Int): Boolean =
        suspendCoroutine { continuation ->
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val socket = Socket()
                    socket.connect(InetSocketAddress(ip, port), timeout)
                    socket.close()
                    continuation.resume(true) // Port is open
                } catch (e: Exception) {
                    continuation.resume(false) // Port is closed or timeout
                }
            }
        }

    private suspend fun getLocalIpAddress(): String? =
        withContext(Dispatchers.IO) {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            return@withContext inetAddress.hostAddress
                        }
                    }
                }
                null
            } catch (ex: SocketException) {
                null
            }
        }
}