package com.setu.passwordmanager.activities

import androidx.recyclerview.widget.ItemTouchHelper

abstract class SwipeGesture :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: androidx.recyclerview.widget.RecyclerView,
        viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        target: androidx.recyclerview.widget.RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(
        viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        direction: Int
    ) {

    }
}