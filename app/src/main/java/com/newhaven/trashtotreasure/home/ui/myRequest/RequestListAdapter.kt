package com.newhaven.trashtotreasure.home.ui.myRequest

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.red
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.newhaven.trashtotreasure.R
import kotlinx.coroutines.NonDisposableHandle.parent

class RequestListAdapter(val context: Context,val eventList: List<Event>) :
    RecyclerView.Adapter<RequestListAdapter.VHViewHolder>() {


    class VHViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val eName: TextView = itemView.findViewById(R.id.tv_eventName)
        val eAddress: TextView = itemView.findViewById(R.id.tv_address)
        val eContact: TextView = itemView.findViewById(R.id.contact_Number)
        val eStatus: TextView = itemView.findViewById(R.id.status)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_event, parent, false)

        return VHViewHolder(view)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: VHViewHolder, position: Int) {
        val ItemsViewModel = eventList[position]

        // sets the image to the imageview from our itemHolder class


        // sets the text to the textview from our itemHolder class
        holder.eName.text = "${ItemsViewModel.name}"
        holder.eAddress.text = "${ItemsViewModel.address}"
        holder.eContact.text = "${ItemsViewModel.contact}"
        if (ItemsViewModel.isApproved) holder.eStatus.text = "Approved" else {

            holder.eStatus.text = "NotApproved"
        holder.eStatus.setTextColor(context.getColor(R.color.red))}


    }

    override fun getItemCount(): Int {
        return eventList.size
    }


}