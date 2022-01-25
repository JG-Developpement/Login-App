package com.jgdeveloppement.login_activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.jgdeveloppement.login_activity.databinding.ActivityLoginBinding
import com.jgdeveloppement.login_activity.home.HomeActivity
import com.jgdeveloppement.login_activity.utils.Utils

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val themePreference: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val getTheme = themePreference.getBoolean(Utils.IS_DARK_THEME, false)
        if (getTheme) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        rooting()
        startAnimation()
        onClickEmailLoginButton()
        onClickGoogleLoginButton()

    }

    /** Login for activity result  */
    private var loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val response = IdpResponse.fromResultIntent(data)
            loginLayout(true)
            if (result.resultCode == Activity.RESULT_OK) {
                HomeActivity.navigate(this)
            }else{
                when (response?.error?.errorCode) {
                    ErrorCodes.NO_NETWORK -> Utils.showSnackBar(binding.loginLayout, getString(R.string.error_no_internet))
                    ErrorCodes.UNKNOWN_ERROR -> Utils.showSnackBar(binding.loginLayout, getString(R.string.error_unknown_error))
                    else ->{
                        Utils.showSnackBar(binding.loginLayout, getString(R.string.error_authentication_canceled))
                        rooting()
                    }
                }
            }
        }else{
            Utils.showSnackBar(binding.loginLayout, getString(R.string.error_authentication_canceled))
            rooting()
        }
    }

    /** Rooting  */
    private fun rooting() {
        loginLayout(false)
        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseAuth.getInstance().currentUser != null) HomeActivity.navigate(this)
            else loginLayout(true)
        }, (2 * 1000).toLong())
    }

    /** Login with Google  */
    private fun onClickGoogleLoginButton(){
        binding.loginButtonGoogle.setOnClickListener {
            startSignInActivity(AuthUI.IdpConfig.GoogleBuilder().build())
        }
    }

    /** Login with Mail  */
    private fun onClickEmailLoginButton(){
        binding.loginButtonMail.setOnClickListener {
            startSignInActivity(AuthUI.IdpConfig.EmailBuilder().build())
        }
    }

    /** Login with FirebaseUI  */
    private fun startSignInActivity(authUI: AuthUI.IdpConfig) {
        loginLauncher.launch(AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(listOf(authUI))
            .setIsSmartLockEnabled(false, true)
            .setTheme(R.style.LoginFirebaseTheme)
            .setLogo(R.drawable.small_logo)
            .build())

        loginLayout(false)
    }

    /** Set visibility progress layout  */
    private fun loginLayout(visibility: Boolean){
        if (visibility) binding.loginProgressLayout.visibility = View.GONE
        else binding.loginProgressLayout.visibility = View.VISIBLE
    }



    /** Animation  */
    private fun startAnimation(){
        //binding.imageView.animationXZoom(Zoom.ZOOM_IN_DOWN)
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
        binding.imageView.startAnimation(pulseAnimation)
    }

    companion object {
        /** Used to navigate to this activity  */
        fun navigate(activity: Activity?) {
            val intent = Intent(activity, LoginActivity::class.java)
            ActivityCompat.startActivity(activity!!, intent, null)
            activity.finish()
        }
    }

}