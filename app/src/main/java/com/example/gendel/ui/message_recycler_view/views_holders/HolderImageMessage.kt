package com.example.gendel.ui.message_recycler_view.views_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.CURRENT_UID
import com.example.gendel.database.getReceivedName
import com.example.gendel.ui.message_recycler_view.views.MessageView
import com.example.gendel.utilities.asTime
import com.example.gendel.utilities.downloadAndSetImage

class HolderImageMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val blockUserImageMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_image)
    private val blockReceivedImageMessage: ConstraintLayout = view.findViewById(R.id.bloc_received_image)

    private val chatUserImageMessage: ImageView =
        blockUserImageMessage.findViewById(R.id.chat_user_image)
    private val chatReceivedImage: ImageView =
        blockReceivedImageMessage.findViewById(R.id.chat_received_image)

    private val chatUserImageMessageTime: TextView =
        blockUserImageMessage.findViewById(R.id.chat_user_image_time)
    private val chatReceivedImageMessageTime: TextView =
        blockReceivedImageMessage.findViewById(R.id.chat_received_image_time)

    private val chatReceivedImageName: TextView = blockReceivedImageMessage
        .findViewById(R.id.chat_received_message_image_name)

    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockUserImageMessage.visibility = View.VISIBLE
            blockReceivedImageMessage.visibility = View.GONE
            chatUserImageMessage.downloadAndSetImage(view.fileUrl)
            chatUserImageMessageTime.text =
                view.timeStamp.asTime()
        } else {
            blockUserImageMessage.visibility = View.GONE
            blockReceivedImageMessage.visibility = View.VISIBLE
            chatReceivedImage.downloadAndSetImage(view.fileUrl)
            chatReceivedImageMessageTime.text =
                view.timeStamp.asTime()
            getReceivedName(view.from){
                chatReceivedImageName.text = it
            }
        }
    }

    override fun onAttach(view: MessageView) {

    }

    override fun onDetach() {

    }
}