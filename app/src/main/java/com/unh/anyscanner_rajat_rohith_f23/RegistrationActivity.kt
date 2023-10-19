package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityRegistrationBinding


class RegistrationActivity : AppCompatActivity() {

    private lateinit var fbaseAuth: FirebaseAuth
    private var db:FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var binding : ActivityRegistrationBinding
    val add = HashMap<String,Any>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        binding= ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView((binding.root))
        fbaseAuth=Firebase.auth


        binding.registerBtn.setOnClickListener{
            val fname=binding.firstNameEt
            val lname=binding.lastNameEt
            val email=binding.emailEt
            val password=binding.passwordEt
            val Repass=binding.repeatPasswordEt

            if(fname.text.toString()==""){
                binding.firstNameEt.error="Field Required"
            }
            if(lname.text.toString()==""){
                binding.lastNameEt.error="Field Required"
            }
            if(email.text.toString()==""){
                binding.emailEt.error="Field Required"
            }
            if(password.text.toString()==""){
                binding.passwordEt.error="Field Required"
            }
            if(Repass.text.toString()==""){
                binding.repeatPasswordEt.error="Field Required"
            }
            else if(!isValidEmail(email.text.toString())){
                binding.emailEt.error="Enter a valid email"
            }
            else if((password.text.toString().length)<=6){
                binding.passwordEt.error="Password should be greater than 6 characters"
            }
            else if(password.text.toString()!=Repass.text.toString()){
                binding.repeatPasswordEt.error="Password does not match"
            }
            else {
                registerUser(binding.firstNameEt.text.toString(),binding.lastNameEt.text.toString(),email.text.toString(), password.text.toString())
            }
        }

    }
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun registerUser(fname:String, lname:String, email: String, password: String) {

        fbaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                val user = fbaseAuth.currentUser
                if (task.isSuccessful) {
                    if (user != null) {
                        add["FirstName"]= fname
                        add["LastName"]= lname
                        add["Password"]= password
                        add["Email"]= email
                        db.collection("users").add(add)
                        goToqrActivity(view = null)
                    }
                } else {
                    binding.emailEt.error="Email already signed up"
                    }
                    }
                }
    fun goToqrActivity(view: View?) {
        val intent = Intent(this, AnyScannerActivity::class.java)
        startActivity(intent)
        finish()
    }
}