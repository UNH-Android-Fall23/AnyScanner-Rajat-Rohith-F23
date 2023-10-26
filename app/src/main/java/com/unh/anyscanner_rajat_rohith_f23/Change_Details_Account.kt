package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityAccountInformationBinding
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityChangeDetailsAccountBinding

class Change_Details_Account : AppCompatActivity() {

    private lateinit var binding: ActivityChangeDetailsAccountBinding

    private var Mainemail: String? = null

    private val db = FirebaseFirestore.getInstance()

    lateinit var fbaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_details_account)

        binding = ActivityChangeDetailsAccountBinding.inflate(layoutInflater)
        setContentView((binding.root))

        fbaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            onBackPressed()
        }

        var docId: String? = ""
        var userData: Map<String, Any> = emptyMap()
        val user = Firebase.auth.currentUser

        user?.let {
            Mainemail = it.email
        }

        val userRef = db.collection("users")
            .whereEqualTo("Email", Mainemail)

        userRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot) {
                        docId = document.id
                        userData = document.data
                        binding.firstNameEt.setText(userData["FirstName"].toString())
                        binding.lastNameEt.setText(userData["LastName"].toString())
                    }
                }
            }
        binding.emailEt.setText(Mainemail)
        binding.registerBtn.setOnClickListener {
            val fname = binding.firstNameEt.text.toString()
            val lname = binding.lastNameEt.text.toString()
            val email = binding.emailEt.text.toString()

            if (fname == "") {
                binding.firstNameEt.error = "Field Required"
            }
            if (lname == "") {
                binding.lastNameEt.error = "Field Required"
            }
            if (email == "") {
                binding.emailEt.error = "Field Required"
            } else if (!isValidEmail(email)) {
                binding.emailEt.error = "Enter a valid email"
            } else {
                checkEmailExistsOrNot(email) { emailExists ->
                    if (emailExists) {
                        if(Mainemail==email) {
                            docId?.let { it1 ->
                                db.collection("users").document(it1).update("FirstName", fname)
                            }
                            docId?.let { it1 ->
                                db.collection("users").document(it1).update("LastName", lname)
                            }
                            goToAIActivity(view = null)
                        }
                        else{
                            binding.emailEt.error = "Email already signed up"
                        }
                    } else {
                        docId?.let { it1 ->
                            db.collection("users").document(it1).update("FirstName", fname)
                        }
                        docId?.let { it1 ->
                            db.collection("users").document(it1).update("LastName", lname)
                        }
                        docId?.let { it1 ->
                            db.collection("users").document(it1).update("Email", email)
                        }
                        user?.delete()
                        fbaseAuth.createUserWithEmailAndPassword(email, userData["Password"].toString())
                            .addOnCompleteListener(this) { task ->
                                val user = fbaseAuth.currentUser
                                if (user!=null) {
                                    goToAIActivity(view = null)
                                }
                            }
                    }
                }
            }
        }
    }
    fun checkEmailExistsOrNot(email: String, callback: (Boolean) -> Unit) {
        val userRef = db.collection("users")
            .whereEqualTo("Email", email)
        userRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }
    fun goToAIActivity(view: View?) {
        val intent = Intent(this, Account_Information::class.java)
        startActivity(intent)
        finish()
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}

