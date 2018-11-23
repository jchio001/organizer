package com.jonathanchiou.foodorganizer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import io.reactivex.Observable
import io.reactivex.functions.Function
import java.text.SimpleDateFormat
import java.util.*

class SchedulerActivity : AppCompatActivity() {

    @BindView(R.id.places_autocompletetextview)
    lateinit var placeAutoCompleteTextView: ServerSidedAutoCompleteTextView<Place>

    @BindView(R.id.account_autocompletetextview)
    lateinit var accountAutoCompleteTextView: ServerSidedAutoCompleteTextView<Account>

    @BindView(R.id.account_chipgroup)
    lateinit var accountChipGroup: ActionChipGroup<Account>

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)
        ButterKnife.bind(this)

        val foodOrganizerClient = ClientManager.get().foodOrganizerClient

        // IGNORE ANDROID STUDIOS. Replacing an interface with a lambda only works if the accepting
        // code is written in Java. It is not.
        placeAutoCompleteTextView.uiModelObservableSupplier =
                object : Function<String, Observable<UIModel<List<Place>>>> {
                    override fun apply(query: String): Observable<UIModel<List<Place>>> {
                        return foodOrganizerClient.getPlaces(query, null)
                    }
                }

        accountAutoCompleteTextView.uiModelObservableSupplier =
                object : Function<String, Observable<UIModel<List<Account>>>> {
                    override fun apply(query: String): Observable<UIModel<List<Account>>> {
                        return foodOrganizerClient.searchAccounts(42, query)
                    }
                }

        accountAutoCompleteTextView.doOnItemClicked {
            accountAutoCompleteTextView.text = null
            accountChipGroup.addChip(it)
        }
    }

    override fun onStop() {
        placeAutoCompleteTextView.cancelPendingRequest()
        accountAutoCompleteTextView.cancelPendingRequest()
        super.onStop()
    }

    @OnClick(R.id.close_icon)
    fun onCloseIconClicked() {
        placeAutoCompleteTextView.cancelPendingRequest()
        accountAutoCompleteTextView.cancelPendingRequest()
        finish()
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
        DatePickerDialog(this,
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
        TimePickerDialog(this,
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

    companion object {
        val DATE_FORMAT by lazy {
            SimpleDateFormat("EEE, MMM, dd, yyyy", Locale.US)
        }

        val TIME_FORMAT by lazy {
            SimpleDateFormat("hh:mm aa", Locale.US)
        }
    }
}