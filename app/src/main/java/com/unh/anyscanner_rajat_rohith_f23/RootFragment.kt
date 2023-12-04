package com.unh.anyscanner_rajat_rohith_f23

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.scottyab.rootbeer.RootBeer
import com.unh.anyscanner_rajat_rohith_f23.databinding.FragmentRootBinding

class RootFragment : Fragment() {

    private var _binding: FragmentRootBinding? = null
    private val binding get() = _binding!!
    private lateinit var logTextView: TextView
    private lateinit var rootBeer: RootBeer
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRootBinding.inflate(inflater, container, false)
        val rootView = binding.root
        logTextView = binding.rootUpdate
        rootBeer = RootBeer(requireContext())
        val videoView = binding.videoView
        binding.checkRootBtn.setOnClickListener {
            binding.imageView7.visibility = View.GONE
            val videoPath = "android.resource://com.unh.anyscanner_rajat_rohith_f23/${R.raw.video}"
            videoView.setVideoPath(videoPath)
            videoView.start()
            redirectLogs()

            videoView.setOnCompletionListener {
                checkRootStatus()
            }
        }
        return rootView
    }

    private val MAX_LINES = 8

    private fun redirectLogs() {
        val process = Runtime.getRuntime().exec("logcat -d")

        val bufferedReader = process.inputStream.bufferedReader()
        val logs = StringBuilder()
        rootBeer.isRooted

        var lineCount = 0

        bufferedReader.forEachLine { line ->
            if (line.contains("LOOKING FOR BINARY")) {
                logs.append(line).append("\n")

                lineCount++
                if (lineCount >= MAX_LINES) {
                    val index = logs.indexOf("\n")
                    logs.delete(0, index + 1)
                }
            }
        }

        updateLogTextView(logs.toString()) // Display logs in TextView
    }


    private fun updateLogTextView(logMessage: String) {
        logTextView.post {
            logTextView.text = logMessage // Update TextView with logs
        }
    }

    private fun checkRootStatus() {
        logTextView.text = ""
        val videoView = binding.videoView
        if (rootBeer.isRooted) {
            val videoPath = "android.resource://com.unh.anyscanner_rajat_rohith_f23/${R.raw.notsafe}"
            videoView.setVideoPath(videoPath)
            videoView.start()
            videoView.setOnCompletionListener {
                binding.imageView7.visibility = View.VISIBLE
            }
            logTextView.text="Device is Rooted"
        } else {
            val videoPath = "android.resource://com.unh.anyscanner_rajat_rohith_f23/${R.raw.safe}"
            videoView.setVideoPath(videoPath)
            videoView.start()
            videoView.setOnCompletionListener {
                binding.imageView7.visibility = View.VISIBLE
            }
            logTextView.text="Device is SAFE"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
