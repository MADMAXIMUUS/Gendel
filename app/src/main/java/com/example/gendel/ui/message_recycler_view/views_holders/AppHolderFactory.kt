package com.example.gendel.ui.message_recycler_view.views_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.ui.message_recycler_view.views.MessageView

class AppHolderFactory {
    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                MessageView.MESSAGE_IMAGE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_item_image, parent, false)
                    HolderImageMessage(view)
                }
                MessageView.MESSAGE_VOICE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_item_voice, parent, false)
                    HolderVoiceMessage(view)
                }
                MessageView.MESSAGE_FIlE -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_item_file, parent, false)
                    HolderFileMessage(view)
                }
                MessageView.MESSAGE_QUIZ->{
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_item_quiz, parent, false)
                    HolderQuizMessage(view)
                }
                MessageView.MESSAGE_QUIZ_PO->{
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_item_quiz_po, parent, false)
                    HolderQuizPOMessage(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_item_text, parent, false)
                    HolderTextMessage(view)
                }
            }
        }
    }
}