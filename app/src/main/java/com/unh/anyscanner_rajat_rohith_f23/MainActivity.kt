package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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


class MainActivity : AppCompatActivity() {
    private lateinit var fbaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private val TAG= "AnyScannerTag"
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var launcher: ActivityResultLauncher<Intent>
    //private var key: String = BuildConfig.default_web_client_id
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView((binding.root))

        fbaseAuth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Authenticate using your fingerprint")
            .setDescription("Touch the fingerprint sensor to log in to AnyScanner")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(this, mainExecutor, object : BiometricPrompt.AuthenticationCallback() {
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
                Toast.makeText(applicationContext, "Biometric Login Failed", Toast.LENGTH_SHORT).show()
            }
        })

        binding.materialTextView2.setOnClickListener {
            biometricPrompt.authenticate(biometricPromptInfo)
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
                    /*
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
                    */
                    goToqrActivity(view = null)
                    Log.d(TAG,"Anyscanner opened")
                }
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    handleSignInResult(data)
                }
            }
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
        val currentUser = fbaseAuth.currentUser
        if (currentUser != null) {
            goToqrActivity(view = null)
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

