package com.midsizemango.passboard.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.google.firebase.database.*
import com.midsizemango.passboard.Adapter.PasswordListAdapter
import com.midsizemango.passboard.Models.Password
import com.midsizemango.passboard.R

import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.midsizemango.noteboard.Models.Note
import com.midsizemango.passboard.Adapter.NoteListAdapter

/**
 * Created by ABC on 1/8/2018.
 */

class SearchListActivity: AppCompatActivity(){

    var pass_List_adapter: PasswordListAdapter? = null
    var note_list_adapter: NoteListAdapter? = null
    lateinit var databasereference: DatabaseReference
    lateinit var preferences: SharedPreferences
    var searchList = mutableListOf<Password>()
    var searchListNote = mutableListOf<Note>()
    var emptyText: TextView? = null
    var recyclerView: RecyclerView? = null
    var toolbar: Toolbar? = null

    var action: String = ""
    var tempList = mutableListOf<Password>()
    var tempListNotes = mutableListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        toolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)
        
        action = intent.getStringExtra("search_action")
        preferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        databasereference = if(action == "password") {
            FirebaseDatabase.getInstance().getReference("passwords")
        }else{
            FirebaseDatabase.getInstance().getReference("notes")
        }
        databasereference.keepSynced(true)

        databasereference.child(preferences.getString("id", "id")).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot in dataSnapshot.children) {
                    if(action == "password") {
                        val data = dataSnapshot.getValue<Password>(Password::class.java)
                        if (!searchList.contains(data)) {
                            searchList.add(data!!)
                        }
                    }else{
                        val data = dataSnapshot.getValue<Note>(Note::class.java)
                        if (!searchListNote.contains(data)) {
                            searchListNote.add(data!!)
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        emptyText = findViewById(R.id.emptyText)
        recyclerView = findViewById(R.id.recyclerview_fragment)
        recyclerView!!.hasFixedSize()
        recyclerView!!.layoutManager = LinearLayoutManager(applicationContext)
        getSearch("")
        updateView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_search, menu)

        val searchView = MenuItemCompat.getActionView(menu.findItem(R.id.search_note)) as SearchView

        searchView.setIconifiedByDefault(false)
        if(action == "password") {
            searchView.queryHint = "Search Passwords"
        }else{
            searchView.queryHint = "Search Notes"
        }
        menu.findItem(R.id.search_note).expandActionView()

        val menuItem = menu.findItem(R.id.search_note)
        MenuItemCompat.setOnActionExpandListener(menuItem, object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                onBackPressed()
                return false
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                tempList.clear()
                tempListNotes.clear()
                getSearch(newText)
                return true
            }
        })

        return true
    }

    fun getSearch(sch: String) {
        if (sch == "") {
            tempList = mutableListOf()
            tempListNotes = mutableListOf()
        } else {
            if(action == "password") {
                for (i in 0 until searchList.size) {
                    if (searchList[i].pass_name!!.toLowerCase().contains(sch)) {
                        tempList.add(searchList[i])
                    }
                }
                pass_List_adapter = PasswordListAdapter(tempList)
                recyclerView!!.adapter = pass_List_adapter
                pass_List_adapter!!.notifyDataSetChanged()
            }else{
                for (i in 0 until searchList.size) {
                    if (searchList[i].pass_name!!.toLowerCase().contains(sch)) {
                        tempListNotes.add(searchListNote[i])
                    }
                }
                note_list_adapter = NoteListAdapter(tempListNotes)
                recyclerView!!.adapter = note_list_adapter
                note_list_adapter!!.notifyDataSetChanged()
            }
        }
        updateView()
    }


    private fun updateView() {
        if(tempList.isEmpty()){
            recyclerView!!.visibility = View.GONE
            emptyText!!.visibility = View.VISIBLE
        }else{
            recyclerView!!.visibility = View.VISIBLE
            emptyText!!.visibility = View.GONE
        }
    }

}