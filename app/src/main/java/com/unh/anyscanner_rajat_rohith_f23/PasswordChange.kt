package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityForgotPasswordVerfifcationBinding
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityPasswordChangeBinding

class PasswordChange : AppCompatActivity() {
    private lateinit var binding: ActivityPasswordChangeBinding
    lateinit var fbaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_change)
        binding = ActivityPasswordChangeBinding.inflate(layoutInflater)
        setContentView((binding.root))

        val email = intent.getStringExtra("email")
        val db = FirebaseFirestore.getInstance()
        var docId: String?=""
        var user: FirebaseUser? = null

        fbaseAuth = FirebaseAuth.getInstance()

        val query = FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("Email", email).get()
        query.addOnSuccessListener { documents ->
            for (document in documents) {
                val userData = document.data
                docId = document.id
                if (email != null) {
                    fbaseAuth.signInWithEmailAndPassword(email, userData["Password"].toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                user = fbaseAuth.currentUser
                            }
                        }
                }
            }
        }

        binding.button3.setOnClickListener {
            val newpass = binding.passwordEt.text.toString()
            val repass = binding.repeatPasswordEt.text.toString()
            if (newpass == "") {
                binding.passwordEt.error = "Field required"
            } else if (repass == "") {
                binding.repeatPasswordEt.error = "Field required"
            } else if (newpass.length <= 6) {
                binding.passwordEt.error = "Password should be greater than 6 characters"
            } else if (newpass != repass) {
                binding.repeatPasswordEt.error = "Password does not match"
            } else {
                docId?.let { it1 -> db.collection("users").document(it1).update("Password", newpass) }
                user!!.updatePassword(newpass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            goToqrActivity(view = null)
                        }
                    }
            }
        }
    }

    fun goToqrActivity(view: View?) {
        val intent = Intent(this, Qr_Activity::class.java)
        startActivity(intent)
        finish()
    }
}