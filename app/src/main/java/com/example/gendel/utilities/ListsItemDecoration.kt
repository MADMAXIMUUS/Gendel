package com.example.gendel.utilities

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ListsItemDecoration(private val spaceTop: Int, private val spaceBottom: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == parent.adapter?.itemCount!! - 1) {
            outRect.top = spaceTop
            outRect.bottom = 330
        } else {
            outRect.bottom = spaceBottom
            outRect.top = spaceTop
        }
    }
}