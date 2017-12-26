package com.midsizemango.passboard.Fragment

/**
 * Created by ABC on 12/23/2017.
 */
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.*
import com.midsizemango.noteboard.Models.Note
import com.midsizemango.passboard.Activity.PasswordEditActivity
import com.midsizemango.passboard.Adapter.NoteSectionedListAdapter
import com.midsizemango.passboard.Adapter.PasswordListAdapter
import com.midsizemango.passboard.Models.Password
import com.midsizemango.passboard.R
import com.midsizemango.passboard.Adapter.PasswordSectionedListAdapter
import com.midsizemango.passboard.Models.GeneralItem
import com.midsizemango.passboard.Models.ListItem
import com.midsizemango.passboard.Models.NameItem
import java.util.*

class NotesSectionedListFragment :Fragment(){

    lateinit var databasereference: DatabaseReference
    lateinit var preferences: SharedPreferences
    lateinit var floatingActionButton: FloatingActionButton
    var mParam1: String? = null
    lateinit var preferencesfg: SharedPreferences
    //val notes = mutableListOf<Password>()
    val consolidatedList = mutableListOf<ListItem>()
    var noteSectionedAdapter: NoteSectionedListAdapter? = null
    val notes = mutableListOf<Note>()
    var emptyText: TextView? = null
    var recyclerView: RecyclerView? = null

    companion object {
        fun newInstance(): NotesSectionedListFragment {
            return NotesSectionedListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val activity = activity

        preferences = activity!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        preferencesfg = activity.getSharedPreferences("PREFSFG", Context.MODE_PRIVATE)
        databasereference = FirebaseDatabase.getInstance().getReference("notes")
        databasereference.keepSynced(true)

        emptyText = view.findViewById(R.id.emptyText) as TextView
        recyclerView = view.findViewById(R.id.recyclerview_fragment) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        noteSectionedAdapter = NoteSectionedListAdapter(consolidatedList)
        recyclerView!!.adapter = noteSectionedAdapter

        return view
    }

    override fun onResume() {
        super.onResume()
        databasereference.child(preferences.getString("id", "id")).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (noteDataSnapshot in dataSnapshot.children) {
                    val note = noteDataSnapshot.getValue<Note>(Note::class.java)
                    if (!notes.contains(note)) {
                        notes.add(note!!)
                    }
                }
                if(notes.isEmpty()){
                    recyclerView!!.visibility = View.GONE
                    emptyText!!.visibility = View.VISIBLE
                }else{
                    recyclerView!!.visibility = View.VISIBLE
                    emptyText!!.visibility = View.GONE
                }
                consolidatedList.clear()
                val groupedHashMap = groupDataIntoHashMap(notes)
                for(namehm in groupedHashMap.keys){
                    val nameItem = NameItem()
                    nameItem.name = namehm
                    consolidatedList.add(nameItem)

                    for(notehm in groupedHashMap[namehm]!!){
                        val generalItem = GeneralItem()
                        generalItem.note = notehm
                        consolidatedList.add(generalItem)
                    }
                }
                noteSectionedAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun groupDataIntoHashMap(noteList: MutableList<Note>): HashMap<String, MutableList<Note>>{
        val groupedHashMap = HashMap<String, MutableList<Note>>()

        for(note in noteList){
            val hmKey: String = note.note_name!!.substring(0,1)
            if(groupedHashMap.containsKey(hmKey)){
                groupedHashMap[hmKey]!!.add(note)
            }else{
                val list = ArrayList<Note>()
                list.add(note)
                groupedHashMap.put(hmKey, list)
            }
        }
        return groupedHashMap
    }
}