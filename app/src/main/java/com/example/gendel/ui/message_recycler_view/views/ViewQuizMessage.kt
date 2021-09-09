package com.example.gendel.ui.message_recycler_view.views

data class ViewQuizMessage(
    override val id: String,
    override val from: String,
    override val timeStamp: String,
    override val fileUrl: String,
    override val text: String = "",
    override val answers: HashMap<String, Any>
) : MessageView {
    override fun getTypeView(): Int {
        return MessageView.MESSAGE_QUIZ
    }

    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}