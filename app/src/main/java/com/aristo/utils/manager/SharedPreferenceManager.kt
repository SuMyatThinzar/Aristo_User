package com.aristo.utils.manager

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceManager {

    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("UserLogIn", Context.MODE_PRIVATE)
    }

    fun setData(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}