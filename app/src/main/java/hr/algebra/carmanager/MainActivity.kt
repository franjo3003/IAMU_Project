package hr.algebra.carmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import hr.algebra.carmanager.databinding.ActivityMainBinding
import hr.algebra.carmanager.ui.fragment.*
import androidx.appcompat.app.AppCompatDelegate
import hr.algebra.carmanager.data.prefs.PreferenceManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import hr.algebra.carmanager.worker.RegistrationReminderWorker
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import hr.algebra.carmanager.ui.fragment.CarDetailsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestNotificationPermission()
        startRegistrationReminderWorker()

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            openFragment(CarListFragment())
        }

        handleNotificationIntent()

        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.nav_cars -> openFragment(CarListFragment())

                R.id.nav_add -> openFragment(AddEditCarFragment())

                R.id.nav_settings -> openFragment(SettingsFragment())

                R.id.nav_about -> openFragment(AboutFragment())
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun applySavedTheme() {
        val preferenceManager = PreferenceManager(this)

        val mode = when (preferenceManager.getThemeMode()) {
            PreferenceManager.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            PreferenceManager.THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun startRegistrationReminderWorker() {
        val request = OneTimeWorkRequestBuilder<RegistrationReminderWorker>()
            .build()

        WorkManager.getInstance(this).enqueue(request)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    100
                )
            }
        }
    }

    private fun handleNotificationIntent() {
        val carId = intent.getLongExtra("open_car_id", -1)

        if (carId != -1L) {
            val fragment = CarDetailsFragment().apply {
                arguments = Bundle().apply {
                    putLong("car_id", carId)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }
    }
}