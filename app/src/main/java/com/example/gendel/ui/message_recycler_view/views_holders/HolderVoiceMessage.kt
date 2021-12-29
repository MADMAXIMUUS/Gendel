package com.example.gendel.ui.message_recycler_view.views_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.CURRENT_UID
import com.example.gendel.database.getReceivedName
import com.example.gendel.models.CommonModel
import com.example.gendel.ui.message_recycler_view.views.MessageView
import com.example.gendel.utilities.AppVoicePlayer
import com.example.gendel.utilities.TYPE_MESSAGE_VOICE
import com.example.gendel.utilities.asTime


class HolderVoiceMessage(val view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private val appVoicePlayer = AppVoicePlayer()
    private lateinit var messageView: MessageView

    private val blockUserVoiceMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_voice)
    private val blockReceivedVoiceMessage: ConstraintLayout =
        view.findViewById(R.id.bloc_received_voice)

    private val chatUserVoiceMessageTime: TextView =
        blockUserVoiceMessage.findViewById(R.id.chat_user_voice_time)
    private val chatReceivedVoiceMessageTime: TextView =
        blockReceivedVoiceMessage.findViewById(R.id.chat_received_voice_time)

    private val chatUserVoiceMessage: TextView =
        blockUserVoiceMessage.findViewById(R.id.chat_user_text)
    private val chatReceivedVoiceMessage: TextView =
        blockReceivedVoiceMessage.findViewById(R.id.chat_received_text)

    private val chatReceivedButtonPlay: ImageView =
        blockReceivedVoiceMessage.findViewById(R.id.chat_received_button_play)
    private val chatReceivedButtonStop: ImageView =
        blockReceivedVoiceMessage.findViewById(R.id.chat_received_button_stop)
    private val chatUserButtonPlay: ImageView =
        blockUserVoiceMessage.findViewById(R.id.chat_user_button_play)
    private val chatUserButtonStop: ImageView =
        blockUserVoiceMessage.findViewById(R.id.chat_user_button_stop)

    private val chatReceivedVoiceName: TextView = blockReceivedVoiceMessage
        .findViewById(R.id.chat_received_message_voice_name)

    override fun drawMessage(view: MessageView) {
        messageView = view
        if (view.from == CURRENT_UID) {
            blockUserVoiceMessage.visibility = View.VISIBLE
            blockReceivedVoiceMessage.visibility = View.GONE
            chatUserVoiceMessage.text = view.text
            chatUserVoiceMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blockUserVoiceMessage.visibility = View.GONE
            blockReceivedVoiceMessage.visibility = View.VISIBLE
            chatReceivedVoiceMessage.text = view.text
            chatReceivedVoiceMessageTime.text =
                view.timeStamp.asTime()
            getReceivedName(view.from) {
                chatReceivedVoiceName.text = it
            }
        }
    }

    override fun onAttach(view: MessageView) {
        appVoicePlayer.init()
        if (view.from == CURRENT_UID) {
            chatUserButtonPlay.setOnClickListener {
                chatUserButtonPlay.visibility = View.GONE
                chatUserButtonStop.visibility = View.VISIBLE
                chatUserButtonStop.setOnClickListener {
                    stop {
                        chatUserButtonStop.setOnClickListener(null)
                        chatUserButtonPlay.visibility = View.VISIBLE
                        chatUserButtonPlay.visibility = View.GONE
                    }
                }
                play(view) {
                    chatUserButtonPlay.visibility = View.VISIBLE
                    chatUserButtonStop.visibility = View.GONE
                }

            }
        } else {
            chatReceivedButtonPlay.setOnClickListener {
                chatReceivedButtonPlay.visibility = View.GONE
                chatReceivedButtonStop.visibility = View.VISIBLE
                chatReceivedButtonStop.setOnClickListener {
                    stop {
                        chatReceivedButtonStop.setOnClickListener(null)
                        chatReceivedButtonPlay.visibility = View.VISIBLE
                        chatReceivedButtonStop.visibility = View.GONE
                    }
                }
                play(view) {
                    chatReceivedButtonPlay.visibility = View.VISIBLE
                    chatReceivedButtonStop.visibility = View.GONE
                }
            }
        }
    }

    private fun play(view: MessageView, function: () -> Unit) {
        appVoicePlayer.play(view.id, view.fileUrl) { function() }
    }

    private fun stop(function: () -> Unit) {
        appVoicePlayer.stop {
            function()
        }
    }

    override fun getMessage(): CommonModel =
        CommonModel(
            id = messageView.id,
            from = messageView.from,
            fileUrl = messageView.fileUrl,
            text = messageView.text,
            type = TYPE_MESSAGE_VOICE,
            timeStamp = messageView.timeStamp,
            answers = messageView.answers
        )

    override fun onDetach() {
        chatUserButtonPlay.setOnClickListener(null)
        chatReceivedButtonPlay.setOnClickListener(null)
        appVoicePlayer.realize()
    }
}