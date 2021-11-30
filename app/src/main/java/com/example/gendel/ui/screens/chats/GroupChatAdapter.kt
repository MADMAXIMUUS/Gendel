package com.example.gendel.ui.screens.chats

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.ui.message_recycler_view.views.MessageView
import com.example.gendel.ui.message_recycler_view.views_holders.AppHolderFactory
import com.example.gendel.ui.message_recycler_view.views_holders.MessageHolder
import com.example.gendel.utilities.APP_ACTIVITY
import com.google.android.material.bottomsheet.BottomSheetDialog

class GroupChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMessagesCache = mutableListOf<MessageView>()
    private val listHolders = mutableListOf<MessageHolder>()

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
            contextMenu.setContentView(R.layout.context_menu)
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
}
