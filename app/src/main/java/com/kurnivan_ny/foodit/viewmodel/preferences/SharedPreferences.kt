package com.kurnivan_ny.foodit.viewmodel.preferences

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences (val context: Context) {
    companion object{
        const val USER_PREFF = "USER_PREFF"
    }

    var sharedPreferences = context.getSharedPreferences(USER_PREFF, 0)

    val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun setValuesString(key:String, values: String?){
        editor.putString(key,values).apply()
    }

    fun setValuesInt(key:String, values: Int){
        editor.putInt(key,values).apply()
    }

    fun setValuesFloat(key:String, values: Float){
        editor.putFloat(key,values).apply()
    }

    fun getValuesString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun getValuesInt(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }
    fun getValuesFloat(key: String): Float {
        return sharedPreferences.getFloat(key, 0.00F)
    }

    fun clear() {
        editor.clear()
            .apply()
    }
}