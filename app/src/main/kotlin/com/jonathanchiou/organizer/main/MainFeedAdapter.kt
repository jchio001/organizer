package com.jonathanchiou.organizer.main

import androidx.recyclerview.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.model.EventBlurb
import com.jonathanchiou.organizer.api.model.Notification
import com.jonathanchiou.organizer.viewholder.AbsViewHolder
import java.lang.IllegalStateException

// Sealed classes don't work cross files yet.
interface MainFeedModel
class TitleModel(val title: String): MainFeedModel
class ButtonModel(val text: String): MainFeedModel

class MainFeedAdapter: Adapter<AbsViewHolder<MainFeedModel>>() {

    private var mainFeedModels = ArrayList<MainFeedModel>(3)

    override fun getItemCount(): Int {
        return mainFeedModels.size
    }

    // TODO: FIXED JANK STUBBED LOGIC
    override fun getItemViewType(position: Int): Int {
        return when (mainFeedModels.get(position)) {
            is Notification -> 1
            is EventBlurb -> 2
            is TitleModel -> 3
            is ButtonModel -> 4
            else -> -1
        }
    }

    override fun onBindViewHolder(viewHolder: AbsViewHolder<MainFeedModel>, position: Int) {
        viewHolder.display(mainFeedModels[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsViewHolder<MainFeedModel> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            1 -> NotificationViewHolder(layoutInflater.inflate(R.layout.cell_main_feed_notification,
                                                               parent,
                                                               false))
            2 -> EventBlurbViewHolder(layoutInflater.inflate(R.layout.cell_event_blurb,
                                                                parent,
                                                                false))
            3 -> TitleViewHolder(layoutInflater.inflate(R.layout.cell_main_feed_title,
                                                           parent,
                                                           false))
            4 -> ButtonViewHolder(layoutInflater.inflate(R.layout.cell_main_feed_button,
                                                         parent,
                                                         false))
            else -> throw IllegalStateException("Invalid viewType $viewType.")
        }
    }

    fun addMainFeedModels(mainFeedModelsPage: List<MainFeedModel>) {
        mainFeedModels.addAll(mainFeedModelsPage)
    }
}