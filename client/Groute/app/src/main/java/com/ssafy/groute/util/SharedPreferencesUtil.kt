package com.ssafy.groute.util

import android.content.Context
import android.content.SharedPreferences
import com.ssafy.groute.src.dto.User

class SharedPreferencesUtil (context: Context) {
    val SHARED_PREFERENCES_NAME = "groute_preference"
    val COOKIES_KEY_NAME = "cookies"

    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    //사용자 정보 저장
    fun addUser(user: User){
        val editor = preferences.edit()
        editor.putString("id", user.id)
        editor.putString("token", user.token)
        editor.apply()
    }

    fun getUser(): User{
        val id = preferences.getString("id", "")
        if (id != ""){
            return User(id!!,"", "", "", "", "", "", "", "", "")
        }else{
            return User()
        }
    }


    fun deleteUser(){
        //preference 지우기
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    fun addUserCookie(cookies: HashSet<String>) {
        val editor = preferences.edit()
        editor.putStringSet(COOKIES_KEY_NAME, cookies)
        editor.apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun getString(key:String): String? {
        return preferences.getString(key, null)
    }

    fun deleteUserCookie() {
        preferences.edit().remove(COOKIES_KEY_NAME).apply()
    }

    fun setAutoLogin(userId: String) {
        val editor = preferences.edit()
        editor.putString("auto", userId)
        editor.apply()
    }

    fun getAutoLogin() : String? {
        return preferences.getString("auto", null)
    }

    fun deleteAutoLogin() {
        preferences.edit().remove("auto").apply()
    }

}