package com.example.gendel.ui.message_recycler_view.views_holders

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.CURRENT_UID
import com.example.gendel.database.getReceivedName
import com.example.gendel.ui.message_recycler_view.views.MessageView
import com.example.gendel.utilities.asTime


class HolderTextMessage(val view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private lateinit var messageView: MessageView
    private val blockUserMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_message)
    private val blockReceivedMessage: ConstraintLayout = view.findViewById(R.id.bloc_received_message)

    private val chatUserMessage: TextView =
        blockUserMessage.findViewById(R.id.chat_user_message)
    private val chatReceivedMessage: TextView =
        blockReceivedMessage.findViewById(R.id.chat_received_message)

    private val chatUserMessageTime: TextView =
        blockUserMessage.findViewById(R.id.chat_user_message_time)
    private val chatReceivedMessageTime: TextView =
        blockReceivedMessage.findViewById(R.id.chat_received_message_time)

    private val chatReceivedName: TextView = blockReceivedMessage
        .findViewById(R.id.chat_received_message_name)

    override fun drawMessage(view: MessageView) {
        messageView = view
        if (view.from == CURRENT_UID) {
            blockUserMessage.visibility = View.VISIBLE
            blockReceivedMessage.visibility = View.GONE
            chatUserMessage.text = view.text
            chatUserMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blockUserMessage.visibility = View.GONE
            blockReceivedMessage.visibility = View.VISIBLE
            chatReceivedMessage.text = view.text
            chatReceivedMessageTime.text =
                view.timeStamp.asTime()
            getReceivedName(view.from) {
                chatReceivedName.text = it
            }
        }
    }

    override fun getMessageId(): String  = messageView.id
    override fun getMessageFrom(): String  = messageView.from

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }
}