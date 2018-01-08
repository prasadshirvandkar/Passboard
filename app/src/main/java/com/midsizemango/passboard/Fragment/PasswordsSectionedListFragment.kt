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
import com.midsizemango.passboard.Models.Password
import com.midsizemango.passboard.R
import com.midsizemango.passboard.Adapter.PasswordSectionedListAdapter
import com.midsizemango.passboard.Models.GeneralItem
import com.midsizemango.passboard.Models.ListItem
import com.midsizemango.passboard.Models.NameItem
import java.util.*

class PasswordsSectionedListFragment :Fragment(){

    lateinit var databasereference: DatabaseReference
    lateinit var preferences: SharedPreferences
    lateinit var floatingActionButton: FloatingActionButton
    var mParam1: String? = null
    lateinit var preferencesfg: SharedPreferences
    //val passwords = mutableListOf<Password>()
    val consolidatedList = mutableListOf<ListItem>()
    var passwordSectionedAdapter: PasswordSectionedListAdapter? = null
    val passwords = mutableListOf<Password>()
    var emptyText: TextView? = null
    var recyclerView: RecyclerView? = null

    companion object {
        fun newInstance(): PasswordsSectionedListFragment {
            return PasswordsSectionedListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val activity = activity

        preferences = activity!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        preferencesfg = activity.getSharedPreferences("PREFSFG", Context.MODE_PRIVATE)
        databasereference = FirebaseDatabase.getInstance().getReference("passwords")
        databasereference.keepSynced(true)

        emptyText = view.findViewById(R.id.emptyText) as TextView
        recyclerView = view.findViewById(R.id.recyclerview_fragment) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        passwordSectionedAdapter = PasswordSectionedListAdapter(consolidatedList)
        recyclerView!!.adapter = passwordSectionedAdapter

        return view
    }

    override fun onResume() {
        super.onResume()
        databasereference.child(preferences.getString("id", "id")).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (passwordDataSnapshot in dataSnapshot.children) {
                    val password = passwordDataSnapshot.getValue<Password>(Password::class.java)
                    if (!passwords.contains(password)) {
                        passwords.add(password!!)
                    }
                }
                if(passwords.isEmpty()){
                    recyclerView!!.visibility = View.GONE
                    emptyText!!.visibility = View.VISIBLE
                }else{
                    recyclerView!!.visibility = View.VISIBLE
                    emptyText!!.visibility = View.GONE
                }
                consolidatedList.clear()
                val groupedHashMap = groupDataIntoHashMap(passwords)
                for(namehm in groupedHashMap.keys){
                    val nameItem = NameItem()
                    nameItem.name = namehm
                    consolidatedList.add(nameItem)

                    for(passwordhm in groupedHashMap[namehm]!!){
                        val generalItem = GeneralItem()
                        generalItem.password = passwordhm
                        consolidatedList.add(generalItem)
                    }
                }
                passwordSectionedAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun groupDataIntoHashMap(passwordList: MutableList<Password>): TreeMap<String, MutableList<Password>>{
        val groupedHashMap = TreeMap<String, MutableList<Password>>()

        for(password in passwordList){
            val hmKey: String = password.pass_name!!.substring(0,1)
            if(groupedHashMap.containsKey(hmKey)){
                groupedHashMap[hmKey]!!.add(password)
            }else{
                val list = ArrayList<Password>()
                list.add(password)
                groupedHashMap.put(hmKey, list)
            }
        }
        return groupedHashMap
    }

}