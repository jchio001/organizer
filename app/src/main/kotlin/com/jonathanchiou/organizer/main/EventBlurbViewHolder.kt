package com.jonathanchiou.organizer.main

import android.view.View
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.model.EventBlurb
import com.jonathanchiou.organizer.viewholder.AbsViewHolder
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class EventBlurbViewHolder(itemView: View): AbsViewHolder<MainFeedModel>(itemView) {

    @BindView(R.id.event_blurb_cell_title)
    lateinit var eventBlurbCellTitle: TextView

    @BindView(R.id.event_blurb_date_cell)
    lateinit var eventBlurbDateCell: TextView

    @BindView(R.id.event_blurb_creator_profile_image)
    lateinit var eventBlurbCreatorProfileImage: CircleImageView

    @BindView(R.id.event_blurb_creator_name)
    lateinit var eventBlurbCreatorName: TextView

    val picasso = Picasso.get()

    init {
        ButterKnife.bind(this, itemView)
    }

    override fun display(listItem: MainFeedModel) {
        val eventBlurb = listItem as EventBlurb
        eventBlurbCellTitle.text = eventBlurb.title
        eventBlurbDateCell.text = DATE_FORMAT.format(Date(eventBlurb.date))

        picasso
            .load(eventBlurb.creator.profileImage)
            .into(eventBlurbCreatorProfileImage)

        eventBlurbCreatorName.setText(eventBlurb.creator.toString())
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("MMM dd, yyyy hh:mm aa", Locale.US)
    }
}