package com.unh.anyscanner_rajat_rohith_f23

import android.R.color
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.Base64


class QRFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    var scanVal: String = ""
    var risk: String= ""
    var urlScanned: String=" "
    var isMalicious:Boolean=false

    private val TAG="AnyScannerF23"
    private val apiKey = "408ac8b1b8565046b21e57b71c2ddb72e10bf0e23745cc21fc8cc68a5ef65292"
    private val client = OkHttpClient()
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_q_r, container, false)
    }

    private fun showQRResults(result:String, severity: String, url: String, isMalicious: Boolean){
        val alertDialog: AlertDialog.Builder=AlertDialog.Builder(context)
        //TODO design custom layout
        val view = layoutInflater.inflate(R.layout.dialog_custom_qr,null)
        val title=view.findViewById<TextView>(R.id.text1)
        val risk=view.findViewById<TextView>(R.id.text2)
        val urlLink=view.findViewById<TextView>(R.id.text3)
        val exitButton=view.findViewById<Button>(R.id.dialogDismiss_button)
        val visitButton=view.findViewById<Button>(R.id.dialogPositive_button)
        alertDialog.setView(view)
        if(result=="Harmless"){
            title.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }else{
            title.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        title.setText("This QR is $result")
        risk.setText("Severity is $severity")
        urlLink.setText("Scanned URL is $url")
        val dialog: AlertDialog = alertDialog.create()
        //Code that adds the data to firebase
        val firestore = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val userDocRef = firestore.collection("QR").document(user.uid).collection("links").document()
            val timestamp = FieldValue.serverTimestamp()
            val linkData = hashMapOf(
                "url" to url,
                "isMalicious" to isMalicious,
                "timestamp" to timestamp
            )

            userDocRef.set(linkData)
                .addOnSuccessListener {
                    Log.d(TAG, "Link added for user: ${user.uid}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding link", e)
                }
        }

        exitButton.setOnClickListener() {
            dialog.dismiss()
            }
        visitButton.setOnClickListener {
            Log.d(TAG,"url is $url")
            var urlRefined = url
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                urlRefined = "http://$url"
                Log.d(TAG, "here")
            }
            Log.d(TAG,"url refined= $urlRefined")
            // Should I Check if there's an app to handle the intent before starting the activity
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlRefined))
            startActivity(intent)


        }
        dialog.show()
    }

    private fun checkURL(analysisId: String){
        val request = Request.Builder()
            .url("https://www.virustotal.com/api/v3/urls/$analysisId")
            .get()
            .header("User-Agent", "VirusTotal")
            .addHeader("accept", "application/json")
            .addHeader("x-apikey", apiKey)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle request failure
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    // Parse and process the URL analysis report as needed
                    val data = JSONObject(responseBody)
                    // Extract "harmless" and "malicious" values
                    Log.d(TAG,"Response data ${data.toString()}")

                    val attributes = data.getJSONObject("data")
                        .getJSONObject("attributes")
                    val harmlessCount = attributes.getJSONObject("total_votes").getInt("harmless")
                    val maliciousCount = attributes.getJSONObject("total_votes").getInt("malicious")
                    val lastVotes=attributes.getJSONObject("last_analysis_stats")
                    val lastharmlessCount = lastVotes.getInt("harmless")
                    val lastmaliciousCount = lastVotes.getInt("malicious")
                    urlScanned=attributes.getString("url")
                    Log.d(TAG,"Report attributes ${data.getJSONObject("data").toString()}")
                    if (attributes.has("crowdsourced_context")) {
                        val crowdsourcedContextArray = attributes.getJSONArray("crowdsourced_context")
                        val firstContextObject = crowdsourcedContextArray.getJSONObject(0)
                        Log.d(TAG,"crowd obj is ${firstContextObject.toString()}")
                         risk=firstContextObject.getString("severity")
                    }else{
                         risk="Not Available"
                    }
                    Log.d(TAG, "Mal count $maliciousCount and lastmalicous count $lastmaliciousCount")
                    if(maliciousCount<harmlessCount || lastmaliciousCount<2 ||risk=="low"){
                        scanVal="Harmless"
                        isMalicious=false
                    }
                    if(maliciousCount>harmlessCount || lastmaliciousCount >2 ||risk=="high"){
                        scanVal="Malicious"
                        isMalicious=true
                    }
                    Log.d(TAG,"Scan and url are: $scanVal and $urlScanned")
                } else {
                    Log.e(TAG, "Request failed with code: ${response.code}")
                    scanVal="Error"
                    risk="Error"
                   urlScanned="Error"
                }
            }
        })
        handler.postDelayed({
            showQRResults(scanVal,risk,urlScanned, isMalicious)
        },3500)

        /**/
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        activityResultLauncher.launch(arrayOf(android.Manifest.permission.CAMERA))
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.camera=CodeScanner.CAMERA_BACK
        codeScanner.formats= listOf(BarcodeFormat.QR_CODE)
        //val url = "https://www.google.com/"
        //val urlId = Base64.getUrlEncoder().withoutPadding().encodeToString(url.toByteArray( StandardCharsets.UTF_8))
        //checkURL(urlId)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                val url = it.text.toString()
                val urlId = Base64.getUrlEncoder().withoutPadding().encodeToString(url.toByteArray(
                    StandardCharsets.UTF_8))
                checkURL(urlId)
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

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    //setup QR scanner
                } else {
                    Toast.makeText(
                        requireContext(), "Please enable permission to use this feature", Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

}



