package com.zii.whatsappclone.model

data class User(
    val email: String? = "",
    val phone: String? = "",
    val name: String? = "",
    val pass: String? = "",
    val imageUrl: String? = "",
    val status: String? = "",
    val statusUrl: String? = "",
    val statusTime: String? = ""
)

data class Contact(
    val name: String?,
    val phone: String?
)