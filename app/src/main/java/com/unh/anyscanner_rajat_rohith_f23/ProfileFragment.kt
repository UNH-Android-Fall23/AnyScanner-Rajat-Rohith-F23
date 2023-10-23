package com.unh.anyscanner_rajat_rohith_f23

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.anyscanner_rajat_rohith_f23.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        var b=false
        var d=false
        var n=false

        db.collection("UserProfile")
            .document(user?.uid ?: "")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userProfile = document.toObject(UserProfile::class.java)
                    if (userProfile != null) {
                        binding.switch5.isChecked = userProfile.biometric
                        binding.switch4.isChecked = userProfile.darkMode
                        binding.switch3.isChecked = userProfile.notifications
                    }
                }
            }

        binding.switch5.setOnCheckedChangeListener { buttonView, isChecked ->
        b=isChecked
            val userProfile = UserProfile(b,d,n)

            db.collection("UserProfile")
                .document(user?.uid ?: "")
                .set(userProfile)
        }

        binding.switch4.setOnCheckedChangeListener { buttonView, isChecked ->
            d=isChecked
            val userProfile = UserProfile(b,d,n)

            db.collection("UserProfile")
                .document(user?.uid ?: "")
                .set(userProfile)
        }

        binding.switch3.setOnCheckedChangeListener { buttonView, isChecked ->
            n=isChecked
            val userProfile = UserProfile(b,d,n)

            db.collection("UserProfile")
                .document(user?.uid ?: "")
                .set(userProfile)
        }

        binding.button8.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            goToLoginActivity(view = null)
        }
        binding.textView10.setOnClickListener {
            goToAIActivity(view = null)
        }
        binding.textView12.setOnClickListener {
            goToAIActivity(view = null)
        }

    }

    fun goToLoginActivity(view: View?) {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
    fun goToAIActivity(view: View?) {
        val intent = Intent(requireContext(), Account_Information::class.java)
        startActivity(intent)
    }
}

data class UserProfile(
    val biometric: Boolean = false,
    val darkMode: Boolean = false,
    val notifications: Boolean = false
)
