package com.midsizemango.passboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.midsizemango.passboard.Authentication.GoogleSignInActivity


class MainActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var account_pref: SharedPreferences
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        account_pref = getSharedPreferences("ACCT_PREF", Context.MODE_PRIVATE)
        mAuth = FirebaseAuth.getInstance()

        //Toast.makeText(applicationContext, preferences.getString("id","id"), Toast.LENGTH_SHORT).show()
        val detailsFragment = PasswordsFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame, detailsFragment, "PasswordFragment")
                .addToBackStack(null)
                .commit()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                val detailsFragment = PasswordsFragment.newInstance()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, detailsFragment, "PasswordFragment")
                        .addToBackStack(null)
                        .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                //message.text = resources.getString(R.string.title_notes)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                //message.text = resources.getString(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.logout -> {
                mAuth!!.signOut()
                finish()
                startActivity(Intent(applicationContext, GoogleSignInActivity::class.java))
                account_pref!!.edit().remove("signed").apply()
                preferences!!.edit().remove("email").remove("name").remove("photo").apply()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed(){
        super.onBackPressed()
        finish()
    }

}
