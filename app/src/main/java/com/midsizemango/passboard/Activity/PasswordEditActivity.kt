package com.midsizemango.passboard.Activity

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.*

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.midsizemango.passboard.Models.Password
import com.midsizemango.passboard.R
import com.afollestad.materialdialogs.MaterialDialog
import android.view.View
import com.midsizemango.passboard.Utils.ColorGenerator
import android.widget.Toast
import com.github.mummyding.colorpickerdialog.ColorPickerDialog
import se.simbio.encryption.Encryption

/**
 * Created by prasads on 15/12/17.
 */

class PasswordEditActivity: AppCompatActivity() {

    var titleName: TextView? = null
    var titleImage: FrameLayout? = null
    var categoryText: TextView? = null

    var emailEditText: EditText? = null
    var passEditText: EditText? = null
    var nameEditText: EditText? = null
    var linkEditText: EditText? = null
    var noteEditText: EditText? = null

    var copyEmail: Button? = null
    var copyPassword: Button? = null
    var copyLink: Button? = null

    var categoryLayout: LinearLayout? = null
    var toolbar: Toolbar? = null
    var appbar: AppBarLayout? = null
    var collapsingToolbar: CollapsingToolbarLayout? = null

    var password: Password? = null
    lateinit var databasereference: DatabaseReference
    lateinit var preferences: SharedPreferences
    private val EXTRA_PASSWORD = "PASSWORD"
    var passwordText: String? = null
    var passId: String? = null
    var passwordModelStatus: Password? = null
    var passwordAction: String? = null
    var selectedCategory: String? = null
    var primaryPreselect: Int = ColorGenerator.MATERIAL.randomColor
    val key = "passboard"
    val salt = "passboard2525"
    var iv = ByteArray(16)
    var encryption: Encryption = Encryption.getDefault(key, salt, iv)
    var editStatus = 0

    fun newInstance(context: Context, password: Password?): Intent {
        val intent = Intent(context, PasswordEditActivity::class.java)
        if (password != null) {
            intent.putExtra(EXTRA_PASSWORD, password)
        }
        intent.putExtra("password_action","edit")
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_edit)

        preferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        databasereference = FirebaseDatabase.getInstance().getReference("passwords").child(preferences.getString("id", "id"))
        databasereference.keepSynced(true)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        appbar = findViewById(R.id.app_bar)
        collapsingToolbar = findViewById(R.id.toolbar_layout)

        titleName = findViewById(R.id.title_text)
        titleImage = findViewById(R.id.title_image)
        categoryText = findViewById(R.id.category_text)

        emailEditText = findViewById(R.id.email_text)
        passEditText = findViewById(R.id.pass_text)
        nameEditText = findViewById(R.id.name_text)
        linkEditText = findViewById(R.id.link_text)
        noteEditText = findViewById(R.id.note_text)

        copyEmail = findViewById(R.id.copy_email)
        copyPassword = findViewById(R.id.copy_pass)
        copyLink = findViewById(R.id.copy_link)

        categoryLayout = findViewById(R.id.category_layout)
        val listCat = mutableListOf("Select Category", "Business", "Shopping", "Information", "Personal", "Work", "Communication",
                "Travel", "Development")
        categoryLayout!!.setOnClickListener {
            MaterialDialog.Builder(this)
                    .title("Categories")
                    .items(listCat)
                    .itemsCallback(object : MaterialDialog.ListCallback {
                        override fun onSelection(dialog: MaterialDialog, view: View, which: Int, text: CharSequence) {
                            selectedCategory = text.toString()
                            categoryText!!.text = text.toString()
                            editStatus = 1
                        }
                    })
                    .show()
        }

