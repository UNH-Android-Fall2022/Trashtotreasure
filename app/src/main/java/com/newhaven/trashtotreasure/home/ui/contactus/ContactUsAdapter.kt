package com.newhaven.trashtotreasure.home.ui.contactus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newhaven.trashtotreasure.R
import kotlinx.android.synthetic.main.list_item_chat.view.*
import kotlinx.android.synthetic.main.list_item_chat.view.textview_chat_received
import kotlinx.android.synthetic.main.list_item_chat.view.textview_chat_receiver
import kotlinx.android.synthetic.main.list_item_chat.view.textview_chat_sender
import kotlinx.android.synthetic.main.list_item_chat.view.textview_chat_sent
import kotlinx.android.synthetic.main.list_item_chat.view.time
import kotlinx.android.synthetic.main.list_item_chat1.view.*

class ContactUsAdapter(
    private val contactUs: List<ContactUs>,
    private val uid: String,
    private val name: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class ViewHolderDefault(v: View) : RecyclerView.ViewHolder(v) {

    }
    class ViewHolderSeason(v: View) : RecyclerView.ViewHolder(v) {

    }


    override fun getItemViewType(position: Int): Int {
        val chatMessage = contactUs[position]
        return if (chatMessage.isAdmin == true)
            1
        else
            0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Create a new View
        val v1: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_chat, parent, false)
        val v2: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_chat1, parent, false)
        return if (viewType == 0) {
            ViewHolderDefault(v1)
        } else {
            ViewHolderSeason(v2)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = contactUs[position]
        when (holder.itemViewType) {
            0 -> {
                holder.itemView.textview_chat_sender.text = chatMessage.username
                holder.itemView.textview_chat_sent.text = chatMessage.text
                holder.itemView.time.text = chatMessage.time
                holder.itemView.sendername.text=chatMessage.username
            }
            1 -> {
                holder.itemView.textview_chat_receiver.text = chatMessage.username
                holder.itemView.textview_chat_received.text = chatMessage.text
                holder.itemView.time.text = chatMessage.time
                holder.itemView.recievername.text = "Admin"

            }
        }
    }

    override fun getItemCount(): Int {
        return contactUs.size
    }


}