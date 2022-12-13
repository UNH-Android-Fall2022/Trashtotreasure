package com.newhaven.trashtotreasure.home.ui.contactus

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    private val chatMessages: List<ContactUs>,
    private val uid: String,
    private val name: String,
    private val isGroup: Boolean,
    private val memberdata: ArrayList<MemberData>?,
    private val context: Context?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        return ViewHolder(inflater, parent)
//    }

    class ViewHolderDefault(v: View) : RecyclerView.ViewHolder(v) {
        private var chatTextSent: TextView? = null
        private var chatTextReceived: TextView? = null
        private var chatTextsender: TextView? = null
        private var chatTextReceiver: TextView? = null
        private var chatTextReceiverName: TextView? = null
        private var chatTextsenderName: TextView? = null
        private var chatTextsenderDate: TextView? = null
        private var chatTextReceiverDate: TextView? = null

        init {
            chatTextSent = itemView.findViewById(R.id.textview_chat_sent)
            chatTextReceived = itemView.findViewById(R.id.textview_chat_received)
            chatTextsender = itemView.findViewById(R.id.textview_chat_sender)
            chatTextReceiver = itemView.findViewById(R.id.textview_chat_receiver)
            chatTextReceiverName = itemView.findViewById(R.id.recievername)
            chatTextsenderName = itemView.findViewById(R.id.sendername)
            chatTextsenderDate = itemView.findViewById(R.id.senderdate)
            chatTextReceiverDate = itemView.findViewById(R.id.recieverdate)
        }
    }
    class ViewHolderSeason(v: View) : RecyclerView.ViewHolder(v) {
         var chatTextSent: TextView? = null
         var chatTextReceived: TextView? = null
         var chatTextsender: TextView? = null
         var chatTextReceiver: TextView? = null
         var chatTextReceiverName: TextView? = null
         var chatTextsenderName: TextView? = null
         var chatTextsenderDate: TextView? = null
         var chatTextReceiverDate: TextView? = null

        init {
            chatTextSent = itemView.findViewById(R.id.textview_chat_sent)
            chatTextReceived = itemView.findViewById(R.id.textview_chat_received)
            chatTextsender = itemView.findViewById(R.id.textview_chat_sender)
            chatTextReceiver = itemView.findViewById(R.id.textview_chat_receiver)
            chatTextReceiverName = itemView.findViewById(R.id.recievername)
            chatTextsenderName = itemView.findViewById(R.id.sendername)
            chatTextsenderDate = itemView.findViewById(R.id.senderdate)
            chatTextReceiverDate = itemView.findViewById(R.id.recieverdate)
        }
    }


    override fun getItemViewType(position: Int): Int {
        val chatMessage = chatMessages[position]

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
        val chatMessage = chatMessages[position]
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
//    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(
//        inflater.inflate(R.layout.list_item_chat, parent, false)
//    ) {
//
//        private var chatTextSent: TextView? = null
//        private var chatTextReceived: TextView? = null
//        private var chatTextsender: TextView? = null
//        private var chatTextReceiver: TextView? = null
//
//        init {
//            chatTextSent = itemView.findViewById(R.id.textview_chat_sent)
//            chatTextReceived = itemView.findViewById(R.id.textview_chat_received)
//            chatTextsender = itemView.findViewById(R.id.textview_chat_sender)
//            chatTextReceiver = itemView.findViewById(R.id.textview_chat_receiver)
//        }
//
//    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }


}