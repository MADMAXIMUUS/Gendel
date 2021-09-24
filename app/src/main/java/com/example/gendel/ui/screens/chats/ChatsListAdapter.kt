package com.example.gendel.ui.screens.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.models.CommonModel
import com.example.gendel.utilities.showToast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatsListAdapter : RecyclerView.Adapter<ChatsListAdapter.MainListHolder>() {

    private var listItems = mutableListOf<CommonModel>()

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemStoreName: TextView = view.findViewById(R.id.main_list_store_name)
        val itemStartDate: TextView = view.findViewById(R.id.main_list_start_date_value)
        val itemEndDate: TextView = view.findViewById(R.id.main_list_end_date_value)
        val itemCost: TextView = view.findViewById(R.id.main_list_shipping_cost_value)
        val itemMember: TextView = view.findViewById(R.id.main_list_member_count_value)
        val itemFavorites: FloatingActionButton = view.findViewById(R.id.main_list_favorite_button)
        val itemRegister: FloatingActionButton = view.findViewById(R.id.main_list_register_button)
        var inFavorites = false
        var registered = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)

        return MainListHolder(view)
    }

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemStoreName.text = listItems[position].storeName
        holder.itemCost.text = listItems[position].cost
        holder.itemStartDate.text = listItems[position].startDate
        holder.itemEndDate.text = listItems[position].endDate
        holder.itemMember.text = listItems[position].memberCount
        for (i in 0 until USER.favorites.size) {
            if (listItems[holder.position].id in USER.favorites.keys) {
                holder.inFavorites = true
                holder.itemFavorites.setImageResource(R.drawable.ic_favorite_color)
            }
        }
    }

    fun updateListItems(item: CommonModel) {
        listItems.add(item)
        notifyItemInserted(listItems.size)
    }

    override fun getItemCount(): Int = listItems.size

    override fun onViewAttachedToWindow(holder: MainListHolder) {
        holder.itemFavorites.setOnClickListener {
            if (!holder.inFavorites) {
                holder.inFavorites = true
                holder.itemFavorites.setImageResource(R.drawable.ic_favorite_color)
                val mapData = hashMapOf<String, Any>()
                mapData[listItems[holder.position].id] = "1"
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FAVORITES)
                    .setValue(mapData)
                    .addOnSuccessListener {
                        USER.favorites[listItems[holder.position].id] = "1"
                        showToast("Объявление добавлено в избранное")
                    }.addOnFailureListener { showToast(it.message.toString()) }
            } else {
                holder.inFavorites = false
                holder.itemFavorites.setImageResource(R.drawable.ic_favorite_outline)
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FAVORITES)
                    .child(listItems[holder.position].id)
                    .removeValue().addOnSuccessListener {
                        USER.favorites.remove(listItems[holder.position].id)
                        showToast("Объявление удалено из избранного")
                    }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
        }
        holder.itemRegister.setOnClickListener {
            if (!holder.registered) {

            } else {

            }
        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: MainListHolder) {
        holder.itemFavorites.setOnClickListener(null)
        holder.itemRegister.setOnClickListener(null)
        super.onViewDetachedFromWindow(holder)
    }
}