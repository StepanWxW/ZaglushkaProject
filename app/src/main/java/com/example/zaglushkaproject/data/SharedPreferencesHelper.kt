package com.example.zaglushkaproject.data

import android.content.Context

import android.content.SharedPreferences

object SharedPreferencesHelper {
    fun getSharedPreferencesLink(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("firebaseLink", null)
    }
    fun saveSharedPreferencesLink(link: String, context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("firebaseLink", link)
        editor.apply()
    }
}