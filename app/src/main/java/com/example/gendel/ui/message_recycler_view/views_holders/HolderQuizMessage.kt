package com.example.gendel.ui.message_recycler_view.views_holders

import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.CURRENT_UID
import com.example.gendel.ui.message_recycler_view.views.MessageView
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.asTime

class HolderQuizMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {

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

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockUserQuiz.visibility = View.VISIBLE
            blockReceivedQuiz.visibility = View.GONE
            chatUserQuizTitle.text = view.text
            chatUserQuizMessageTime.text =
                view.timeStamp.asTime()
            for (i in 0 until view.answers.size)
            {
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
            for (i in 0 until view.answers.size)
            {
                val answer = CheckBox(APP_ACTIVITY)
                answer.text = view.answers["answer $i"].toString()
                answer.textSize = 14f
                blockReceivingAnswers.addView(answer)
            }
        }
    }

    override fun onAttach(view: MessageView) {

    }


    override fun onDetach() {

    }
}