package com.unh.anyscanner_rajat_rohith_f23

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.scottyab.rootbeer.RootBeer
import com.unh.anyscanner_rajat_rohith_f23.databinding.FragmentRootBinding
import java.io.OutputStream
import java.io.PrintStream


class RootFragment : Fragment() {


    private var _binding: FragmentRootBinding? = null
    private val binding get() = _binding!!
    private val TAG="AnyScannerF23"
    private lateinit var logTextView: TextView
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRootBinding.inflate(inflater, container, false)
        logTextView = binding.rootUpdate
        binding.checkRootBtn.setOnClickListener {
            animateButton()
            redirectLogs()
            checkRootStatus()
        }
        return binding.root

    }
    private fun redirectLogs() {
     //TODO set text view to fetch update from rootbeer logs

    }

    private fun updateLogTextView(logMessage: String) {
        Log.d(TAG, "Updating log text: $logMessage")
        handler.post {
            val logText = "${logTextView.text}\n$logMessage"
            logTextView.text = logText
        }
    }
    private fun checkRootStatus() {
        val rootBeer = RootBeer(requireContext())
        if (rootBeer.isRooted) {
            // Device is rooted
            // Handle rooted device scenario
            logTextView.text = "Device is Rooted"
        } else {
            // Device is not rooted
            logTextView.text = "Device is Not-Rooted"
        }
    }

    private fun animateButton() {
        // Load the scale animation from the resources
        val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_animation)

        // Start the animation on the button
        binding.checkRootBtn.startAnimation(scaleAnimation)
    }
}