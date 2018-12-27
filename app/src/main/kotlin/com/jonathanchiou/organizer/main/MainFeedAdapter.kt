package com.jonathanchiou.organizer.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Consumer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import butterknife.internal.DebouncingOnClickListener
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.model.EventBlurb
import com.jonathanchiou.organizer.api.model.Notification
import com.jonathanchiou.organizer.viewholder.AbsViewHolder

// Sealed classes don't work cross files yet.
interface MainFeedModel
class TitleModel(val title: String): MainFeedModel
class ButtonModel(val text: String): MainFeedModel

class MainFeedAdapter(mainFeedModels: List<MainFeedModel> = emptyList()):
    Adapter<AbsViewHolder<MainFeedModel>>() {

    var recyclerView: RecyclerView? = null

    val mainFeedModels = ArrayList<MainFeedModel>(13)

    var notificationConsumer: Consumer<Notification>? = null

    var eventBlurbConsumer: Consumer<EventBlurb>? = null

    var buttonClickConsumer: Consumer<View>? = null

    val onClickListener = object: DebouncingOnClickListener() {
        override fun doClick(v: View) {
            recyclerView?.let {
                val position = it.getChildAdapterPosition(v)
                when (getItemViewType(position)) {
                    1 -> notificationConsumer?.accept(mainFeedModels[position] as Notification)
                    2 -> eventBlurbConsumer?.accept(mainFeedModels[position] as EventBlurb)
                    4 -> buttonClickConsumer?.accept(v)
                    else -> return
                }
            }
        }
    }

    init {
        this.mainFeedModels.addAll(mainFeedModels)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemCount(): Int {
        return mainFeedModels.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (mainFeedModels[position]) {
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
        val viewHolder = when (viewType) {
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

        viewHolder.itemView.setOnClickListener(onClickListener)
        return viewHolder
    }
}