package com.midsizemango.passboard.Adapter

import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import com.midsizemango.noteboard.Models.Note
import com.midsizemango.passboard.Activity.FingerprintAuthenticationActivity
import com.midsizemango.passboard.Models.Password
import com.midsizemango.passboard.R
import se.simbio.encryption.Encryption


/**
 * Created by prasads on 15/12/17.
 */
class PasswordListAdapter(private var passwords: MutableList<Password>?) : RecyclerView.Adapter<PasswordListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var title: TextView = itemView.findViewById(R.id.title)
        private var email: TextView = itemView.findViewById(R.id.email)
        private var title_image: FrameLayout = itemView.findViewById(R.id.title_image)
        private var title_text: TextView = itemView.findViewById(R.id.title_text)
        private var cardview: CardView = itemView.findViewById(R.id.rootView)
        val key = "passboard"
        val salt = "passboard2525"
        var iv = ByteArray(16)
        var encryption: Encryption = Encryption.getDefault(key, salt, iv)

        private var password: Password? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(password: Password) {
            this.password = password
            title.text = password.getPassName()
            email.text = encryption.decrypt(password.getPassEmail())
            title_image.setBackgroundColor(password.pass_color)
            if(password.pass_name!!.length > 2){
                title_text.text = password.pass_name!!.substring(0,2)
            }else {
                title_text.text = password.pass_name!!.substring(0,1)
            }
        }

        override fun onClick(view: View) {
            val context = view.context
            context.startActivity(FingerprintAuthenticationActivity().newInstance(context, password))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.password_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(passwords!![position])
    }

    override fun getItemCount(): Int {
        return passwords!!.size
    }

    fun updateList(passwords: MutableList<Password>) {
        if (passwords.size != this.passwords!!.size || !this.passwords!!.containsAll(passwords)) {
            this.passwords = passwords
            notifyDataSetChanged()
        }
    }

    fun removeItem(position: Int) {
        passwords!!.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItem(position: Int): Password {
        return passwords!![position]
    }

    var titleComparatorAesc: Comparator<Password> = Comparator { lhs, rhs ->
        val lTitle = lhs.pass_name
        val rTitle = rhs.pass_name
        lTitle!!.compareTo(rTitle!!, ignoreCase = true)
    }
}