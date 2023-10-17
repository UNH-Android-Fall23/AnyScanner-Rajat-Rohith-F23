package com.unh.anyscanner_rajat_rohith_f23

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.budiyev.android.codescanner.CodeScanner
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityAnyScannerBinding

class AnyScannerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnyScannerBinding
    private lateinit var codeScanner: CodeScanner
    private val TAG = "AnyScannerTag"
    private val navController: NavController
        get() = findNavController(R.id.activity_main_nav_host)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnyScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //codeScanner= CodeScanner(this, binding.scannerView)


        val navView: BottomNavigationView = binding.bottomNavView

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            destination.label = when (destination.id) {
                R.id.qrFragment -> resources.getString(R.string.app_name)
                R.id.wifiFragment -> resources.getString(R.string.app_name)
                R.id.rootFragment -> resources.getString(R.string.app_name)
                R.id.profileFragment -> resources.getString(R.string.app_name)
                else -> resources.getString(R.string.app_name)
            }

            // val navController =  findNavController(R.id.activity_main_nav_host)

            //actionBar?.title = "Name"

            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.qrFragment, R.id.wifiFragment, R.id.rootFragment, R.id.profileFragment
                )
            )


            //set runtime permission camera
            //activityResultLauncher.launch(arrayOf(android.Manifest.permission.CAMERA))
            //setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

}
