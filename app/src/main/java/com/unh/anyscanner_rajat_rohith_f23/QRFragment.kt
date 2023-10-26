package com.unh.anyscanner_rajat_rohith_f23

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.Base64
import okhttp3.*
import org.json.JSONObject



class QRFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    var scanVal: String = ""
    var urlScanned: String=" "
    private val TAG="AnyScannerF23"
    private val apiKey = "408ac8b1b8565046b21e57b71c2ddb72e10bf0e23745cc21fc8cc68a5ef65292"
    private val client = OkHttpClient()
    private val handler = Handler(Looper.getMainLooper())




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_q_r, container, false)
    }

    private fun showQRResults(title:String, message: String){
        val alertDialog: AlertDialog.Builder=AlertDialog.Builder(context)
        //TODO design custom layout
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setNegativeButton("Close") { dialog, _ ->
            dialog.dismiss() // Close the dialog
        }
        alertDialog.setPositiveButton("Visit") { _, _ ->
            // Create an Intent to open the URL in a web browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(message))

            // Check if there's an app to handle the intent before starting the activity
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                requireActivity().startActivity(intent)
            } else {
                // Handle the case where there's no app to handle the intent
                Toast.makeText(context, "No app to handle this URL.", Toast.LENGTH_SHORT).show()
            }
        }
        val dialog: AlertDialog = alertDialog.create()
        dialog.show()
    }

    private fun checkURL(analysisId: String){
        val request = Request.Builder()
            .url("https://www.virustotal.com/api/v3/urls/$analysisId")
            .get()
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
                    val totalVotes = data.getJSONObject("data")
                        .getJSONObject("attributes")
                        .getJSONObject("total_votes")
                    val harmlessCount = totalVotes.getInt("harmless")
                    val maliciousCount = totalVotes.getInt("malicious")
                    val lastVotes=data.getJSONObject("data")
                        .getJSONObject("attributes")
                        .getJSONObject("last_analysis_stats")
                    val lastharmlessCount = lastVotes.getInt("harmless")
                    val lastmaliciousCount = lastVotes.getInt("malicious")
                    urlScanned=data.getJSONObject("data")
                        .getJSONObject("attributes").getString("url")

                    if(harmlessCount>maliciousCount || lastharmlessCount>lastmaliciousCount){
                        scanVal="Harmless"
                    }else if(maliciousCount>harmlessCount || lastmaliciousCount>lastharmlessCount){
                        scanVal="Malicious"
                    }

                    Log.d(TAG, "Harmless and mal counts are $harmlessCount and $maliciousCount")
                    Log.d(TAG,"Scan and url $scanVal and $urlScanned")
                } else {
                    Log.e(TAG, "Request failed with code: ${response.code}")
                    scanVal="Error"
                   urlScanned="error"
                }

            }
        })
        Log.d(TAG,"Scan and url outer $scanVal and $urlScanned")
        handler.postDelayed({
            showQRResults(scanVal,urlScanned)
        },3000)

        /**/
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.camera=CodeScanner.CAMERA_BACK
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                val url = it.text.toString()
                val urlId = Base64.getUrlEncoder().withoutPadding().encodeToString(url.toByteArray(
                    StandardCharsets.UTF_8))
                checkURL(urlId)
                Toast.makeText(activity, url, Toast.LENGTH_LONG).show()
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