package com.unh.anyscanner_rajat_rohith_f23

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Preview : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isSignedIn = sharedPreferences.getBoolean("isSignedIn", false)

        if (isSignedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val buttonScan = findViewById<Button>(R.id.button6)
        buttonScan.setOnClickListener {
            buttonScan.visibility = View.INVISIBLE
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = QRFragment()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        val navView: BottomNavigationView = findViewById(R.id.bottomNavView)
        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.qrFragment -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Sign in required for this Feature!!!")
                        .setCancelable(false)
                        .setPositiveButton("Sign In") { dialog, _ ->
                            dialog.dismiss()
                            sharedPreferences.edit().putBoolean("isSignedIn", true).apply()
                            goTologinActivity(view = null)
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                            goToqrActivity(view = null)
                        }
                    val alertDialog = builder.create()
                    alertDialog.show()
                }
                R.id.wifiFragment -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Sign in required! For this Feature!!!")
                        .setCancelable(false)
                        .setPositiveButton("Sign In") { dialog, _ ->
                            dialog.dismiss()
                            sharedPreferences.edit().putBoolean("isSignedIn", true).apply()
                            goTologinActivity(view = null)
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                            goToqrActivity(view = null)
                        }
                    val alertDialog = builder.create()
                    alertDialog.show()
                }
                R.id.rootFragment -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Sign in required! For this Feature!!!")
                        .setCancelable(false)
                        .setPositiveButton("Sign In") { dialog, _ ->
                            dialog.dismiss()
                            sharedPreferences.edit().putBoolean("isSignedIn", true).apply()
                            goTologinActivity(view = null)
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                            goToqrActivity(view = null)
                        }
                    val alertDialog = builder.create()
                    alertDialog.show()
                }
                R.id.profileFragment -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Sign in required! For this Feature!!!")
                        .setCancelable(false)
                        .setPositiveButton("Sign In") { dialog, _ ->
                            dialog.dismiss()
                            sharedPreferences.edit().putBoolean("isSignedIn", true).apply()
                            goTologinActivity(view = null)
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                            goToqrActivity(view = null)
                        }
                    val alertDialog = builder.create()
                    alertDialog.show()
                }
            }
            true
        }
    }

    fun goToqrActivity(view: View?) {
        val intent = Intent(this, Preview::class.java)
        startActivity(intent)
        finish()
    }

    fun goTologinActivity(view: View?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {

    }
}
