package com.jonathanchiou.organizer.drafts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import butterknife.internal.DebouncingOnClickListener
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.persistence.EventDraft
import com.jonathanchiou.organizer.viewholder.AbsViewHolder
import io.reactivex.functions.Consumer

class EventDraftsAdapter(recyclerView: RecyclerView) : Adapter<AbsViewHolder<EventDraft>>() {

    private val eventDrafts = ArrayList<EventDraft>(3)

    var itemConsumer: Consumer<Int>? = null

    private val onClickListener = object: DebouncingOnClickListener() {
        override fun doClick(v: View) {
            itemConsumer?.accept(recyclerView.getChildAdapterPosition(v))
        }
    }

    fun getItem(position: Int): EventDraft {
        return eventDrafts[position]
    }

    fun updateItem(position: Int, updatedDraft: EventDraft) {
        eventDrafts.set(position, updatedDraft)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return eventDrafts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsViewHolder<EventDraft> {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_event_draft,
                     parent,
                     false)
        view.setOnClickListener(onClickListener)
        return EventDraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: AbsViewHolder<EventDraft>, position: Int) {
        holder.display(eventDrafts[position])
    }

    fun addAll(eventDraftPage: List<EventDraft>) {
        eventDrafts.addAll(eventDraftPage)
        notifyDataSetChanged()
    }
}