package com.newhaven.trashtotreasure.admin

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.newhaven.trashtotreasure.R
import com.newhaven.trashtotreasure.home.ui.myRequest.Event
import com.newhaven.trashtotreasure.home.ui.myRequest.OnContactUsClick

class AdminRequestListAdapter(
    val context: Context,
    val eventList: List<Event>,
    val onContactUsClick: OnContactUsClick
    ) :
    RecyclerView.Adapter<AdminRequestListAdapter.VHViewHolder>(){


    class VHViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val eId: TextView = itemView.findViewById(R.id.tv_reqId)
        val eName: TextView = itemView.findViewById(R.id.tv_eventName)
        val eAddress: TextView = itemView.findViewById(R.id.tv_address)
        val eContact: TextView = itemView.findViewById(R.id.contact_Number)
        val eStatus: TextView = itemView.findViewById(R.id.tvISApproved)
        val eDriver: TextView = itemView.findViewById(R.id.status)
        val tvContact: TextView = itemView.findViewById(R.id.contactUs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_event_admin, parent, false)

        return VHViewHolder(view)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: VHViewHolder, position: Int) {


        val ItemsViewModel = eventList[position]

        // sets the image to the imageview from our itemHolder class


        // sets the text to the textview from our itemHolder class
        holder.eId.text = "${ItemsViewModel.eId}"
        holder.eName.text = "${ItemsViewModel.name}"
        holder.eAddress.text = "${ItemsViewModel.address}"
        holder.eContact.text = "${ItemsViewModel.contact}"
        if (ItemsViewModel.isApproved) holder.eStatus.text = "Approved" else {
            holder.eStatus.text = "Approve"
            holder.eStatus.background = context.getDrawable(R.drawable.btn_red)
        }

        val eventDetails = hashMapOf(
            "eid" to ItemsViewModel.eId,
            "uid" to ItemsViewModel.uId,
            "name" to ItemsViewModel.name,
            "contact" to ItemsViewModel.contact,
            "address" to ItemsViewModel.address,
            "isApproved" to !ItemsViewModel.isApproved
        )
        holder.eStatus.setOnClickListener {
            onContactUsClick.onApproveClick(ItemsViewModel.eId,eventDetails as HashMap<String, Any> /* = java.util.HashMap<kotlin.String, kotlin.Any> */)
        }

        holder.tvContact.setOnClickListener {
            onContactUsClick.onClick(ItemsViewModel.eId,ItemsViewModel.uId)
        }


        holder.eDriver.setOnClickListener {
            if(ItemsViewModel.isApproved){
                onContactUsClick.onDriverClick(ItemsViewModel.eId)
            }else{
                Toast.makeText(context, "Please approve request first.", Toast.LENGTH_SHORT).show()
            }
        }
       // notifyItemChanged(position)
    }


    override fun getItemCount(): Int {
        return eventList.size
    }

}