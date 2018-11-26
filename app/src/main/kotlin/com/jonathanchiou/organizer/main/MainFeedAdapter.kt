package com.jonathanchiou.organizer.main

import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.model.Notification

class MainFeedAdapter: Adapter<ViewHolder>() {

    var notification: Notification? = null

    override fun getItemCount(): Int {
        return if (notification == null) 0 else 1
    }

    // TODO: FIXED JANK STUBBED LOGIC
    override fun getItemViewType(position: Int): Int {
        return if (position == 1) 1 else -1
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        when (viewHolder) {
            is NotificationViewHolder -> {
                viewHolder.notificationTitle.text = notification!!.title
                viewHolder.notificationActionCount.text = notification!!.actionCount.toString()
                viewHolder.notificationText.text = notification!!.text
                viewHolder.notificationActionText.text = notification!!.actionType
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return NotificationViewHolder(LayoutInflater.from(parent.context)
                                          .inflate(R.layout.cell_main_feed_notification,
                                                   parent,
                                                   false))
    }
}