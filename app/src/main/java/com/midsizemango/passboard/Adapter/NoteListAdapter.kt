package com.midsizemango.passboard.Adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.database.FirebaseDatabase
import com.midsizemango.noteboard.Models.Note
import com.midsizemango.passboard.Activity.FingerprintAuthenticationActivity
import com.midsizemango.passboard.Fragment.NotesListFragment
import com.midsizemango.passboard.R
import se.simbio.encryption.Encryption

/**
 * Created by prasads on 15/12/17.
 */
class NoteListAdapter(private var notes: MutableList<Note>?) : RecyclerView.Adapter<NoteListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        internal var title: TextView = itemView.findViewById(R.id.title)
        internal var content: TextView = itemView.findViewById(R.id.content)
        internal var title_image: FrameLayout = itemView.findViewById(R.id.title_image)
        internal var title_text: TextView = itemView.findViewById(R.id.title_text)
        internal var cardview: CardView = itemView.findViewById(R.id.rootView)
        val key = "passboard"
        val salt = "passboard2525"
        var iv = ByteArray(16)
        var encryption: Encryption = Encryption.getDefault(key, salt, iv)

        private var note: Note? = null
        internal var context: Context = itemView.context
    }

    var titleComparatorAesc: Comparator<Note> = Comparator { lhs, rhs ->
        val lTitle = lhs.note_name
        val rTitle = rhs.note_name
        lTitle!!.compareTo(rTitle!!, ignoreCase = true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.bind(notes!![position])
        val note = notes!![position]
        holder.title.text = holder.encryption.decrypt(note.note_name)
        val s = holder.encryption.decrypt(note.note_text).length
        val sb = StringBuilder()
        for(i in 0..s){
            sb.append("*")
        }
        holder.content.text = sb.toString()
        holder.title_image.setBackgroundColor(note.note_color!!)
        if(note.note_name!!.length >= 4){
            holder.title_text.text = holder.encryption.decrypt(note.note_name!!).substring(0,3)
        }else {
            holder.title_text.text = holder.encryption.decrypt(note.note_name!!)
        }

        holder.cardview.setOnClickListener {
            holder.context.startActivity(FingerprintAuthenticationActivity().newInstance(holder.context, note))
        }

        val preferences = holder.context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val databasereference = FirebaseDatabase.getInstance().getReference("notes")
        holder.cardview.setOnLongClickListener {
            MaterialDialog.Builder(holder.context)
                    .title("Delete")
                    .content("Do you want to Delete the Password?")
                    .positiveText("delete")
                    .negativeText("cancel")
                    .onPositive { dialog, which ->
                        removeItem(position)
                        databasereference.child(preferences.getString("id", "id")).child(note.note_user_id).removeValue()
                    }
                    .onNegative { dialog, which ->
                        dialog.dismiss()
                    }.show()
            true
        }
    }

    override fun getItemCount(): Int {
        return notes!!.size
    }

    fun updateList(notes: MutableList<Note>) {
        if (notes.size != this.notes!!.size || !this.notes!!.containsAll(notes)) {
            this.notes = notes
            notifyDataSetChanged()
        }
    }

    fun removeItem(position: Int) {
        notes!!.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItem(position: Int): Note {
        return notes!![position]
    }
}