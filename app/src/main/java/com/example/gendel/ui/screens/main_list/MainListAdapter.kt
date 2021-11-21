package com.example.gendel.ui.screens.main_list

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

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {

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
        if (USER.verified == "false") {
            holder.itemRegister.isEnabled = false
            holder.itemFavorites.isEnabled = false
        }
        for (i in 0 until USER.favorites.size) {
            if (listItems[holder.position].id in USER.favorites.keys) {
                holder.inFavorites = true
                holder.itemFavorites.setImageResource(R.drawable.ic_favorite_color)
                break
            }
        }
        for (i in 0 until USER.registered.size) {
            if (listItems[holder.position].id in USER.registered.keys) {
                holder.registered = true
                holder.itemRegister.setImageResource(R.drawable.ic_unregister)
                break
            }
        }
    }

    fun updateListItems(item: CommonModel) {
        listItems.add(0, item)
        notifyItemInserted(0)
    }

    override fun getItemCount(): Int = listItems.size

    override fun onViewAttachedToWindow(holder: MainListHolder) {
        holder.itemFavorites.setOnClickListener {
            if (!holder.inFavorites) {
                val mapData = hashMapOf<String, Any>()
                mapData[listItems[holder.adapterPosition].id] = "1"
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FAVORITES)
                    .updateChildren(mapData)
                    .addOnSuccessListener {
                        holder.inFavorites = true
                        holder.itemFavorites.setImageResource(R.drawable.ic_favorite_color)
                        USER.favorites[listItems[holder.adapterPosition].id] = "1"
                        showToast("Объявление добавлено в избранное")
                    }.addOnFailureListener { showToast(it.message.toString()) }
            } else {
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FAVORITES)
                    .child(listItems[holder.adapterPosition].id)
                    .removeValue().addOnSuccessListener {
                        holder.inFavorites = false
                        holder.itemFavorites.setImageResource(R.drawable.ic_favorite_outline)
                        USER.favorites.remove(listItems[holder.adapterPosition].id)
                        showToast("Объявление удалено из избранного")
                    }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
        }
        holder.itemRegister.setOnClickListener {
            if (!holder.registered) {
                val mapData = hashMapOf<String, Any>()
                mapData[listItems[holder.adapterPosition].id] = "1"
                getBill(listItems[holder.adapterPosition].id) { bill ->
                    val members = bill.members
                    members["member ${bill.memberCount.toInt()}"] = CURRENT_UID
                    val newMemberCount = (bill.memberCount.toInt() + 1).toString()
                    val mapDataBills = hashMapOf<String, Any>()
                    mapDataBills[CHILD_ID] = bill.id
                    mapDataBills[CHILD_COST] = bill.cost
                    mapDataBills[CHILD_MEMBERS] = members
                    mapDataBills[CHILD_MEMBERS_COUNT] = newMemberCount
                    mapDataBills[CHILD_END_DATE] = bill.endDate
                    mapDataBills[CHILD_START_DATE] = bill.startDate
                    mapDataBills[CHILD_STORE_NAME] = bill.storeName
                    REF_DATABASE_ROOT.child(NODE_BILLS).child(bill.id).updateChildren(mapDataBills)
                        .addOnSuccessListener {
                            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
                                .child(CHILD_REGISTERED)
                                .updateChildren(mapData)
                                .addOnSuccessListener {
                                    holder.registered = true
                                    holder.itemRegister.setImageResource(R.drawable.ic_unregister)
                                    USER.registered[listItems[holder.adapterPosition].id] = "1"
                                    getBill(bill.id) { bill1 ->
                                        holder.itemMember.text = bill1.memberCount
                                    }
                                    showToast("Вы согласились помочь)")
                                }.addOnFailureListener { showToast(it.message.toString()) }
                        }.addOnFailureListener { showToast(it.message.toString()) }
                }

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