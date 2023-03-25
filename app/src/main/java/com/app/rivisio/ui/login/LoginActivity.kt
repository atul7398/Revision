package com.app.rivisio.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import com.app.rivisio.R
import com.app.rivisio.data.prefs.UserState
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.HomeActivity
import com.app.rivisio.ui.onboarding.OnboardingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val CONST_SIGN_IN = 100
    private val loginViewModel: LoginViewModel by viewModels()

    companion object {
        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override fun getViewModel(): BaseViewModel = loginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        showLoading()

        if (isUserSignedIn()) {
            hideLoading()
            startActivity(HomeActivity.getStartIntent(this@LoginActivity))
            finish()
        } else {
            hideLoading()
        }

        setUpObserver()

        findViewById<AppCompatButton>(R.id.google_button).setOnClickListener {
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, CONST_SIGN_IN)
        }
    }

    private fun setUpObserver() {
        loginViewModel.isUserLoggedIn.observe(this, Observer {
            if (it) {
                startActivity(HomeActivity.getStartIntent(this@LoginActivity))
                finish()
            }
        })
    }

    private fun isUserSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        return account != null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONST_SIGN_IN) {
            handleSignData(data)
        }
    }

    private fun handleSignData(data: Intent?) {
        val addOnCompleteListener = GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnCompleteListener { it: Task<GoogleSignInAccount> ->
                Timber.d("isSuccessful ${it.isSuccessful}")
                if (it.isSuccessful) {
                    loginViewModel.setUserState(UserState.LOGGED_IN)
                    // user successfully logged-in
                    Timber.d("account ${it.result?.account}")
                    Timber.d("displayName ${it.result?.displayName}")
                    Timber.d("Email ${it.result?.email}")

                    it.result.email?.let { it1 ->
                        it.result.displayName?.let { it2 ->
                            loginViewModel.setUserDetails(
                                it1,
                                it2
                            )
                        }
                    }

                } else {
                    // authentication failed
                    Timber.e("exception ${it.exception}")
                }
            }

    }

}