package com.example.gendel.ui.message_recycler_view.views_holders

import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.CURRENT_UID
import com.example.gendel.database.getReceivedName
import com.example.gendel.ui.message_recycler_view.views.MessageView
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.asTime

class HolderQuizPOMessage(val view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private lateinit var messageView: MessageView
    private val blockUserQuizPO: ConstraintLayout = view.findViewById(R.id.bloc_user_quiz_po)
    private val blockReceivedQuizPO: ConstraintLayout =
        view.findViewById(R.id.bloc_received_quiz_po)

    private val blockUserAnswers: RadioGroup = view.findViewById(R.id.chat_user_quiz_po)
    private val blockReceivingAnswers: RadioGroup = view.findViewById(R.id.chat_received_quiz_po)

    private val chatUserQuizPOMessageTime: TextView =
        blockUserQuizPO.findViewById(R.id.chat_user_quiz_po_time)
    private val chatReceivedQuizPOMessageTime: TextView =
        blockReceivedQuizPO.findViewById(R.id.chat_received_quiz_po_time)

    private val chatUserQuizPOTitle: TextView =
        blockUserQuizPO.findViewById(R.id.chat_user_quiz_po_title)
    private val chatReceivedQuizPOTitle: TextView =
        blockReceivedQuizPO.findViewById(R.id.chat_received_quiz_po_title)

    private val chatReceivedQuizPOName: TextView = blockReceivedQuizPO
        .findViewById(R.id.chat_received_message_quiz_po_name)

    override fun drawMessage(view: MessageView) {
        messageView = view
        if (view.from == CURRENT_UID) {
            blockUserQuizPO.visibility = View.VISIBLE
            blockReceivedQuizPO.visibility = View.GONE
            chatUserQuizPOTitle.text = view.text
            chatUserQuizPOMessageTime.text =
                view.timeStamp.asTime()
            for (i in 0 until view.answers.size)
            {
                val answer = RadioButton(APP_ACTIVITY)
                answer.text = view.answers["answer $i"].toString()
                answer.textSize = 14f
                blockUserAnswers.addView(answer)
            }
        } else {
            blockUserQuizPO.visibility = View.GONE
            blockReceivedQuizPO.visibility = View.VISIBLE
            chatReceivedQuizPOTitle.text = view.text
            chatReceivedQuizPOMessageTime.text =
                view.timeStamp.asTime()
            getReceivedName(view.from){
                chatReceivedQuizPOName.text = it
            }
            for (i in 0 until view.answers.size)
            {
                val answer = RadioButton(APP_ACTIVITY)
                answer.text = view.answers["answer $i"].toString()
                answer.textSize = 14f
                blockReceivingAnswers.addView(answer)
            }
        }
    }


    override fun getMessageId(): String  = messageView.id
    override fun getMessageFrom(): String  = messageView.from
    override fun getFileUrl(): String = messageView.fileUrl

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }
}