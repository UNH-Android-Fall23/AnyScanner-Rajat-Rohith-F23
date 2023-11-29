import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unh.anyscanner_rajat_rohith_f23.databinding.FragmentWifiDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume

class WifiDetailsFragment : Fragment() {

    private var _binding: FragmentWifiDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var connectedSSID: String
    private lateinit var selectedSSID: String
    private lateinit var selectedCapability: String
    private lateinit var portVal: String


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

        GlobalScope.launch {
            val localIpAddress = getLocalIpAddress()
            if (localIpAddress != null) {
                val ports = listOf(80, 443, 22) // Replace with the target ports
                val timeout = 5000 // Replace with your desired timeout in milliseconds

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

                val finalResult = resultStringBuilder.toString().trimEnd(',')
                portVal=finalResult
            } else {
                println("Failed to retrieve the local IP address.")
            }

            // Display details in the UI
            withContext(Dispatchers.Main) {
                binding.wifiSSIDTextView.text = "Wi-Fi SSID: $selectedSSID" ?: "No WiFi connected"
                binding.wifiSecurityTextView.text =
                    "Wi-Fi Security: $selectedCapability" ?: "No WiFi connected"
                binding.wifiDetailsTextView.text=portVal
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
