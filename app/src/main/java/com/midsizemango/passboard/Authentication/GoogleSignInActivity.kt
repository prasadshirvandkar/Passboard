package com.midsizemango.passboard.Authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.*;
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.auth.GoogleAuthProvider
import com.midsizemango.passboard.MainActivity
import com.midsizemango.passboard.R

/**
 * Created by prasads on 15/12/17.
 */

class GoogleSignInActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    override fun onConnectionFailed(p0: ConnectionResult) {}

    private var signinbtn : SignInButton? = null
    private var mAuth : FirebaseAuth? = null
    private val TAG : String = "GoogleActivity"
    private val RC_SIGN_IN : Int = 1234

    private var mGoogleSignInClient : GoogleSignInClient? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var account_pref: SharedPreferences? = null
    private var signed_in: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_signin)

        signinbtn = findViewById<View>(R.id.google_signin_btn) as SignInButton

        val gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()

        account_pref = getSharedPreferences("ACCT_PREF", Context.MODE_PRIVATE)
        signed_in = account_pref!!.getBoolean("signed", false)

        if(signed_in){
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        signinbtn!!.setOnClickListener{
            signIn()
        }
    }

    override fun onStart(){
        super.onStart()
        if(signed_in){
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            val acct = result.signInAccount
            firebaseAuthWithGoogle(acct!!)
            val preferences: SharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
            preferences.edit()
                    .putString("name", acct.displayName)
                    .putString("email", acct.email)
                    .putString("photo", acct.photoUrl.toString())
                    .putString("id", acct.id)
                    .apply()
        } else {
            Toast.makeText(applicationContext, "Sign in failed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        //val user = mAuth!!.currentUser
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        account_pref!!.edit().putBoolean("signed", true).apply()
                        finish()
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

}
