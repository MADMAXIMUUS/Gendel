package com.example.gendel.ui.screens.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.models.CommonModel
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.downloadAndSetImage
import com.example.gendel.utilities.replaceFragment
import com.google.android.material.imageview.ShapeableImageView

class ChatsListAdapter : RecyclerView.Adapter<ChatsListAdapter.ChatsListHolder>() {

    private var listItems = mutableListOf<CommonModel>()

    class ChatsListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.chats_list_item_name)
        val itemPlaceholderName: TextView = view.findViewById(R.id.chats_list_placeholder_name)
        val itemLastMessage: TextView = view.findViewById(R.id.chats_list_last_message)
        val itemPhoto: ShapeableImageView = view.findViewById(R.id.chats_list_item_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chats_list_item, parent, false)
        val holder = ChatsListHolder(view)
        holder.itemView.setOnClickListener {
            replaceFragment(GroupChatFragment(listItems[holder.adapterPosition]))
        }
        return holder
    }

    override fun onBindViewHolder(holder: ChatsListHolder, position: Int) {
        holder.itemName.text = listItems[position].storeName
        if (listItems[position].lastMessage.isNotEmpty())
            holder.itemLastMessage.text = listItems[position].lastMessage
        else
            holder.itemLastMessage.text = APP_ACTIVITY.getString(R.string.no_message_yet)
        holder.itemPhoto.downloadAndSetImage(listItems[position].photoUrl)
        holder.itemPlaceholderName.text = listItems[position].storeName[0].toString()
    }

    fun updateListItems(item: CommonModel) {
        listItems.add(0, item)
        notifyItemInserted(0)
    }

    override fun getItemCount(): Int = listItems.size
}