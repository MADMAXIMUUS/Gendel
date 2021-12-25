package com.example.gendel.ui.message_recycler_view.views_holders

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.CURRENT_UID
import com.example.gendel.database.getReceivedName
import com.example.gendel.ui.message_recycler_view.views.MessageView
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.asTime

class HolderQuizMessage(val view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private lateinit var messageView: MessageView
    private val blockUserQuiz: ConstraintLayout = view.findViewById(R.id.bloc_user_quiz)
    private val blockReceivedQuiz: ConstraintLayout =
        view.findViewById(R.id.bloc_received_quiz)

    private val blockUserAnswers: LinearLayout = blockUserQuiz.findViewById(R.id.chat_user_quiz)
    private val blockReceivingAnswers: LinearLayout =
        blockReceivedQuiz.findViewById(R.id.chat_received_quiz)

    private val userVoteButton: Button = blockUserQuiz.findViewById(R.id.chat_user_vote_button)
    private val receivingVoteButton: Button =
        blockReceivedQuiz.findViewById(R.id.chat_received_vote_button)

    private val chatUserQuizMessageTime: TextView =
        blockUserQuiz.findViewById(R.id.chat_user_quiz_time)
    private val chatReceivedQuizMessageTime: TextView =
        blockReceivedQuiz.findViewById(R.id.chat_received_quiz_time)

    private val chatUserQuizTitle: TextView =
        blockUserQuiz.findViewById(R.id.chat_user_quiz_title)
    private val chatReceivedQuizTitle: TextView =
        blockReceivedQuiz.findViewById(R.id.chat_received_quiz_title)

    private val chatReceivedQuizName: TextView = blockReceivedQuiz
        .findViewById(R.id.chat_received_message_quiz_name)

    override fun drawMessage(view: MessageView) {
        messageView = view
        if (view.from == CURRENT_UID) {
            blockUserQuiz.visibility = View.VISIBLE
            blockReceivedQuiz.visibility = View.GONE
            chatUserQuizTitle.text = view.text
            chatUserQuizMessageTime.text =
                view.timeStamp.asTime()
            for (i in 0 until view.answers.size) {
                val answer = CheckBox(APP_ACTIVITY)
                answer.text = view.answers["answer $i"].toString()
                answer.textSize = 14f
                blockUserAnswers.addView(answer)
            }
        } else {
            blockUserQuiz.visibility = View.GONE
            blockReceivedQuiz.visibility = View.VISIBLE
            chatReceivedQuizTitle.text = view.text
            chatReceivedQuizMessageTime.text =
                view.timeStamp.asTime()
            getReceivedName(view.from) {
                chatReceivedQuizName.text = it
            }
            for (i in 0 until view.answers.size) {
                val answer = CheckBox(APP_ACTIVITY)
                answer.text = view.answers["answer $i"].toString()
                answer.textSize = 14f
                blockReceivingAnswers.addView(answer)
            }
        }
    }

    override fun getMessageId(): String  = messageView.id
    override fun getMessageFrom(): String  = messageView.from
    override fun getFileUrl(): String = messageView.fileUrl
    override fun getMessageText(): String = messageView.text

    override fun onAttach(view: MessageView) {

    }


    override fun onDetach() {

    }
}