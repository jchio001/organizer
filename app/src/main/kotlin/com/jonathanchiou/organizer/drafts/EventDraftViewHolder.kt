package com.jonathanchiou.organizer.drafts

import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.persistence.EventDraft
import com.jonathanchiou.organizer.viewholder.AbsViewHolder
import java.text.SimpleDateFormat
import java.util.*

class EventDraftViewHolder(itemView: View) : AbsViewHolder<EventDraft>(itemView) {

    @BindView(R.id.event_draft_title)
    lateinit var eventDraftTitle: TextView

    @BindView(R.id.event_draft_last_updated_text)
    lateinit var eventDraftLastUpdatedText: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun display(listItem: EventDraft) {
        eventDraftTitle.text = listItem.title
        eventDraftLastUpdatedText.text =
            "Last updated ${DATE_FORMAT.format(listItem.lastUpdatedTime)}"
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.US)
    }
}