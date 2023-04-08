package com.app.rivisio.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import com.app.rivisio.R
import com.app.rivisio.data.prefs.UserState
import com.app.rivisio.ui.base.BaseActivity
import com.app.rivisio.ui.base.BaseViewModel
import com.app.rivisio.ui.home.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val CONST_SIGN_IN = 100
    private val loginViewModel: LoginViewModel by viewModels()

    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                handleSignData(data)
            }
        }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override fun getViewModel(): BaseViewModel = loginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        showLoading()
//
//        if (isUserSignedIn()) {
//            hideLoading()
//            startActivity(HomeActivity.getStartIntent(this@LoginActivity))
//            finish()
//        } else {
//            hideLoading()
//        }

        setUpObserver()

        findViewById<AppCompatButton>(R.id.google_button).setOnClickListener {
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope("https://www.googleapis.com/auth/user.phonenumbers.read"))
                .requestProfile()
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = mGoogleSignInClient.signInIntent
            launcher.launch(signInIntent)
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

    private fun handleSignData(data: Intent?) {
        val addOnCompleteListener = GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnCompleteListener { it: Task<GoogleSignInAccount> ->
                Timber.d("isSuccessful ${it.isSuccessful}")
                if (it.isSuccessful) {
                    loginViewModel.setUserState(UserState.LOGGED_IN)
                    // user successfully logged-in
                    //Timber.d("account ${it.result?.account}")
                    //Timber.d("displayName ${it.result?.displayName}")
                    //Timber.d("Email ${it.result?.email}")

                    val acct = GoogleSignIn.getLastSignedInAccount(this@LoginActivity)
                    if (acct != null) {
                        val name = acct.displayName
                        val firstName = acct.givenName
                        val lastName = acct.familyName
                        val email = acct.email
                        val personId = acct.id
                        val personPhoto: Uri? = acct.photoUrl

                        Timber.d("Name: $name")
                        Timber.d("Email: $email")
                        Timber.d("First Name: $firstName")
                        Timber.d("Last Name: $lastName")

                        if (email != null && name != null) {
                            loginViewModel.setUserDetails(email, name)
                        }
                    }

                } else {
                    // authentication failed
                    Timber.e("exception ${it.exception}")
                }
            }

    }

}