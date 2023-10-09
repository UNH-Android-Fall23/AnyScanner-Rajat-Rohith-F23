package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var fbaseAuth: FirebaseAuth
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView((binding.root))
        fbaseAuth = Firebase.auth

        binding.registerText.setOnClickListener {
            goToREActivity(view = null)
        }

        binding.loginBtn.setOnClickListener {
            val username = binding.usernameEt
            val password = binding.passwordEt
            fbaseAuth.signInWithEmailAndPassword(username.toString(), password.toString())
                .addOnCompleteListener(this) { task ->
                        val user = fbaseAuth.currentUser
                    if(user!=null){

                        goToqrActivity(view = null)
                    }
                    }
                }
        }
    fun goToqrActivity(view: View?) {
        val intent = Intent(this, Qr_Activity::class.java)
        startActivity(intent)
    }
    fun goToREActivity(view: View?) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = fbaseAuth.currentUser
        if (currentUser != null) {
        //  goToqrActivity(view = null)
        }
    }
}