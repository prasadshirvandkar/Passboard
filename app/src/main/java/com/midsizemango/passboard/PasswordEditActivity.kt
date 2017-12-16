package com.midsizemango.passboard

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.midsizemango.passboard.Models.Password


/**
 * Created by prasads on 15/12/17.
 */
class PasswordEditActivity: AppCompatActivity() {

    var titleName: TextView? = null
    var categoryText: TextView? = null

    var emailEditText: EditText? = null
    var passEditText: EditText? = null
    var nameEditText: EditText? = null
    var linkEditText: EditText? = null
    var noteEditText: EditText? = null

    var copyEmail: Button? = null
    var copyPassword: Button? = null
    var copyName: Button? = null
    var copyLink: Button? = null

    var categoryLayout: LinearLayout? = null

    var password: Password? = null
    lateinit var databasereference: DatabaseReference
    lateinit var preferences: SharedPreferences
    private val EXTRA_PASSWORD = "PASSWORD"
    var passwordText: String? = null
    var passId: String? = null
    var passwordModelStatus: Password? = null

    fun newInstance(context: Context, password: Password?): Intent {
        val intent = Intent(context, PasswordEditActivity::class.java)
        if (password != null) {
            intent.putExtra(EXTRA_PASSWORD, password)
        }
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_edit)

        preferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        databasereference = FirebaseDatabase.getInstance().getReference("passwords").child(preferences.getString("id", "id"))

        titleName = findViewById<TextView>(R.id.title_name) as TextView
        categoryText = findViewById<TextView>(R.id.category_text) as TextView

        emailEditText = findViewById<EditText>(R.id.email_text) as EditText
        passEditText = findViewById<EditText>(R.id.pass_text) as EditText
        nameEditText = findViewById<EditText>(R.id.name_text) as EditText
        linkEditText = findViewById<EditText>(R.id.link_text) as EditText
        noteEditText = findViewById<EditText>(R.id.note_text) as EditText

        copyEmail = findViewById<Button>(R.id.copy_email) as Button
        copyPassword = findViewById<Button>(R.id.copy_pass) as Button
        copyName = findViewById<Button>(R.id.copy_name) as Button
        copyLink = findViewById<Button>(R.id.copy_link) as Button

        categoryLayout = findViewById<LinearLayout>(R.id.category_layout) as LinearLayout

        password = intent.getSerializableExtra(EXTRA_PASSWORD) as Password?
        passwordModelStatus = intent.getSerializableExtra(EXTRA_PASSWORD) as Password?
        if(password != null){
            passId = password!!.getPassUserId()
            Toast.makeText(applicationContext, passId, Toast.LENGTH_SHORT).show()
            titleName!!.text = password!!.getPassName()
            categoryText!!.text = password!!.getPassCategory()

            emailEditText!!.setText(password!!.getPassEmail())
            passEditText!!.setText(password!!.getPassPass())
            nameEditText!!.setText(password!!.getPassName())
            linkEditText!!.setText(password!!.getPassLink())
            noteEditText!!.setText(password!!.getPassText())
        } else{
            password = Password()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item. itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed(){
        super.onBackPressed()
        val str = databasereference.push().key
        if (password == null) {
            password = Password()
            password!!.setPassId(str)
        }
        Toast.makeText(applicationContext, str.toString(), Toast.LENGTH_SHORT).show()
        password!!.setPassEmail(emailEditText!!.text.toString())
        if(passwordModelStatus == null){
            password!!.setPassUserId(str)
        }
        password!!.setPassPass(passEditText!!.text.toString())
        password!!.setPassName(nameEditText!!.text.toString())
        password!!.setPassLink(linkEditText!!.text.toString())
        password!!.setPassText(noteEditText!!.text.toString())
        password!!.setPassColor(Color.CYAN)
        password!!.setPassCategory("Business")
        if(passwordModelStatus == null) {
            databasereference.child(str).setValue(password)
        } else{
            databasereference.child(passId).setValue(password)
        }
        finish()
    }

}