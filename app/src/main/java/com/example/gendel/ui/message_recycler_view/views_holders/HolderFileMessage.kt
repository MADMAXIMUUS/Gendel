package com.example.gendel.ui.message_recycler_view.views_holders

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.CURRENT_UID
import com.example.gendel.database.getFileFromStorage
import com.example.gendel.database.getReceivedName
import com.example.gendel.models.CommonModel
import com.example.gendel.ui.message_recycler_view.views.MessageView
import com.example.gendel.utilities.*
import java.io.File


class HolderFileMessage(val view: View) : RecyclerView.ViewHolder(view), MessageHolder {

    private lateinit var messageView: MessageView

    private val blockUserFileMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_file)
    private val blockReceivedFileMessage: ConstraintLayout =
        view.findViewById(R.id.bloc_received_file)

    private val chatUserFileMessageTime: TextView =
        blockUserFileMessage.findViewById(R.id.chat_user_file_time)
    private val chatReceivedFileMessageTime: TextView =
        blockReceivedFileMessage.findViewById(R.id.chat_received_file_time)

    private val chatUserFilename: TextView = blockUserFileMessage
        .findViewById(R.id.chat_user_filename)
    private val chatReceivedFilename: TextView = blockReceivedFileMessage
        .findViewById(R.id.chat_received_filename)

    private val chatUserProgressBar: ProgressBar = blockUserFileMessage
        .findViewById(R.id.user_progress_bar)
    private val chatReceivedProgressBar: ProgressBar = blockReceivedFileMessage
        .findViewById(R.id.received_progress_bar)

    private val chatUserFileDownloadButton: ImageView = blockUserFileMessage
        .findViewById(R.id.chat_user_button_download)
    private val chatReceivedFileDownloadButton: ImageView = blockReceivedFileMessage
        .findViewById(R.id.chat_received_button_download)

    private val chatReceivedName: TextView = blockReceivedFileMessage
        .findViewById(R.id.chat_received_message_file_name)

    override fun drawMessage(view: MessageView) {
        messageView = view
        if (view.from == CURRENT_UID) {
            blockUserFileMessage.visibility = View.VISIBLE
            blockReceivedFileMessage.visibility = View.GONE
            chatUserFileMessageTime.text =
                view.timeStamp.asTime()
            chatUserFilename.text = view.text
        } else {
            blockUserFileMessage.visibility = View.GONE
            blockReceivedFileMessage.visibility = View.VISIBLE
            chatReceivedFileMessageTime.text =
                view.timeStamp.asTime()
            chatReceivedFilename.text = view.text
            getReceivedName(view.from){
                chatReceivedName.text = it
            }
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRENT_UID) {
            chatUserFileDownloadButton.setOnClickListener { clickToButtonFile(view)}
        } else {
            chatReceivedFileDownloadButton.setOnClickListener { clickToButtonFile(view) }
        }
    }

    private fun clickToButtonFile(view: MessageView) {
        if (view.from == CURRENT_UID) {
            chatUserFileDownloadButton.visibility = View.INVISIBLE
            chatUserProgressBar.visibility =  View.VISIBLE
        }else{
            chatReceivedFileDownloadButton.visibility = View.INVISIBLE
            chatReceivedProgressBar.visibility =  View.VISIBLE
        }

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )

        try{
            if (checkPermission(WRITE_FILES)){
                file.createNewFile()
                getFileFromStorage(file,view.fileUrl) {
                    if (view.from == CURRENT_UID) {
                        chatUserFileDownloadButton.visibility = View.VISIBLE
                        chatUserProgressBar.visibility =  View.INVISIBLE
                    }else{
                        chatReceivedFileDownloadButton.visibility = View.VISIBLE
                        chatReceivedProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    override fun getMessage(): CommonModel =
        CommonModel(
            id = messageView.id,
            from = messageView.from,
            fileUrl = messageView.fileUrl,
            text = messageView.text,
            type = TYPE_MESSAGE_FIlE,
            timeStamp = messageView.timeStamp,
            answers = messageView.answers
        )

    override fun onDetach() {
        chatUserFileDownloadButton.setOnClickListener(null)
        chatReceivedFileDownloadButton.setOnClickListener(null)

    }
}