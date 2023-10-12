package com.unh.anyscanner_rajat_rohith_f23

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.budiyev.android.codescanner.CodeScanner
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityAnyScannerBinding

class AnyScannerActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAnyScannerBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_any_scanner)

        binding = ActivityAnyScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //codeScanner= CodeScanner(this, binding.scannerView)

        val navView: BottomNavigationView = binding.bottomNavView

        //val navController = findNavController(R.id.activity_main_nav_host)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.qrFragment, R.id.wifiFragment
            )
        )

        //set runtime permission camera
       // activityResultLauncher.launch(arrayOf(android.Manifest.permission.CAMERA))

        //setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)
    }
}