package hr.algebra.carmanager.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import hr.algebra.carmanager.R
import hr.algebra.carmanager.data.prefs.PreferenceManager
import hr.algebra.carmanager.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferenceManager: PreferenceManager

    private lateinit var fuelLabels: List<String>
    private lateinit var fuelValues: List<String>
    private lateinit var themeLabels: List<String>
    private lateinit var themeValues: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        preferenceManager = PreferenceManager(requireContext())

        fuelLabels = resources.getStringArray(R.array.fuel_filter_labels).toList()
        fuelValues = resources.getStringArray(R.array.fuel_filter_values).toList()
        themeLabels = resources.getStringArray(R.array.theme_labels).toList()
        themeValues = resources.getStringArray(R.array.theme_values).toList()

        setupNotificationsSwitch()
        setupFuelSpinner()
        setupThemeSpinner()
    }

    private fun setupNotificationsSwitch() {
        binding.swNotifications.isChecked = preferenceManager.areNotificationsEnabled()

        binding.swNotifications.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.setNotificationsEnabled(isChecked)

            Toast.makeText(
                requireContext(),
                if (isChecked) getString(R.string.notifications_on)
                else getString(R.string.notifications_off),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupFuelSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            fuelLabels
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFuelFilter.adapter = adapter

        val savedValue = preferenceManager.getDefaultFuelFilter()
        val index = fuelValues.indexOf(savedValue).takeIf { it >= 0 } ?: 0

        binding.spFuelFilter.setSelection(index)

        binding.spFuelFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                preferenceManager.setDefaultFuelFilter(fuelValues[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun setupThemeSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            themeLabels
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spThemeMode.adapter = adapter

        val savedValue = preferenceManager.getThemeMode()
        val index = themeValues.indexOf(savedValue).takeIf { it >= 0 } ?: 0

        binding.spThemeMode.setSelection(index)

        binding.spThemeMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            private var firstSelection = true

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (firstSelection) {
                    firstSelection = false
                    return
                }

                val selectedTheme = themeValues[position]
                preferenceManager.setThemeMode(selectedTheme)
                applyTheme(selectedTheme)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun applyTheme(themeMode: String) {
        val mode = when (themeMode) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}