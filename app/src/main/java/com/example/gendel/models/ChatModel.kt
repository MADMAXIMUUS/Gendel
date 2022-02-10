package com.example.gendel.models

data class ChatModel(
    val id: String = "",
    var photoUrl: String = "empty",
    var members: HashMap<String, Any> = hashMapOf(),
    var lastMessage: String = ""
)
