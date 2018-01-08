package com.midsizemango.passboard.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import com.midsizemango.passboard.Activity.FingerprintAuthenticationActivity

import com.midsizemango.passboard.Models.GeneralItem
import com.midsizemango.passboard.Models.ListItem
import com.midsizemango.passboard.Models.NameItem
import com.midsizemango.passboard.R
import se.simbio.encryption.Encryption
import com.afollestad.materialdialogs.MaterialDialog

/**
 * Created by ABC on 12/23/2017.
 */

class NoteSectionedListAdapter(private var consolidatedList: MutableList<ListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return consolidatedList[position].getType()
    }

    override fun getItemCount(): Int {
        return consolidatedList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            1 -> {
                val v1 = inflater.inflate(R.layout.note_item, parent, false)
                viewHolder = GeneralViewHolder(v1)
            }

            0 -> {
                val v2 = inflater.inflate(R.layout.password_item_header, parent, false)
                viewHolder = NameViewHolder(v2)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            1 -> {
                val generalItem = consolidatedList[position] as GeneralItem
                val itemHolder = viewHolder as GeneralViewHolder
                itemHolder.title.text = itemHolder.encryption.decrypt(generalItem.note!!.note_name)
                val s = itemHolder.encryption.decrypt(generalItem.note!!.note_text).length
                val sb = StringBuilder()
                for(i in 0..s){
                    sb.append("*")
                }
                itemHolder.content.text = sb.toString()
                itemHolder.title_image.setBackgroundColor(generalItem.note!!.note_color!!)
                if(generalItem.note!!.note_name!!.length > 2){
                    itemHolder.title_text.text = generalItem.note!!.note_name!!.substring(0,2)
                }else {
                    itemHolder.title_text.text = generalItem.note!!.note_name!!.substring(0,1)
                }


                itemHolder.cardview.setOnClickListener {
                    itemHolder.context.startActivity(FingerprintAuthenticationActivity().newInstance(itemHolder.context, generalItem.note))
                }

                val preferences = itemHolder.context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                val databasereference = FirebaseDatabase.getInstance().getReference("notes")

                itemHolder.cardview.setOnLongClickListener {
                    MaterialDialog.Builder(itemHolder.context)
                            .title("Delete")
                            .content("Do you want to Delete the Password?")
                            .positiveText("delete")
                            .negativeText("cancel")
                            .onPositive { dialog, which ->
                                consolidatedList.removeAt(position)
                                databasereference.child(preferences.getString("id", "id")).child(generalItem.password!!.pass_user_id).removeValue()
                            }
                            .onNegative { dialog, which ->
                                dialog.dismiss()
                            }.show()
                    true
                }

            }

            0 -> {
                val nameItem = consolidatedList[position] as NameItem
                val nameViewHolder = viewHolder as NameViewHolder
                nameViewHolder.titleName.text = nameItem.name!!
            }
        }
    }

    internal class NameViewHolder(v: View) : RecyclerView.ViewHolder(v){
        internal var titleName: TextView = itemView.findViewById(R.id.tvTitle)
    }

    internal class GeneralViewHolder(v: View) : RecyclerView.ViewHolder(v){
        internal var title: TextView = itemView.findViewById(R.id.title)
        internal var content: TextView = itemView.findViewById(R.id.content)
        internal var title_image: FrameLayout = itemView.findViewById(R.id.title_image)
        internal var title_text: TextView = itemView.findViewById(R.id.title_text)
        internal var cardview: CardView = itemView.findViewById(R.id.rootView)
        val key = "passboard"
        val salt = "passboard2525"
        var iv = ByteArray(16)
        var encryption: Encryption = Encryption.getDefault(key, salt, iv)
        internal val context = v.context

    }
}
