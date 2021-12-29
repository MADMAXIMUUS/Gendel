package com.example.gendel.ui.screens.chats

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.CURRENT_UID
import com.example.gendel.database.deleteMessageForAll
import com.example.gendel.database.deleteMessageForSingle
import com.example.gendel.databinding.FragmentChatBinding
import com.example.gendel.models.CommonModel
import com.example.gendel.ui.message_recycler_view.views.MessageView
import com.example.gendel.ui.message_recycler_view.views_holders.AppHolderFactory
import com.example.gendel.ui.message_recycler_view.views_holders.MessageHolder
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.TYPE_MESSAGE_TEXT
import com.example.gendel.utilities.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog

class GroupChatAdapter(private val group: CommonModel, private val binding: FragmentChatBinding) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMessagesCache = mutableListOf<MessageView>()
    private val listHolders = mutableListOf<MessageHolder>()
    var clicked: CommonModel = CommonModel()
    private var clickedId: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).drawMessage(listMessagesCache[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(listMessagesCache[holder.adapterPosition])
        listHolders.add((holder as MessageHolder))
        holder.itemView.setOnClickListener {
            val contextMenu = BottomSheetDialog(APP_ACTIVITY, R.style.SheetDialog)
            contextMenu.setContentView(R.layout.context_menu)
            val reply = contextMenu
                .findViewById<ConstraintLayout>(R.id.chat_context_menu_reply_root)
            val forward = contextMenu
                .findViewById<ConstraintLayout>(R.id.chat_context_menu_forward_root)
            val edit = contextMenu
                .findViewById<ConstraintLayout>(R.id.chat_context_menu_edit_root)
            val pin = contextMenu
                .findViewById<ConstraintLayout>(R.id.chat_context_menu_pin_root)
            val copy = contextMenu
                .findViewById<ConstraintLayout>(R.id.chat_context_menu_copy_root)
            val delete = contextMenu
                .findViewById<ConstraintLayout>(R.id.chat_context_menu_delete_root)
            delete!!.setOnClickListener {
                if (holder.getMessage().from == CURRENT_UID) {
                    var checked = false
                    AlertDialog.Builder(APP_ACTIVITY, R.style.MyDialogTheme)
                        .setTitle(R.string.delete_message_title)
                        .setMultiChoiceItems(R.array.ContextMenuChoices, null) { _, _, isChecked ->
                            checked = isChecked
                        }
                        .setPositiveButton(R.string.delete_for_all_dialog_delete) { dialog, _ ->
                            if (checked) {
                                deleteMessageForAll(
                                    group,
                                    (holder as MessageHolder).getMessage().id,
                                    (holder as MessageHolder).getMessage().fileUrl
                                )
                            } else {
                                deleteMessageForSingle(
                                    group.id,
                                    (holder as MessageHolder).getMessage().id
                                )
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.delete_for_all_dialog_cancel) { dialog, _ ->
                            dialog.cancel()
                        }.create().show()
                    contextMenu.dismiss()
                }
                else{
                    AlertDialog.Builder(APP_ACTIVITY, R.style.MyDialogTheme)
                        .setTitle(R.string.delete_message_title)
                        .setMessage(APP_ACTIVITY.getString(R.string.delete_message))
                        .setPositiveButton(R.string.delete_for_all_dialog_delete) { dialog, _ ->
                            deleteMessageForSingle(
                                group.id,
                                (holder as MessageHolder).getMessage().id
                            )
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.delete_for_all_dialog_cancel) { dialog, _ ->
                            dialog.cancel()
                        }.create().show()
                    contextMenu.dismiss()
                }
            }
            edit!!.setOnClickListener {
                if ((holder as MessageHolder).getMessage().type == TYPE_MESSAGE_TEXT) {
                    binding.chatInputMessage.setText((holder as MessageHolder).getMessage().text)
                    binding.chatButtonSendMessage.visibility = View.GONE
                    binding.chatButtonSendEditedMessage.visibility = View.VISIBLE
                    binding.chatInputMessage.setSelection(binding.chatInputMessage.length())
                }
                clicked = (holder as MessageHolder).getMessage()
                clickedId = holder.adapterPosition
                contextMenu.dismiss()
            }
            copy!!.setOnClickListener {
                val clipboardManager =
                    APP_ACTIVITY.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip =
                    ClipData.newPlainText("Gendel", (holder as MessageHolder).getMessage().text)
                clipboardManager.setPrimaryClip(clip)
                contextMenu.dismiss()
                showToast(APP_ACTIVITY.getString(R.string.added_to_clipboard))
            }

            contextMenu.show()
        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDetach()
        listHolders.remove((holder as MessageHolder))
        holder.itemView.setOnClickListener(null)
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return listMessagesCache[position].getTypeView()
    }

    override fun getItemCount(): Int = listMessagesCache.size

    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            notifyItemInserted(listMessagesCache.size)
        }
        onSuccess()
    }

    fun updateItem(item: MessageView, onSuccess: (Int) -> Unit) {
        if (clickedId != -1) {
            listMessagesCache[clickedId] = item
            notifyItemChanged(clickedId)
            onSuccess(clickedId)
            clickedId = -1
        }
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!listMessagesCache.contains(item)) {
            listMessagesCache.add(item)
            listMessagesCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun onDestroy() {
        listHolders.forEach {
            it.onDetach()
        }
    }

    fun removeValue(item: MessageView){
        val index = listMessagesCache.indexOf(item)
        listMessagesCache.remove(item)
        notifyItemRemoved(index)
    }
}
