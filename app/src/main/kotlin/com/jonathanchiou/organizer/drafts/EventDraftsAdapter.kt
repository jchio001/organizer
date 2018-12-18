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

    private var eventDrafts = ArrayList<EventDraft>(3)

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
        // I'm notifying twice about changes to the dataset because I want the recyclerview to
        // - update the corresponding draft
        // - animate & move said draft to the top of the list
        // Which is not the effect of notifyItemRangeChanged()
        eventDrafts.set(position, updatedDraft)
        notifyItemChanged(position)

        val updatedDrafts = ArrayList<EventDraft>(eventDrafts.size)
        updatedDrafts.add(updatedDraft)

        for (i in 0 until eventDrafts.size) {
            if (i != position) {
                updatedDrafts.add(eventDrafts[i])
            }
        }

        eventDrafts = updatedDrafts
        notifyItemMoved(position, 0)
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