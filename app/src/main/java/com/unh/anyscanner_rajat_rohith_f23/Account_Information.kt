package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityAccountInformationBinding
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityMainBinding

class Account_Information : AppCompatActivity() {
    private lateinit var binding: ActivityAccountInformationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_information)

        binding = ActivityAccountInformationBinding.inflate(layoutInflater)
        setContentView((binding.root))

        binding.button5.setOnClickListener {
            goToFPActivity(view = null)
        }
        binding.textView.setOnClickListener {
            onBackPressed()
        }
    }
    fun goToFPActivity(view: View?) {
        val intent = Intent(this, Forgot_Password_Verfifcation::class.java)
        startActivity(intent)
    }
}