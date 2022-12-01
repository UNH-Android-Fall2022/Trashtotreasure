package com.newhaven.trashtotreasure.home.ui.myRequest

import java.text.FieldPosition

interface OnContactUsClick {
    fun onClick()

    fun onApproveClick(eid: String, event: HashMap<String, Any>)

    fun onDriverClick(eid: String)
}