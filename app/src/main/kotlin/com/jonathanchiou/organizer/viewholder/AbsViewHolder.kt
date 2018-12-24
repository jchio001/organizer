package com.jonathanchiou.organizer.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder

abstract class AbsViewHolder<T>(itemView: View): ViewHolder(itemView) {

    abstract fun display(listItem: T)
}