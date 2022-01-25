package com.jgdeveloppement.login_activity.utils

import android.graphics.Color
import android.view.View
import com.google.android.material.snackbar.Snackbar

object Utils {
    const val RC_SIGN_IN = 123
    const val IS_DARK_THEME = "IS_DARK_THEME"
    var isDarkTheme: String = "none"

    fun showSnackBar(view: View?, message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).show()
    }

    fun textColor(isDarkTheme: Boolean): Int{
        return if (isDarkTheme) Color.rgb(238,238,238) else Color.rgb(57,62,70)
    }

    fun isConnected(callback: (ok: Boolean) -> Unit){
        val command = "ping -c 1 google.com"
        val ping = Runtime.getRuntime().exec(command).waitFor()
        if (ping == 0) callback(true) else callback(false)
    }
}