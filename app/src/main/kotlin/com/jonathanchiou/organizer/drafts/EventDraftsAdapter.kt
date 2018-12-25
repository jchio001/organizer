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

class EventDraftsAdapter(recyclerView: RecyclerView): Adapter<AbsViewHolder<EventDraft>>() {

    var eventDrafts = ArrayList<EventDraft>(3)

    var itemConsumer: Consumer<Int>? = null

    var deleteConsumer: Consumer<Array<EventDraft>>? = null

    private val onClickListener = object: DebouncingOnClickListener() {
        override fun doClick(v: View) {
            itemConsumer?.accept(recyclerView.getChildAdapterPosition(v))
        }
    }

    private val onDeleteIconClickListener = object: DebouncingOnClickListener() {
        override fun doClick(v: View) {
            val index = recyclerView.getChildAdapterPosition(v.parent as View)
            val deletedDraft = arrayOf(eventDrafts[index])

            eventDrafts.removeAt(index)
            notifyItemRemoved(index)

            deleteConsumer?.accept(deletedDraft)
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
        eventDrafts[position] = updatedDraft
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

    fun undoDeletion(deletedDrafts: Array<EventDraft>) {
        val previousSize = eventDrafts.size
        val deletedDraftsCount = deletedDrafts.size

        if (deletedDraftsCount == 0) {
            return
        }

        if (previousSize == 0) {
            eventDrafts.addAll(deletedDrafts)
            notifyItemRangeInserted(0, deletedDraftsCount)
            return
        }

        val restoredEventDrafts = ArrayList<EventDraft>(previousSize + deletedDraftsCount)
        var lowestChangedIndex = -1

        var deletedDraftsIndex = 0

        for (i in 0 until previousSize) {
            val currentDraft = eventDrafts[i]

            if (deletedDraftsIndex < deletedDraftsCount) {
                val currentDeletedDraft = deletedDrafts[deletedDraftsIndex]

                if (currentDeletedDraft.lastUpdatedTime > currentDraft.lastUpdatedTime) {
                    restoredEventDrafts.add(currentDeletedDraft)

                    if (lowestChangedIndex == -1) {
                        lowestChangedIndex = i
                    }

                    ++deletedDraftsIndex
                }
            }

            restoredEventDrafts.add(currentDraft)
        }

        for (i in deletedDraftsIndex until deletedDraftsCount) {
            restoredEventDrafts.add(deletedDrafts[i])

            if (lowestChangedIndex == -1) {
                lowestChangedIndex = restoredEventDrafts.size - 1
            }
        }

        eventDrafts = restoredEventDrafts
        if (deletedDraftsCount == 1) {
            notifyItemInserted(lowestChangedIndex)
        } else {
            notifyItemRangeChanged(lowestChangedIndex, restoredEventDrafts.size)
        }
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
        val eventDraftViewHolder = EventDraftViewHolder(view)
        eventDraftViewHolder.deleteIcon.setOnClickListener(onDeleteIconClickListener)
        return eventDraftViewHolder
    }

    override fun onBindViewHolder(holder: AbsViewHolder<EventDraft>, position: Int) {
        holder.display(eventDrafts[position])
    }

    fun addAll(eventDraftPage: List<EventDraft>) {
        eventDrafts.addAll(eventDraftPage)
        notifyDataSetChanged()
    }
}