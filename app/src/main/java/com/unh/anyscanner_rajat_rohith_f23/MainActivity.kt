package com.unh.anyscanner_rajat_rohith_f23

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.unh.anyscanner_rajat_rohith_f23.databinding.ActivityMainBinding
import androidx.biometric.BiometricPrompt


interface BiometricCallback {
    fun onBiometricValueReceived(biometricValue: Boolean, notifValue: Boolean, darkValue: Boolean)
}


class MainActivity : AppCompatActivity() {
    private lateinit var fbaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var email: String? = null
    private var b : Boolean = false
    private var n : Boolean = false
    private var d : Boolean = false
    private val db = FirebaseFirestore.getInstance()
    private val user = Firebase.auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView((binding.root))

        fbaseAuth = Firebase.auth
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        user?.let {
            email = it.email
            if(email==""){
                email = sharedPreferences.getString("user_email", null)
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Authenticate using your fingerprint")
            .setDescription("Touch the fingerprint sensor to log with user $email")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt =
            BiometricPrompt(this, mainExecutor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, errString, Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    goToqrActivity(view = null)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Biometric Login Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            })

        binding.materialTextView2.setOnClickListener {
            if (email != null && b) {
                biometricPrompt.authenticate(biometricPromptInfo)
            } else {
                Toast.makeText(this, "First Login Needed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.google.setOnClickListener {
            signInWithGoogle()
        }

        binding.registerText.setOnClickListener {
            goToREActivity(view = null)
        }

        binding.materialTextView.setOnClickListener {
            goToFPActivity(view = null)
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
            if (!isValidEmail(username.text.toString())) {
                binding.editTextTextEmailAddress2.error = "Enter a valid email"
            } else {
                val query = FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("Email", username.text.toString()).get()
                query.addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        binding.editTextTextEmailAddress2.error = "This Email needs sign up"
                    } else {
                        for (document in documents) {
                            val userData = document.data
                            if (userData["Password"] != password.text.toString()) {
                                binding.editTextTextPassword.error =
                                    "Password does not match"
                            }
                            if (userData["Password"] == password.text.toString()) {
                                passwordMatched = true
                            }
                        }
                    }
                    if (passwordMatched) {
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
                                            binding.editTextTextEmailAddress2.error =
                                                "INVALID EMAIL"
                                            binding.editTextTextPassword.error =
                                                "INVALID PASSWORD"
                                        }
                                    }
                                }
                            }
                    }
                }
            }

            //goToqrActivity(view = null)
        }
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    handleSignInResult(data)
                }
            }


    }


    override fun onBackPressed() {

    }

    fun goToqrActivity(view: View?) {
        val intent = Intent(this, AnyScannerActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToREActivity(view: View?) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    fun goToFPActivity(view: View?) {
        val intent = Intent(this, Forgot_Password_Verfifcation::class.java)
        startActivity(intent)
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    public override fun onStart() {
        super.onStart()

        fetchBiometricValue(object : BiometricCallback {
            override fun onBiometricValueReceived(biometricValue: Boolean, notifValue: Boolean, darkValue: Boolean) {
                b = biometricValue
                n = notifValue
                d = darkValue
                if (email != null && !b) {
                    goToqrActivity(view = null)
                }
                if (email != null && b) {
                    binding.materialTextView2.performClick()
                }
                if(n){
                    note()
                }
                if(d){
                    AppCompatDelegate
                        .setDefaultNightMode(
                            AppCompatDelegate
                                .MODE_NIGHT_YES);
                }
                if(!d){
                    AppCompatDelegate
                        .setDefaultNightMode(
                            AppCompatDelegate
                                .MODE_NIGHT_NO);
                }
            }
        })
    }

    private fun note() {
        val context = this
        ProfileFragment.scheduleRandomNotification(context)
    }

    private fun fetchBiometricValue(callback: BiometricCallback) {
        if (email != null) {
            val collectionReference = db.collection("UserProfile")
            val documentReference = collectionReference.document(user?.uid ?: "")
            documentReference.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documentSnapshot = task.result

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            val data = documentSnapshot.data
                            val b = (data?.get("biometric") ?: false) as Boolean
                            val n = (data?.get("notifications") ?: false) as Boolean
                            val d = (data?.get("darkMode") ?: false) as Boolean
                            callback.onBiometricValueReceived(b, n, d)
                        }
                    }
                }
        }
    }


    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            fbaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = fbaseAuth.currentUser
                        if (user != null) {
                            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("user_email", account.email)
                            editor.apply()
                            goToqrActivity(view = null)
                        }
                    }
                    else{
                        Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: ApiException) {
            Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
        }
    }
}


