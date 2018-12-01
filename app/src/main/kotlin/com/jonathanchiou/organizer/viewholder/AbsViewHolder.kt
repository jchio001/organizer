package com.jonathanchiou.organizer.viewholder

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.View

abstract class AbsViewHolder<T>(itemView: View): ViewHolder(itemView) {

    abstract fun display(listItem: T)
}