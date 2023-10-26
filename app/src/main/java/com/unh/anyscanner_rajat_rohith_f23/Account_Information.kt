package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityAccountInformationBinding
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityMainBinding

class Account_Information : AppCompatActivity() {

    private var email: String? = null
    private val db = FirebaseFirestore.getInstance()


    private lateinit var binding: ActivityAccountInformationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_information)

        binding = ActivityAccountInformationBinding.inflate(layoutInflater)
        setContentView((binding.root))

        var googleprovider = true

        val user = Firebase.auth.currentUser
        user?.let {
             email = it.email
        }

        val userRef = db.collection("users")
            .whereEqualTo("Email", email)

        userRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot) {
                        binding.textView17.text = document.getString("FirstName")
                        binding.textView18.text = document.getString("LastName")
                        binding.textView21.text = email
                        googleprovider=false
                    }
                } else {
                    googleprovider=true
                }
            }.addOnFailureListener { googleprovider=true }

        if(googleprovider){
            user?.let {
                for (profile in it.providerData) {
                    val i = profile.displayName
                    val(fn,ln)= split(i)
                    binding.textView17.text = fn
                    binding.textView18.text = ln
                    binding.textView21.text = profile.email
                }
            }
        }

        binding.button4.setOnClickListener {
            if(googleprovider){
                Toast.makeText(this, "Google Sign in Used; Can't Change Details", Toast.LENGTH_SHORT).show()
            }
            else{
            goToCDActivity(view = null)
            }
        }

        binding.button5.setOnClickListener {
            if(googleprovider){
                Toast.makeText(this, "Google Sign in Used; Can't Change Details", Toast.LENGTH_SHORT).show()
            }
            else{
                goToFPActivity(view = null)
            }
        }
        binding.textView.setOnClickListener {
            onBackPressed()
        }
    }
    fun split(inputString: String?): Pair<String, String> {
        val parts = inputString?.split(" ", limit = 2)
        if (parts != null) {
            return Pair(parts?.get(0) ?:"", if (parts.size > 1) parts[1] else "")
        }
        return Pair("No Name Provided", "No Name Provided")
    }
    fun goToFPActivity(view: View?) {
        val intent = Intent(this, Forgot_Password_Verfifcation::class.java)
        startActivity(intent)
        finish()
    }
    fun goToCDActivity(view: View?) {
        val intent = Intent(this, Change_Details_Account::class.java)
        startActivity(intent)
        finish()
    }
}