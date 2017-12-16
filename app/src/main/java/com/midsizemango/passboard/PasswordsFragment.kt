package com.midsizemango.passboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.midsizemango.passboard.Models.Password


/**
 * Created by prasads on 15/12/17.
 */
class PasswordsFragment : Fragment(), DeletionListener{

    var pass_adapter: PasswordsAdapter? = null
    lateinit var databasereference: DatabaseReference
    lateinit var preferences: SharedPreferences
    lateinit var floatingActionButton: FloatingActionButton

    companion object {
        fun newInstance(): PasswordsFragment {
            return PasswordsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_password, container, false)
        val activity = activity

        preferences = activity!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        databasereference = FirebaseDatabase.getInstance().getReference("passwords")

        floatingActionButton = view.findViewById<FloatingActionButton>(R.id.add_password) as FloatingActionButton
        floatingActionButton.setOnClickListener {
            startActivity(Intent(activity, PasswordEditActivity::class.java))
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_password) as RecyclerView
        recyclerView.hasFixedSize()
        recyclerView.layoutManager = LinearLayoutManager(activity)
        pass_adapter = PasswordsAdapter(Collections.emptyList())

        val itemTouchHelper: ItemTouchHelper? = ItemTouchHelper(ItemTouchHelperCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, this))
        itemTouchHelper!!.attachToRecyclerView(recyclerView)
        recyclerView.adapter = pass_adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        databasereference.child(preferences.getString("id", "id")).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val passwords = mutableListOf<Password>()
                for (passwordDataSnapshot in dataSnapshot.children) {
                    val password = passwordDataSnapshot.getValue<Password>(Password::class.java)
                    passwords.add(password!!)
                }
                pass_adapter?.updateList(passwords)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun itemRemoved(position: Int) {
        val password: Password = pass_adapter!!.getItem(position)
        pass_adapter!!.removeItem(position)
        databasereference.child(password.getPassId()).removeValue()
    }

}