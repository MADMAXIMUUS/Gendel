package com.example.gendel.ui.screens.lists

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gendel.R
import com.example.gendel.database.*
import com.example.gendel.models.CommonModel
import com.example.gendel.utilities.APP_ACTIVITY
import com.example.gendel.utilities.showToast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListAdapter(private val isFavorites: Boolean) :
    RecyclerView.Adapter<ListAdapter.MainListHolder>() {

    private var listItems = mutableListOf<CommonModel>()

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemStoreName: TextView = view.findViewById(R.id.main_list_store_name)
        val itemStartDate: TextView = view.findViewById(R.id.main_list_start_date_value)
        val itemEndDate: TextView = view.findViewById(R.id.main_list_end_date_value)
        val itemCost: TextView = view.findViewById(R.id.main_list_shipping_cost_value)
        val itemMember: TextView = view.findViewById(R.id.main_list_member_count_value)
        val itemTags: TextView = view.findViewById(R.id.main_list_tags)
        val itemFavorites: FloatingActionButton = view.findViewById(R.id.main_list_favorite_button)
        val itemRegister: FloatingActionButton = view.findViewById(R.id.main_list_register_button)
        var inFavorites = false
        var registered = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.main_list_item, parent, false)

        return MainListHolder(view)
    }

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemStoreName.text = listItems[position].storeName
        holder.itemCost.text = listItems[position].cost
        holder.itemStartDate.text = listItems[position].startDate
        holder.itemEndDate.text = listItems[position].endDate
        holder.itemMember.text = listItems[position].memberCount
        var tags: String = when {
            listItems[position].tags.size == 1 -> listItems[position].tags["tag 0"].toString()
            listItems[position].tags.size == 2 -> listItems[position].tags["tag 0"].toString() +
                    " " + listItems[position].tags["tag 1"].toString()
            listItems[position].tags.size >= 3 -> listItems[position].tags["tag 0"].toString() + " " +
                    " " + listItems[position].tags["tag 1"].toString() +
                    " " + listItems[position].tags["tag 2"].toString()
            else -> ""
        }
        if (tags.length > 20)
            tags = tags.substring(0..16) + "..."
        holder.itemTags.text = tags
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

    private fun removeFromList(item: CommonModel) {
        val index = listItems.indexOf(item)
        listItems.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun getItemCount(): Int = listItems.size

    override fun onViewAttachedToWindow(holder: MainListHolder) {
        holder.itemTags.setOnClickListener {
            val dialogView: View = LayoutInflater
                .from(APP_ACTIVITY)
                .inflate(R.layout.list_tags_viewer, null)
            listItems[holder.adapterPosition].tags.forEach {
                Log.e("tags", it.value.toString())
                val textView = TextView(APP_ACTIVITY)
                textView.text = it.value.toString()
                textView.textSize = 20f
                textView.setTextColor(
                    ContextCompat.getColor(
                        APP_ACTIVITY,
                        R.color.pink
                    )
                )
                val layoutParams = LinearLayout
                    .LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                layoutParams.setMargins(0, 0, 0, 10)
                textView.layoutParams = layoutParams
                dialogView.findViewById<LinearLayout>(R.id.list_tags_container)
                    .addView(textView)
            }
            AlertDialog.Builder(APP_ACTIVITY, R.style.MyDialogTheme)
                .setTitle(R.string.new_bill_tags_title)
                .setView(dialogView)
                .setPositiveButton(APP_ACTIVITY.getString(R.string.list_tag_viewer_ok)) { dialog, _ ->

                }.setOnDismissListener {
                    dialogView.findViewById<LinearLayout>(R.id.list_tags_container)
                        .removeAllViews()
                    (dialogView.parent as ViewGroup).removeView(dialogView)
                }.create().show()
        }
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
                        showToast(APP_ACTIVITY.getString(R.string.bill_in_favorites))
                    }.addOnFailureListener { showToast(it.message.toString()) }
            } else {
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FAVORITES)
                    .child(listItems[holder.adapterPosition].id)
                    .removeValue().addOnSuccessListener {
                        holder.inFavorites = false
                        holder.itemFavorites.setImageResource(R.drawable.ic_favorite_outline)
                        USER.favorites.remove(listItems[holder.adapterPosition].id)
                        if (isFavorites)
                            removeFromList(listItems[holder.adapterPosition])
                        showToast(APP_ACTIVITY.getString(R.string.bill_remove_favorites))
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
        holder.itemTags.setOnClickListener(null)
        super.onViewDetachedFromWindow(holder)
    }
}