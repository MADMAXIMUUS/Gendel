package com.example.gendel.models

data class MessageModel(
    val id: String = "",
    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timeStamp: Any = "",
    var fileUrl: String = "empty",
    var choice: Boolean = false,
    var answers: HashMap<String, Any> = hashMapOf()
)
