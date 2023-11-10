package com.unh.anyscanner_rajat_rohith_f23

import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scottyab.rootbeer.RootBeer
import com.unh.anyscanner_rajat_rohith_f23.databinding.FragmentRootBinding


class RootFragment : Fragment() {


    private var _binding: FragmentRootBinding? = null
    private val binding get() = _binding!!
    private val TAG="AnyScannerF23"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRootBinding.inflate(inflater, container, false)
        binding.checkRootBtn.setOnClickListener {
            checkRootStatus()
        }
        return binding.root

    }
    private fun checkRootStatus() {
        val rootBeer = RootBeer(requireContext())

        if (rootBeer.isRooted) {
            // Device is rooted
            // Handle rooted device scenario
            Log.d(TAG,"rooted")
        } else {
            // Device is not rooted
            Log.d(TAG,"not-rooted ")

            // Handle non-rooted device scenario
        }
    }
}