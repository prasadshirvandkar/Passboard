package com.midsizemango.passboard

import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.midsizemango.passboard.Models.Password


/**
 * Created by prasads on 15/12/17.
 */
class PasswordsAdapter(private var passwords: MutableList<Password>?) : RecyclerView.Adapter<PasswordsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var title: TextView = itemView.findViewById(R.id.title)
        private var email: TextView = itemView.findViewById(R.id.email)
        private var image: ImageView = itemView.findViewById(R.id.image_drawable)
        private var cardview: CardView = itemView.findViewById(R.id.card_view)

        private var password: Password? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(password: Password) {
            this.password = password
            title.text = password.getPassName()
            email.text = password.getPassEmail()
            cardview.setCardBackgroundColor(password.pass_color!!)
        }

        override fun onClick(view: View) {
            val context = view.context
            //Toast.makeText(context, password!!.getPassId(), Toast.LENGTH_SHORT).show()
            context.startActivity(PasswordEditActivity().newInstance(context, password))
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
}