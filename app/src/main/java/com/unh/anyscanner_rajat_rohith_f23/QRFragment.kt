package com.unh.anyscanner_rajat_rohith_f23

import android.app.AlertDialog
import android.os.Bundle
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
    private var scanVal: String = ""
    private val TAG="AnyScannerF23"
    private val apiKey = "408ac8b1b8565046b21e57b71c2ddb72e10bf0e23745cc21fc8cc68a5ef65292"
    private val client = OkHttpClient()




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_q_r, container, false)
    }

    private fun showQRResults(message: String){
        val alertDialog: AlertDialog.Builder=AlertDialog.Builder(context)
        //TODO design custom layout
        alertDialog.setTitle(scanVal)
        alertDialog.setMessage(message)
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
                    if(harmlessCount>maliciousCount){
                        scanVal="Harmless"
                    }else if(maliciousCount>harmlessCount){
                        scanVal="Malicious"
                    }
                    Log.d(TAG, "Harmless and mal counts are $harmlessCount and $maliciousCount")
                } else {
                    Log.e(TAG, "Request failed with code: ${response.code}")
                    scanVal="Error"
                }
            }
        })

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
                showQRResults(url)
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