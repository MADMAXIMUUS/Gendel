package com.example.gendel.models

data class UserModel(
    val id: String = "",
    var name: String = "",
    var state: String = "",
    var email: String = "",
    var photoUrl: String = "empty",
    var verified: String = "false",
    var token: String = ""
)