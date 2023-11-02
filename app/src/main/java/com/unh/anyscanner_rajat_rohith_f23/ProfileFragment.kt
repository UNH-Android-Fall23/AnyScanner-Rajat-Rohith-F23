package com.unh.anyscanner_rajat_rohith_f23

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.anyscanner_rajat_rohith_f23.databinding.FragmentProfileBinding
import java.util.Calendar
import java.util.Random


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val notificationId = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()
        var b = false
        var d = false
        var n = false

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
            b = isChecked
            val userProfile = UserProfile(b, d, n)

            db.collection("UserProfile")
                .document(user?.uid ?: "")
                .set(userProfile)
        }

        binding.switch4.setOnCheckedChangeListener { buttonView, isChecked ->
            d = isChecked
            val userProfile = UserProfile(b, d, n)

            db.collection("UserProfile")
                .document(user?.uid ?: "")
                .set(userProfile)
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

        binding.switch3.setOnCheckedChangeListener { buttonView, isChecked ->
            n = isChecked
            val userProfile = UserProfile(b, d, n)

            db.collection("UserProfile")
                .document(user?.uid ?: "")
                .set(userProfile)

            if (n) {
                if (checkPermission()) {
                    context?.let { scheduleRandomNotification(it) }
                } else {
                    binding.switch3.isChecked = false
                    Toast.makeText(
                        requireContext(),
                        "Need Notification Permission",
                        Toast.LENGTH_SHORT
                    ).show()
                    requestPermission()
                }
            }
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission(): Boolean {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        val granted = PackageManager.PERMISSION_GRANTED
        return ContextCompat.checkSelfPermission(requireContext(), permission) == granted
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            0
        )
    }

    fun goToLoginActivity(view: View?) {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    fun goToAIActivity(view: View?) {
        val intent = Intent(requireContext(), Account_Information::class.java)
        startActivity(intent)
    }
    companion object {
        fun scheduleRandomNotification(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val random = Random()
            val randomHours = random.nextInt(24)
            val randomMinutes = random.nextInt(60)

            val calendar = Calendar.getInstance()
            //calendar.add(Calendar.HOUR_OF_DAY, randomHours)
            //calendar.add(Calendar.MINUTE, randomMinutes)
            calendar.add(Calendar.SECOND, 1)

            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    class NotificationReceiver : BroadcastReceiver() {
        var profileFragment: ProfileFragment? = null
        private val notificationChannelId = "notification_channel_id"
        private val notificationId = 1

        fun assignProfileFragment(fragment: ProfileFragment) {
            profileFragment = fragment
        }

        override fun onReceive(context: Context, intent: Intent) {
            createNotificationChannel(context)
            showNotification(context)
        }
        private fun createNotificationChannel(context: Context) {
            val name = "Checking For root"
            val descriptionText = "If found any, shown in notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannelId = "notification_channel_id"
            val channel = NotificationChannel(notificationChannelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        private fun showNotification(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val builder = NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(R.drawable.screenshot_2023_10_09_at_9_00_04_am)
                .setContentTitle("Hello!")
                .setContentText("Check your Device, It may be rooted")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notify(notificationId, builder.build())
            }
        }
    }

}

data class UserProfile(
    val biometric: Boolean = false,
    val darkMode: Boolean = false,
    val notifications: Boolean = false
)
