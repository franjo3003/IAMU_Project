package hr.algebra.carmanager.data.prefs

import android.content.Context
import hr.algebra.carmanager.extensions.getStringValue
import hr.algebra.carmanager.extensions.putBooleanValue
import hr.algebra.carmanager.extensions.putStringValue


class PreferenceManager(context: Context) {

    private val prefs = context.getSharedPreferences(
        "car_manager_preferences",
        Context.MODE_PRIVATE
    )

    fun areNotificationsEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.putBooleanValue(KEY_NOTIFICATIONS_ENABLED, enabled)
    }

    fun getDefaultFuelFilter(): String {
        return prefs.getStringValue(KEY_DEFAULT_FUEL_FILTER, DEFAULT_FUEL_FILTER)
    }

    fun setDefaultFuelFilter(fuelType: String) {
        prefs.putStringValue(KEY_DEFAULT_FUEL_FILTER, fuelType)
    }

    fun getThemeMode(): String {
        return prefs.getStringValue(KEY_THEME_MODE, THEME_SYSTEM)
    }

    fun setThemeMode(themeMode: String) {
        prefs.putStringValue(KEY_THEME_MODE, themeMode)
    }

    companion object {
        private const val DEFAULT_FUEL_FILTER = "all"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"

        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DEFAULT_FUEL_FILTER = "default_fuel_filter"
    }
}