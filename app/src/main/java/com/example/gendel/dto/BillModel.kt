package com.example.gendel.dto

data class BillModel(
    var storeName: String = "",
    var cost: String = "",
    var endDate: String = "",
    var startDate: String = "",
    var memberCount: Int = 1,
    var tags: List<String> = listOf()
)
