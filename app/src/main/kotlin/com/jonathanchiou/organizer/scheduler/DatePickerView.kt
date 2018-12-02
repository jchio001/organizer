package com.jonathanchiou.organizer.scheduler

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import com.jonathanchiou.organizer.R
import java.text.SimpleDateFormat
import java.util.*

class DatePickerView(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {

    @BindView(R.id.now_switch)
    lateinit var nowSwitch: Switch

    @BindView(R.id.date_layout)
    lateinit var dateLayout: FrameLayout

    @BindView(R.id.date_textview)
    lateinit var dateTextView: TextView

    @BindView(R.id.time_textview)
    lateinit var timeTextView: TextView

    val scheduledTime by lazy {
        val now = Calendar.getInstance()
        now.add(Calendar.MINUTE, 30)
        now
    }

    init {
        inflate(context, R.layout.layout_date_picker, this)
        orientation = VERTICAL
        ButterKnife.bind(this, this)
    }

    @OnCheckedChanged(R.id.now_switch)
    fun onSwitchStateChanges(isChecked: Boolean) {
        if (!isChecked) {
            val scheduledTimeInEpochMs = scheduledTime.time
            dateTextView.text = DATE_FORMAT.format(scheduledTimeInEpochMs)
            timeTextView.text = TIME_FORMAT.format(scheduledTimeInEpochMs)
            dateLayout.visibility = View.VISIBLE
        } else {
            dateLayout.visibility = View.GONE
        }
    }

    @OnClick(R.id.date_textview)
    fun onDateTextViewClicked() {
        DatePickerDialog(context,
                         { _, y, m, d ->
                             scheduledTime.set(Calendar.YEAR, y)
                             scheduledTime.set(Calendar.MONTH, m)
                             scheduledTime.set(Calendar.DAY_OF_MONTH, d)

                             dateTextView.text = DATE_FORMAT.format(scheduledTime.time)
                         },
                         scheduledTime.get(Calendar.YEAR),
                         scheduledTime.get(Calendar.MONTH),
                         scheduledTime.get(Calendar.DAY_OF_MONTH))
            .show()
    }

    @OnClick(R.id.time_textview)
    fun onTimeTextViewClicked() {
        TimePickerDialog(context,
                         { _, h, m ->
                             scheduledTime.set(Calendar.HOUR_OF_DAY, h)
                             scheduledTime.set(Calendar.MINUTE, m)

                             timeTextView.text = TIME_FORMAT.format(scheduledTime.time)
                         },
                         scheduledTime.get(Calendar.HOUR_OF_DAY),
                         scheduledTime.get(Calendar.MINUTE),
                         false)
            .show()
    }


    /**
     * Gets the current date & time combination selected by this view and returns a epoch ms
     * representation of it.
     */
    fun getCurrentlySelectedTime(defaultNow: Boolean = true): Long {
        return (if (!nowSwitch.isChecked) scheduledTime else Calendar.getInstance()).timeInMillis
    }

    fun getUserSelectedTime(): Long {
        return if (!nowSwitch.isChecked) scheduledTime.timeInMillis else NO_TIME_SELECTED
    }

    companion object {
        private val DATE_FORMAT by lazy {
            SimpleDateFormat("EEE MMM, dd, yyyy", Locale.US)
        }

        private val TIME_FORMAT by lazy {
            SimpleDateFormat("hh:mm aa", Locale.US)
        }

        const val NO_TIME_SELECTED = -1L
    }
}