package com.midsizemango.passboard.Activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.midsizemango.passboard.Fragment.NotesListFragment
import com.midsizemango.passboard.Fragment.PasswordsSectionedListFragment
import com.midsizemango.passboard.Fragment.SearchListActivity
import com.midsizemango.passboard.R


class MainActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var account_pref: SharedPreferences
    private var mAuth: FirebaseAuth? = null
    var fragmentSwitchStatus: Int? = null
    var statusLeave: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.app_name)

        preferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        account_pref = getSharedPreferences("ACCT_PREF", Context.MODE_PRIVATE)
        mAuth = FirebaseAuth.getInstance()

        switchFragment(PasswordsSectionedListFragment.newInstance(), "PasswordsSectionListFragment")
        fragmentSwitchStatus = 0

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                switchFragment(PasswordsSectionedListFragment.newInstance(), "PasswordsSectionListFragment")
                fragmentSwitchStatus = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                switchFragment(NotesListFragment.newInstance(), "NoteListFragment")
                fragmentSwitchStatus = 1
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun switchFragment(fragment: Fragment, name: String){
        supportFragmentManager.beginTransaction()
                .replace(R.id.frame, fragment, name)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack(null)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                MaterialDialog.Builder(this)
                        .title("Logout")
                        .content("Are you sure you want to Logout?")
                        .positiveText("logout")
                        .negativeText("cancel")
                        .onPositive { dialog, which ->
                            mAuth!!.signOut()
                            finish()
                            startActivity(Intent(applicationContext, GoogleSignInActivity::class.java))
                            account_pref.edit().remove("signed").apply()
                            preferences.edit().remove("email").remove("name").remove("photo").apply()
                        }
                        .onNegative { dialog, which ->
                            dialog.dismiss()
                        }.show()
                true
            }

            R.id.add -> {
                val intent = if(fragmentSwitchStatus == 0){
                    Intent(applicationContext, PasswordEditActivity::class.java)
                }else{
                    Intent(applicationContext, NoteEditActivity::class.java)
                }
                intent.putExtra("note_action", "add")
                startActivity(intent)
                true
            }

            R.id.search -> {
                val intent = Intent(applicationContext, SearchListActivity::class.java)
                if(fragmentSwitchStatus == 0){
                    intent.putExtra("search_action", "password")
                }else{
                    intent.putExtra("search_action", "note")
                }
                startActivity(intent)
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
