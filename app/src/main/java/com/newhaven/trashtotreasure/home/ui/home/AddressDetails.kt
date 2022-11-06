package com.newhaven.trashtotreasure.home.ui.home

import kotlinx.serialization.Serializable

@Serializable
data class AddressDetails(
    private val flatNo: String,
    private val landmark: String,
    private val city: String,
    private val country: String,
    private val pin: String
)
