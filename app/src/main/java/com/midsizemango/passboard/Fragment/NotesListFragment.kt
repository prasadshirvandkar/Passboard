package com.midsizemango.passboard.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.google.firebase.database.*
import com.midsizemango.noteboard.Models.Note
import com.midsizemango.passboard.Adapter.NoteListAdapter
import com.midsizemango.passboard.R
import java.util.*

/**
 * Created by ABC on 12/23/2017.
 */
class NotesListFragment : Fragment() {

    var note_list_adapter: NoteListAdapter? = null
    lateinit var databasereference: DatabaseReference
    lateinit var preferences: SharedPreferences
    var notes = mutableListOf<Note>()
    var emptyText: TextView? = null
    var recyclerView: RecyclerView? = null

    companion object {
        fun newInstance(): NotesListFragment {
            return NotesListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_list, container, false)
        val activity = activity

        preferences = activity!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        databasereference = FirebaseDatabase.getInstance().getReference("notes")
        databasereference.keepSynced(true)

        emptyText = view.findViewById(R.id.emptyText) as TextView
        recyclerView = view.findViewById(R.id.recyclerview_fragment) as RecyclerView
        recyclerView!!.hasFixedSize()
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        note_list_adapter = NoteListAdapter(notes)

        recyclerView!!.adapter = note_list_adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        databasereference.child(preferences.getString("id", "id")).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (noteDataSnapshot in dataSnapshot.children) {
                    val note = noteDataSnapshot.getValue<Note>(Note::class.java)
                    if(!notes.contains(note)) {
                        notes.add(note!!)
                    }
                }
                if(notes.size == 0){
                    recyclerView!!.visibility = View.GONE
                    emptyText!!.visibility = View.VISIBLE
                }else{
                    recyclerView!!.visibility = View.VISIBLE
                    emptyText!!.visibility = View.GONE
                }
                sortList1(NoteListAdapter(notes).titleComparatorAesc)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            databasereference.child(preferences.getString("id", "id")).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (noteDataSnapshot in dataSnapshot.children) {
                        val note = noteDataSnapshot.getValue<Note>(Note::class.java)
                        if(!notes.contains(note)) {
                            notes.add(note!!)
                        }
                    }
                    //note_list_adapter?.updateList(notes)
                    sortList1(NoteListAdapter(notes).titleComparatorAesc)
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    fun sortList1(noteComparator: Comparator<Note>) {
        Collections.sort(notes, noteComparator)
        note_list_adapter!!.notifyDataSetChanged()
    }

}