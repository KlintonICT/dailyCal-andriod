package com.example.dailycarl.helper

import android.content.Context

val PREFERENCE_NAME = "SharedPreferenceExample"
val PREFERENCE_LANGUAGE = "Language"

class Preference(context : Context){

    val preference = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)

    fun getLoginCount() : String? {
        return preference.getString(PREFERENCE_LANGUAGE,"en")
    }

    fun setLoginCount(Language:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_LANGUAGE,Language)
        editor.apply()
    }
}