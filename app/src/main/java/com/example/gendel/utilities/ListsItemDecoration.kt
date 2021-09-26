package com.example.gendel.utilities

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ListsItemDecoration(val spaceTop: Int, val spaceBottom: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = spaceBottom
        outRect.top = spaceTop
    }
}