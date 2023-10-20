package com.unh.anyscanner_rajat_rohith_f23

import android.app.AlertDialog
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toast
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafeBrowsingThreat
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Tasks
import com.google.android.material.tabs.TabLayout.TabGravity
import java.security.SecureRandom


class QRFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var scanVal: String
    private val TAG="AnyScannerF23"




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_q_r, container, false)
    }

    private fun showQRResults(){
        val alertDialog: AlertDialog.Builder=AlertDialog.Builder(context)
        //TODO design custom layout
        alertDialog.setTitle("Depends on Threat type")
        alertDialog.setMessage(scanVal)
        val dialog: AlertDialog = alertDialog.create()
        dialog.show()
    }

    private fun checkURL(){
        //crashes on using safebrowsing as we dont have to initialize it we are just testing
        //Tasks.await(SafetyNet.getClient(requireContext()).initSafeBrowsing())


        SafetyNet.getClient(requireActivity()).lookupUri(
            "https:wicar.org",
            "AIzaSyBOs9JmoasE81MHrNIDymU9w2IwyvnvnIA",
            SafeBrowsingThreat.TYPE_POTENTIALLY_HARMFUL_APPLICATION,
            SafeBrowsingThreat.TYPE_SOCIAL_ENGINEERING
        )
            .addOnSuccessListener(requireActivity()) { sbResponse ->
                // Indicates communication with the service was successful.
                // Identify any detected threats.
                Log.d(TAG,"Hurray")

                if (sbResponse.detectedThreats.isEmpty()) {
                    // No threats found.
                    //TODO return to display safe url
                    Log.d(TAG,"Threats found are ${sbResponse.detectedThreats}")
                } else {
                    // Threats found!
                    Log.d(TAG,"Threats found are ${sbResponse.detectedThreats}")
                }
            }
            .addOnFailureListener(requireActivity()) { e: Exception ->
                if (e is ApiException) {
                    // An error with the Google Play Services API contains some
                    // additional details.
                    Log.d(TAG, "Error: ${CommonStatusCodes.getStatusCodeString(e.statusCode)}")

                    // Note: If the status code, s.statusCode,
                    // is SafetyNetStatusCode.SAFE_BROWSING_API_NOT_INITIALIZED,
                    // you need to call initSafeBrowsing(). It means either you
                    // haven't called initSafeBrowsing() before or that it needs
                    // to be called again due to an internal error.
                } else {
                    // A different, unknown type of error occurred.
                    Log.d(TAG, "Error: ${e.message}")
                }
            }

            
        /**/
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.camera=CodeScanner.CAMERA_BACK
        checkURL()
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                scanVal=it.text.toString()
                Toast.makeText(activity, scanVal, Toast.LENGTH_LONG).show()

                showQRResults()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}