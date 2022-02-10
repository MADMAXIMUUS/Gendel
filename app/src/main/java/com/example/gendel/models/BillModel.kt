package com.example.gendel.models

data class BillModel(
    val id: String = "",
    var storeName: String = "",
    var cost: String = "",
    var endDate: String = "",
    var startDate: String = "",
    var chatId: String = "",
    var tags: HashMap<String, Any> = hashMapOf()
)
