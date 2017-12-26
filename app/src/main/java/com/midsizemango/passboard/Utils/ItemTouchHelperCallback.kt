package com.midsizemango.passboard.Utils

/**
 * Created by prasads on 15/12/17.
 */

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper


class ItemTouchHelperCallback : ItemTouchHelper.SimpleCallback {

    private var listener: DeletionListener? = null

    private constructor(dragDirs: Int, swipeDirs: Int) : super(dragDirs, swipeDirs)

    constructor(dragDirs: Int, swipeDirs: Int, listener: DeletionListener) : super(dragDirs, swipeDirs) {
        this.listener = listener
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener?.itemRemoved(viewHolder.adapterPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Fade out the view as it is swiped out of the parent's bounds
            val alpha = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.width.toFloat()
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    companion object {
        private val ALPHA_FULL = 1.0f
    }
}
