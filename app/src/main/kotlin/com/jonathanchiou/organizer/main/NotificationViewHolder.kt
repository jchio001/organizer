package com.jonathanchiou.organizer.main

import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.model.Notification
import com.jonathanchiou.organizer.viewholder.AbsViewHolder

class NotificationViewHolder(itemView: View) : AbsViewHolder<MainFeedModel>(itemView) {

    @BindView(R.id.notification_title)
    lateinit var notificationTitle: TextView

    @BindView(R.id.notification_action_count)
    lateinit var notificationActionCount: TextView

    @BindView(R.id.notification_text)
    lateinit var notificationText: TextView

    @BindView(R.id.notification_action_text)
    lateinit var notificationActionText: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun display(listItem: MainFeedModel) {
        val notification = listItem as Notification
        notificationTitle.text = notification.title
        notificationActionCount.text = notification.actionCount.toString()
        notificationText.text = notification.text
        notificationActionText.text = notification.actionType
    }
}