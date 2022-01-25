package com.jgdeveloppement.login_activity.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentActivity
import com.example.awesomedialog.*
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.jgdeveloppement.login_activity.LoginActivity
import com.jgdeveloppement.login_activity.R
import com.jgdeveloppement.login_activity.databinding.ActivityHomeBinding
import com.jgdeveloppement.login_activity.utils.Utils

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private var themePreference: SharedPreferences? = null
    private var isDarkTheme: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themePreference = this.getPreferences(Context.MODE_PRIVATE)
        isDarkTheme = themePreference!!.getBoolean(Utils.IS_DARK_THEME, false)
        if (isDarkTheme as Boolean) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        configureDrawerLayout()
        binding.navView.setNavigationItemSelectedListener(this)
    }

    /** ************************  */
    /** Configure Drawer Layout  */
    /** **********************  */
    private fun configureDrawerLayout() {
        val toggle = ActionBarDrawerToggle(this, binding.layoutDrawer, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.layoutDrawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.theme -> {
                runThemeDialogue()
            }
            R.id.logout -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnSuccessListener { finish(); LoginActivity.navigate(this) }
            }
            else -> return false
        }

        binding.layoutDrawer.closeDrawer(GravityCompat.START)
        return true
    }
    /** *********************  */
    /** ********************  */

    /** PopUp dialogue for set theme  */
    private fun runThemeDialogue(){
        val editTheme = themePreference!!.edit()
        val textColor = Utils.textColor(isDarkTheme as Boolean)
        AwesomeDialog.build(this)
            .background(R.drawable.shape_dialogue)
            .title(getString(R.string.dialogue_title), Typeface.DEFAULT_BOLD, textColor)
            .body(getString(R.string.dialogue_body), Typeface.SANS_SERIF, textColor)
            .icon(R.drawable.small_logo, true)
            .onPositive(getString(R.string.dialogue_light_theme), R.drawable.background_button, textColor){
                editTheme.putBoolean(Utils.IS_DARK_THEME, false)
                editTheme.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            .onNegative(getString(R.string.dialogue_dark_theme), R.drawable.background_button, textColor){
                editTheme.putBoolean(Utils.IS_DARK_THEME, true)
                editTheme.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            .position(AwesomeDialog.POSITIONS.CENTER)

    }

    companion object {
        /** Used to navigate to this activity  */
        fun navigate(activity: Activity?) {
            val intent = Intent(activity, HomeActivity::class.java)
            ActivityCompat.startActivity(activity!!, intent, null)
            activity.finish()
        }
    }
}