package com.jonathanchiou.organizer.main

import android.view.View
import android.widget.TextView
import com.jonathanchiou.organizer.viewholder.AbsViewHolder

class TitleViewHolder(itemView: View): AbsViewHolder<MainFeedModel>(itemView) {

    override fun display(listItem: MainFeedModel) {
        (itemView as TextView).text = (listItem as TitleModel).title
    }
}