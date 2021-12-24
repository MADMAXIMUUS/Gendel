package com.example.gendel.ui.message_recycler_view.views_holders

import com.example.gendel.ui.message_recycler_view.views.MessageView

interface MessageHolder {

    fun drawMessage(view: MessageView)
    fun onAttach(view: MessageView)
    fun getMessageId(): String
    fun getMessageFrom(): String
    fun getFileUrl(): String
    fun onDetach()
}