package com.example.gendel.models

data class CommonModel(
    val id: String = "",
    var name: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty",
    var storeName: String = "",
    var cost: String = "",
    var endDate: String = "",
    var startDate: String = "",
    var memberCount: String = "",
    var members: HashMap<String, Any> = hashMapOf(),
    var tags: HashMap<String, Any> = hashMapOf(),

    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timeStamp: Any = "",
    var fileUrl: String = "empty",
    var lastMessage: String = "",
    var choice: Boolean = false,
    var answers: HashMap<String, Any> = hashMapOf()
) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}