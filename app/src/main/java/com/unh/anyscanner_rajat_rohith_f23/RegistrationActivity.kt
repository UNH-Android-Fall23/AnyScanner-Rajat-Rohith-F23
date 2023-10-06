package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private lateinit var fbaseAuth: FirebaseAuth
    private val TAG = "Signup_Activity"
    private lateinit var binding : ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        binding= ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView((binding.root))
        fbaseAuth=Firebase.auth


        //on-click listener for registration
        binding.registerBtn.setOnClickListener{
            val email=binding.emailEt
            val password=binding.passwordEt

            //TO-Do conditions for password verification

            registerUser(email.text.toString(),password.text.toString())

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }



    private fun registerUser(email: String, password: String) {

        fbaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Registration Successfull")
                    val user = fbaseAuth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
    }

}