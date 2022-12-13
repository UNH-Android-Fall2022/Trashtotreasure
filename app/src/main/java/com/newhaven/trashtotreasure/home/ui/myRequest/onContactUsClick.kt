package com.newhaven.trashtotreasure.home.ui.myRequest

interface OnContactUsClick {
    fun onClick(eid: String,uid: String)

    fun onApproveClick(eid: String, event: HashMap<String, Any>)

    fun onDriverClick(eid: String)

    fun onDeclineClick(eid: String)
}