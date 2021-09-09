package com.example.gendel.models

data class UserModel(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var name: String = "",
    var state: String = "",
    var email: String = "",
    var photoUrl: String = "empty"
)