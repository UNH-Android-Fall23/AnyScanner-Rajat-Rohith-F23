package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var fbaseAuth: FirebaseAuth
    private lateinit var binding : ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        binding= ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView((binding.root))
        fbaseAuth=Firebase.auth


        binding.registerBtn.setOnClickListener{
            val email=binding.emailEt
            val password=binding.passwordEt
            registerUser(email.text.toString(),password.text.toString())

        }

    }

    private fun registerUser(email: String, password: String) {

        fbaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                    val user = fbaseAuth.currentUser
              //testusercaselike password should be 6 char and email should have @ and .com otherwise firebase would not accept
                if(user!=null){
                   goToqrActivity(view = null)
                }
                }
    }
    fun goToqrActivity(view: View?) {
        val intent = Intent(this, Qr_Activity::class.java)
        startActivity(intent)
    }
}