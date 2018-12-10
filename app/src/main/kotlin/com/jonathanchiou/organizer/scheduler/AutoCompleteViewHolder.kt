package com.jonathanchiou.organizer.scheduler

import android.view.View
import android.widget.TextView
import com.jonathanchiou.organizer.viewholder.AbsViewHolder

interface AutoCompleteModel {
    fun getTextForViewHolder(): String
}

class AutoCompleteViewHolder(itemView: View):
    AbsViewHolder<AutoCompleteModel>(itemView) {

    override fun display(listItem: AutoCompleteModel) {
        (itemView as TextView).text = listItem.getTextForViewHolder()
    }
}