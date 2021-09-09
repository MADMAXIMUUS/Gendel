package com.example.gendel.ui.message_recycler_view.views

interface MessageView {
    val id: String
    val from: String
    val timeStamp: String
    val fileUrl: String
    val text: String
    val answers: HashMap<String, Any>

    companion object {
        val MESSAGE_IMAGE: Int
            get() = 0
        val MESSAGE_TEXT: Int
            get() = 1
        val MESSAGE_VOICE: Int
            get() = 2
        val MESSAGE_FIlE: Int
            get() = 3
        val MESSAGE_QUIZ: Int
            get() = 4
        val MESSAGE_QUIZ_PO: Int
            get() = 5
    }

    fun getTypeView(): Int
}