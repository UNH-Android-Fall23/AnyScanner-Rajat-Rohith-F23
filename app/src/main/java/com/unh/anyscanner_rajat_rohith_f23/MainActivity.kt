package com.unh.anyscanner_rajat_rohith_f23

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var fbaseAuth: FirebaseAuth
    private val TAG = "Login_Signup"
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView((binding.root))
        //initializing Firebase Authorizer
        fbaseAuth=Firebase.auth

        binding.loginBtn.setOnClickListener{
            val username=binding.usernameEt
            val password=binding.passwordEt

            accountLogin(username.text.toString(),password.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser=fbaseAuth.currentUser
        if (currentUser!=null){
            reload()
        }
    }



    private fun accountLogin(username: String, password: String){
        fbaseAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    Log.d(TAG, "Login Successfull!")
                    val user=fbaseAuth.currentUser
                    updateUI(user)
                }

            }
    }

    private fun reload() {
        TODO("Not yet implemented")
    }
    private fun updateUI(user: FirebaseUser?) {
    }
}