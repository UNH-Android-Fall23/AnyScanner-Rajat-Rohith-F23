package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityMainBinding
import kotlinx.coroutines.tasks.await


class MainActivity : AppCompatActivity() {
    private lateinit var fbaseAuth: FirebaseAuth
    private val tag="idk"
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
            var passwordMatched = false
            val username = binding.editTextTextEmailAddress2
            val password = binding.editTextTextPassword
            if (username.text.toString() == "") {
                binding.editTextTextEmailAddress2.error = "Field Required"
            }
            if (password.text.toString() == "") {
                binding.editTextTextPassword.error = "Field Required"
            }
            val query = FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("Email", username.text.toString()).get()
            query.addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    binding.editTextTextEmailAddress2.error = "This Email needs sign up"
                } else {
                    for (document in documents) {
                        val userData = document.data
                        if (userData["Password"] != password.text.toString()) {
                            binding.editTextTextPassword.error = "Password does not match"
                        }
                        if (userData["Password"] == password.text.toString()) {
                            passwordMatched=true
                        }
                    }
                }
                if(passwordMatched){
                    fbaseAuth.signInWithEmailAndPassword(
                        username.text.toString(),
                        password.text.toString()
                    )
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = fbaseAuth.currentUser
                                if (user != null) {
                                    goToqrActivity(view = null)
                                }
                            } else {
                                when ((task.exception as FirebaseAuthException?)!!.errorCode) {
                                    "ERROR_INVALID_EMAIL" -> binding.editTextTextEmailAddress2.error =
                                        "INVALID EMAIL"

                                    "ERROR_WRONG_PASSWORD" -> binding.editTextTextPassword.error =
                                        "INVALID PASSWORD"

                                    else -> {
                                        binding.editTextTextEmailAddress2.error = "INVALID EMAIL"
                                        binding.editTextTextPassword.error = "INVALID PASSWORD"
                                    }
                                }
                            }
                        }
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
                //goToqrActivity(view = null)
                FirebaseAuth.getInstance().signOut()
            }
        }
    }

