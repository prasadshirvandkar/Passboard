package com.midsizemango.passboard.Fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.midsizemango.noteboard.Models.Note
import com.midsizemango.passboard.*
import com.midsizemango.passboard.Activity.PasswordEditActivity
import com.midsizemango.passboard.Adapter.PasswordListAdapter
import com.midsizemango.passboard.Models.Password
import com.midsizemango.passboard.Utils.DeletionListener
import com.midsizemango.passboard.Utils.ItemTouchHelperCallback
import java.util.*


/**
 * Created by prasads on 15/12/17.
 */
class PasswordsListFragment : Fragment(), DeletionListener {

    var pass_List_adapter: PasswordListAdapter? = null
    lateinit var databasereference: DatabaseReference
    lateinit var preferences: SharedPreferences
    var passwords = mutableListOf<Password>()
    var emptyText: TextView? = null
    var recyclerView: RecyclerView? = null

    companion object {
        fun newInstance(): PasswordsListFragment {
            return PasswordsListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_list, container, false)
        val activity = activity

        preferences = activity!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        databasereference = FirebaseDatabase.getInstance().getReference("passwords")
        databasereference.keepSynced(true)

        emptyText = view.findViewById(R.id.emptyText) as TextView
        recyclerView = view.findViewById(R.id.recyclerview_fragment) as RecyclerView
        recyclerView!!.hasFixedSize()
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        pass_List_adapter = PasswordListAdapter(passwords)

        val itemTouchHelper: ItemTouchHelper? = ItemTouchHelper(ItemTouchHelperCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, this))
        itemTouchHelper!!.attachToRecyclerView(recyclerView)
        recyclerView!!.adapter = pass_List_adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        databasereference.child(preferences.getString("id", "id")).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (passwordDataSnapshot in dataSnapshot.children) {
                    val password = passwordDataSnapshot.getValue<Password>(Password::class.java)
                    if(!passwords.contains(password)) {
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
                pass_List_adapter!!.notifyDataSetChanged()
                sortList1(PasswordListAdapter(passwords).titleComparatorAesc)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK){
            Toast.makeText(activity, "Launched PF", Toast.LENGTH_SHORT).show()
            databasereference.child(preferences.getString("id", "id")).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (passwordDataSnapshot in dataSnapshot.children) {
                        val password = passwordDataSnapshot.getValue<Password>(Password::class.java)
                        if(!passwords.contains(password)) {
                            passwords.add(password!!)
                        }
                    }
                    pass_List_adapter?.updateList(passwords)
                    sortList1(PasswordListAdapter(passwords).titleComparatorAesc)
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    override fun itemRemoved(position: Int) {
        val password: Password = pass_List_adapter!!.getItem(position)
        pass_List_adapter!!.removeItem(position)
        databasereference.child(preferences.getString("id", "id")).child(password.pass_user_id).removeValue()
    }

    fun sortList1(noteComparator: Comparator<Password>) {
        Collections.sort(passwords, noteComparator)
        pass_List_adapter!!.notifyDataSetChanged()
    }

}