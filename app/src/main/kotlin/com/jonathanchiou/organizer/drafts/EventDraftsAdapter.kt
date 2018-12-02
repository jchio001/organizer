package com.jonathanchiou.organizer.drafts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.persistence.EventDraft
import com.jonathanchiou.organizer.viewholder.AbsViewHolder

class EventDraftsAdapter : Adapter<AbsViewHolder<EventDraft>>() {

    private val eventDrafts = ArrayList<EventDraft>(3)

    override fun getItemCount(): Int {
        return eventDrafts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsViewHolder<EventDraft> {
        return EventDraftViewHolder(LayoutInflater.from(parent.context)
                                        .inflate(R.layout.cell_event_draft,
                                                 parent,
                                                 false))
    }

    override fun onBindViewHolder(holder: AbsViewHolder<EventDraft>, position: Int) {
        holder.display(eventDrafts[position])
    }

    fun addAll(eventDraftPage: List<EventDraft>) {
        eventDrafts.addAll(eventDraftPage)
        notifyDataSetChanged()
    }
}