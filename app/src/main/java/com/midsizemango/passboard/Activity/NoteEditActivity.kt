package com.midsizemango.passboard.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.github.mummyding.colorpickerdialog.ColorPickerDialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.midsizemango.noteboard.Models.Note
import com.midsizemango.passboard.Fragment.NotesListFragment
import com.midsizemango.passboard.R
import com.midsizemango.passboard.Utils.ColorGenerator
import se.simbio.encryption.Encryption
import java.util.*

/**
 * Created by ABC on 12/23/2017.
 */

class NoteEditActivity: AppCompatActivity() {

    var note: Note? = null
    lateinit var databasereference: DatabaseReference
    lateinit var preferences: SharedPreferences
    private val EXTRA_NOTE = "NOTE"
    var primaryPreselect: Int = ColorGenerator.MATERIAL.randomColor
    val key = "passboard"
    val salt = "passboard2525"
    var iv = ByteArray(16)
    var encryption: Encryption = Encryption.getDefault(key, salt, iv)
    var noteModelStatus: Note? = null

    var noteId: String? = null

    var toolbar: Toolbar? = null
    var editTitle: EditText? = null
    var editContent: EditText? = null
    var noteAction: String? = null
    var titleLength = 0
    var contentLength = 0

    fun newInstance(context: Context, note: Note?): Intent {
        val intent = Intent(context, NoteEditActivity::class.java)
        if (note != null) {
            intent.putExtra(EXTRA_NOTE, note)
        }
        intent.putExtra("note_action","edit")
        return intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_edit)

        preferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        databasereference = FirebaseDatabase.getInstance().getReference("notes").child(preferences.getString("id", "id"))
        databasereference.keepSynced(true)

        toolbar = findViewById(R.id.toolbar)
        editTitle = findViewById(R.id.edit_title)
        editContent = findViewById(R.id.edit_content)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar!!.title = ""
        title = ""

        noteAction = intent.getStringExtra("note_action")
        if(noteAction.equals("edit")){
            editTitle!!.isFocusable = false
            editContent!!.isFocusable = false
        }

        note = intent.getSerializableExtra(EXTRA_NOTE) as Note?
        noteModelStatus = intent.getSerializableExtra(EXTRA_NOTE) as Note?
        if(note != null){
            noteId = note!!.note_user_id
            primaryPreselect = note!!.note_color!!
            toolbar!!.navigationIcon!!.colorFilter = PorterDuffColorFilter(note!!.note_color!!, PorterDuff.Mode.MULTIPLY)
            window.statusBarColor = note!!.note_color!!
            toolbar!!.setTitleTextColor(note!!.note_color!!)
            editTitle!!.setText(encryption.decrypt(note!!.note_name))
            editContent!!.setText(encryption.decrypt(note!!.note_text))
            editTitle!!.setTextColor(note!!.note_color!!)
        } else{
            note = Note()
            primaryPreselect = ColorGenerator.MATERIAL.randomColor
            window.statusBarColor = primaryPreselect
            toolbar!!.navigationIcon!!.colorFilter = PorterDuffColorFilter(primaryPreselect, PorterDuff.Mode.MULTIPLY)
            toolbar!!.setTitleTextColor(primaryPreselect)
            editTitle!!.setTextColor(primaryPreselect)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        if(noteModelStatus != null){
            menu.findItem(R.id.edit).icon.colorFilter = PorterDuffColorFilter(note!!.note_color!!, PorterDuff.Mode.MULTIPLY)
            menu.findItem(R.id.color).icon.colorFilter = PorterDuffColorFilter(note!!.note_color!!, PorterDuff.Mode.MULTIPLY)
        }else{
            menu.findItem(R.id.edit).icon.colorFilter = PorterDuffColorFilter(primaryPreselect, PorterDuff.Mode.MULTIPLY)
            menu.findItem(R.id.color).icon.colorFilter = PorterDuffColorFilter(primaryPreselect, PorterDuff.Mode.MULTIPLY)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            R.id.edit -> {
                editTitle!!.isFocusableInTouchMode = true
                editContent!!.isFocusableInTouchMode = true
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
                            toolbar!!.navigationIcon!!.colorFilter = PorterDuffColorFilter(newColor, PorterDuff.Mode.MULTIPLY)
                            window.statusBarColor = newColor
                            toolbar!!.setTitleTextColor(newColor)
                            editTitle!!.setTextColor(newColor)
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
        if (!editTitle!!.text.isEmpty()) {
            val str = databasereference.push().key
            if (note == null) {
                note = Note()
                note!!.note_id = str
            }
            note!!.note_name = encryption.encrypt(editTitle!!.text.toString())
            note!!.note_text = encryption.encrypt(editContent!!.text.toString())
            note!!.note_color = primaryPreselect
            note!!.note_date_time = Date()
            if (noteModelStatus == null) {
                note!!.note_user_id = str
            }
            if (noteModelStatus == null) {
                databasereference.child(str).setValue(note)
            } else {
                databasereference.child(noteId).setValue(note)
            }

        } else if (editTitle!!.text.isEmpty()) {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }
}