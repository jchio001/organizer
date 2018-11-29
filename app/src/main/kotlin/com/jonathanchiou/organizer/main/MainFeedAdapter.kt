package com.jonathanchiou.organizer.main

import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.viewholder.AbsViewHolder

interface MainFeedModel

class MainFeedAdapter: Adapter<AbsViewHolder<MainFeedModel>>() {

    private var mainFeedModels = ArrayList<MainFeedModel>(3)

    override fun getItemCount(): Int {
        return mainFeedModels.size
    }

    // TODO: FIXED JANK STUBBED LOGIC
    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> 1
            else -> 2
        }
    }

    override fun onBindViewHolder(viewHolder: AbsViewHolder<MainFeedModel>, position: Int) {
        viewHolder.display(mainFeedModels.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsViewHolder<MainFeedModel> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            1 -> NotificationViewHolder(layoutInflater.inflate(R.layout.cell_main_feed_notification,
                                                     parent,
                                                     false))
            else -> EventBlurbViewHolder(layoutInflater.inflate(R.layout.cell_event_blurb,
                                                      parent,
                                                      false))
        }
    }

    fun addMainFeedModels(mainFeedModelsPage: List<MainFeedModel>) {
        mainFeedModels.addAll(mainFeedModelsPage)
    }
}