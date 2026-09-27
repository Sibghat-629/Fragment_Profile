package com.example.fragment_profile

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.fragment_profile.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var receiver: MyReceiver
    private var statusLogHistory = "Lifecycle: onCreate()\n"

    private val userListFragment = UserListFragment()
    private val userFormFragment = UserFormFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0) // Leave bottom for navigation bar or handle insets
            insets
        }

        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(userListFragment)
        }

        receiver = MyReceiver { logMsg ->
            logStatus("Broadcast: $logMsg")
            profileFragment.appendStatusLog(logMsg)
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction("com.example.fragment_profile.TEST_BROADCAST")
        }
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_users -> {
                    loadFragment(userListFragment)
                    true
                }
                R.id.nav_form -> {
                    loadFragment(userFormFragment)
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    fun navigateToTab(itemId: Int) {
        binding.bottomNavigationView.selectedItemId = itemId
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun getStatusLogs(): String = statusLogHistory

    override fun onStart() { super.onStart(); logStatus("Lifecycle: onStart()") }
    override fun onResume() { super.onResume(); logStatus("Lifecycle: onResume()") }
    override fun onPause() { super.onPause(); logStatus("Lifecycle: onPause()") }
    override fun onStop() { super.onStop(); logStatus("Lifecycle: onStop()") }
    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(receiver) } catch (_: Exception) {}
    }

    private fun logStatus(status: String) {
        statusLogHistory = "$status\n$statusLogHistory"
    }
}
