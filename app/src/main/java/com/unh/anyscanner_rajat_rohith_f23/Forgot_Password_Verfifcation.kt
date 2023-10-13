package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityForgotPasswordVerfifcationBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Properties
import java.util.Random
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class Forgot_Password_Verfifcation : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordVerfifcationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_verfifcation)
        binding = ActivityForgotPasswordVerfifcationBinding.inflate(layoutInflater)
        setContentView((binding.root))

        lateinit var i: String
        val rnd = Random()
        val number: Int = rnd.nextInt(999999)
        i=number.toString()
        lateinit var email: String

        binding.button.setOnClickListener {
            email = binding.editTextTextEmailAddress.text.toString()
            if(email==""){
                binding.editTextTextEmailAddress.error="Field Required"
            }
            if (!isValidEmail(email)) {
                binding.editTextTextEmailAddress.error = "Enter a valid email"
            } else {
                val query = FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("Email", email).get()
                query.addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        binding.editTextTextEmailAddress.error = "This Email needs sign up"
                    } else {
                        sendEmail(email,i)
                    }
                }
            }
        }
        binding.button2.setOnClickListener{
            val j=binding.editTextNumber.text.toString()
            if(j==i){
                goToPCActivity(view = null,email)
            }
            else{
                binding.editTextNumber.error="Code Does not match"
            }
        }

    }


    private fun goToPCActivity(view: Nothing?,email: String) {
        val intent = Intent(this, PasswordChange::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    fun sendEmail(email: String,i:String) {
        GlobalScope.launch(Dispatchers.IO) {
            val fromEmail = "anyscanner1@gmail.com"
            val password = "xugz ukmx xgom puft"

            val props = Properties()
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.starttls.enable", "true")
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.port", "587")

            val session: Session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(fromEmail, password)
                }
            })

            try {
                val message = MimeMessage(session)

                message.setFrom(InternetAddress(fromEmail))
                message.addRecipient(Message.RecipientType.TO, InternetAddress(email))

                message.subject = "AnyScanner"
                message.setText("Hello, The code requested for password change: $i")
                Transport.send(message)
                binding.textView6.text = "CODE SENT"
            } catch (e: MessagingException) {
                e.printStackTrace()
                binding.textView6.text = e.message
            }
        }
    }
    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}