        nameEditText!!.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                editStatus = 1
            }
        })

        emailEditText!!.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                editStatus = 1
            }
        })

        passEditText!!.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                editStatus = 1
            }
        })

        linkEditText!!.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                editStatus = 1
            }
        })

        noteEditText!!.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                editStatus = 1
            }
        })

        passwordAction = intent.getStringExtra("password_action")
        if(passwordAction.equals("edit")){
            emailEditText!!.isFocusable = false
            passEditText!!.isFocusable = false
            nameEditText!!.isFocusable = false
            linkEditText!!.isFocusable = false
            noteEditText!!.isFocusable = false
        }

        password = intent.getSerializableExtra(EXTRA_PASSWORD) as Password?
        passwordModelStatus = intent.getSerializableExtra(EXTRA_PASSWORD) as Password?
        if(password != null){
            passId = password!!.pass_user_id
            if(password!!.pass_name!!.length >= 4){
                titleName!!.text = password!!.pass_name!!.substring(0,4)
            }else {
                titleName!!.text = password!!.pass_name!!
            }
            categoryText!!.text = password!!.pass_category
            emailEditText!!.setText(encryption.decrypt(password!!.pass_email.toString()))
            passEditText!!.setText(encryption.decrypt(password!!.pass_pass.toString()))
            nameEditText!!.setText(password!!.pass_name)
            linkEditText!!.setText(password!!.pass_link)
            noteEditText!!.setText(encryption.decrypt(password!!.pass_text.toString()))

            selectedCategory = password!!.pass_category
            if(password!!.pass_category != "Selected Category"){
                categoryText!!.setTextColor(password!!.pass_color)
                categoryText!!.setTypeface(categoryText!!.typeface, Typeface.BOLD)
            }

            primaryPreselect = password!!.pass_color
            toolbar!!.setBackgroundColor(password!!.pass_color)
            appbar!!.setBackgroundColor(password!!.pass_color)
            window.statusBarColor = password!!.pass_color
            collapsingToolbar!!.title = password!!.pass_name
        } else{
            password = Password()
            selectedCategory = "Select Category"
            categoryText!!.text = selectedCategory
            primaryPreselect = ColorGenerator.MATERIAL.randomColor
            toolbar!!.setBackgroundColor(primaryPreselect)
            appbar!!.setBackgroundColor(primaryPreselect)
            window.statusBarColor = primaryPreselect
            titleImage!!.visibility = View.GONE
            collapsingToolbar!!.title = "Add Password"
        }

        copyEmail!!.setOnClickListener {
            if(emailEditText!!.text.isEmpty()){
                Toast.makeText(applicationContext, "Email Field is Empty", Toast.LENGTH_SHORT).show()
            } else {
                copyText(emailEditText!!.text.toString())
            }
        }

        copyLink!!.setOnClickListener {
            if(linkEditText!!.text.isEmpty()){
                Toast.makeText(applicationContext, "Link Field is Empty", Toast.LENGTH_SHORT).show()
            } else {
                copyText(linkEditText!!.text.toString())
            }
        }

        copyPassword!!.setOnClickListener {
            if(passEditText!!.text.isEmpty()){
                Toast.makeText(applicationContext, "Password Field is Empty", Toast.LENGTH_SHORT).show()
            } else {
                copyText(passEditText!!.text.toString())
            }
        }
    }

    private fun copyText(s: String?){
        val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.text = s
        Toast.makeText(applicationContext, "Copied", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            R.id.edit -> {
                emailEditText!!.isFocusableInTouchMode = true
                passEditText!!.isFocusableInTouchMode = true
                nameEditText!!.isFocusableInTouchMode = true
                linkEditText!!.isFocusableInTouchMode = true
                noteEditText!!.isFocusableInTouchMode = true
                true
            }

            R.id.color -> {
                val colorsList = intArrayOf(-0x1a8c8d,
                        -0xff432c,
                        -0xff6978,
                        -0x86aab8,
                        -0xbaa59c,
                        -0x1a848e,
                        -0x72dcbb,
                        -0x9f8275,
                        -0x8fbd,
                        -0xbf7f,
                        -0x16e19d,
                        -0xfc8cc1,
                        -0xa41db,
                        -0xca600e,
                        -0xa98804,
                        -0x2bb19311,
                        -0xd9cdc8,
                        -0xff6978,
                        -0xfd651c,
                        -0xff61d7,
                        -0xcc4987)

                ColorPickerDialog(this, colorsList)
                        .setDismissAfterClick(false)
                        .setTitle("Choose Color")
                        .setCheckedColor(Color.CYAN)
                        .setOnColorChangedListener { newColor ->
                            //Toast.makeText(applicationContext, "Color " + newColor, Toast.LENGTH_SHORT).show()
                            primaryPreselect = newColor
                            toolbar!!.setBackgroundColor(newColor)
                            appbar!!.setBackgroundColor(newColor)
                            window.statusBarColor = newColor
                        }
                        .build(5)
                        .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed(){
        super.onBackPressed()
        if(editStatus == 1) {
            if (!emailEditText!!.text.isEmpty() && !passEditText!!.text.isEmpty() && !nameEditText!!.text.isEmpty()) {
                val str = databasereference.push().key
                if (password == null) {
                    password = Password()
                    password!!.pass_id = str
                }
                //Toast.makeText(applicationContext, str.toString(), Toast.LENGTH_SHORT).show()
                password!!.pass_email = encryption.encrypt(emailEditText!!.text.toString())
                if (passwordModelStatus == null) {
                    password!!.pass_user_id = str
                }
                password!!.pass_pass = encryption.encrypt(passEditText!!.text.toString())
                password!!.pass_name = nameEditText!!.text.toString()
                password!!.pass_link = linkEditText!!.text.toString()
                password!!.pass_text = encryption.encrypt(noteEditText!!.text.toString())
                password!!.pass_color = primaryPreselect
                password!!.pass_category = selectedCategory.toString()

                if (passwordModelStatus == null) {
                    databasereference.child(str).setValue(password)
                } else {
                    databasereference.child(passId).setValue(password)
                }
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            } else if (emailEditText!!.text.isEmpty() && passEditText!!.text.isEmpty()) {
                val intent = Intent()
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
        }else{
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }

}