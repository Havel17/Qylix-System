package com.example.qylixSystem.repository

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesRepository {
    private lateinit var pref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    fun init(context: Context) {
        pref = context.getSharedPreferences(
            "pref",
            Context.MODE_PRIVATE
        )
        editor = pref.edit()
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value)
    }

    fun putInt(key: String, value: Int) {
        editor.putInt(key, value)
    }

    fun getInt(key: String): Int {
        return pref.getInt(key, -1)
    }

    fun saveData() {
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, false)
    }
}