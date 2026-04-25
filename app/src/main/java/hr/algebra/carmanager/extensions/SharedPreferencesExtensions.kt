package hr.algebra.carmanager.extensions

import android.content.SharedPreferences

fun SharedPreferences.putBooleanValue(key: String, value: Boolean) {
    edit()
        .putBoolean(key, value)
        .apply()
}

fun SharedPreferences.putStringValue(key: String, value: String) {
    edit()
        .putString(key, value)
        .apply()
}

fun SharedPreferences.getStringValue(key: String, defaultValue: String): String {
    return getString(key, defaultValue) ?: defaultValue
}