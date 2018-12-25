package com.jonathanchiou.organizer.drafts

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.persistence.EventDraft
import com.jonathanchiou.organizer.viewholder.AbsViewHolder
import java.text.SimpleDateFormat
import java.util.*

class EventDraftViewHolder(itemView: View): AbsViewHolder<EventDraft>(itemView) {

    @BindView(R.id.event_draft_title)
    lateinit var eventDraftTitle: TextView

    @BindView(R.id.event_draft_last_updated_text)
    lateinit var eventDraftLastUpdatedText: TextView

    @BindView(R.id.delete_icon)
    lateinit var deleteIcon: ImageView

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun display(listItem: EventDraft) {
        val title = listItem.title
        eventDraftTitle.text = if (title.isEmpty()) "(No title)" else title

        val description = listItem.description
        eventDraftLastUpdatedText.text = "Last updated ${DATE_FORMAT.format(listItem.lastUpdatedTime)}"
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.US)
    }
}