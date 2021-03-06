package com.jonathanchiou.organizer.scheduler

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.room.Room
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.Account
import com.jonathanchiou.organizer.api.model.ApiUIModel
import com.jonathanchiou.organizer.api.model.Place
import com.jonathanchiou.organizer.api.model.toApiUIModelStream
import com.jonathanchiou.organizer.persistence.DbUIModel
import com.jonathanchiou.organizer.persistence.EventDraft
import com.jonathanchiou.organizer.persistence.OrganizerDatabase
import com.jonathanchiou.organizer.persistence.toDbUIModelStream
import com.jonathanchiou.organizer.scheduler.AccountsSelectionActivity.Companion.SELECTED_ACCOUNTS_KEY
import com.jonathanchiou.organizer.scheduler.DatePickerView.Companion.NO_TIME_SELECTED
import com.jonathanchiou.organizer.scheduler.PlaceSelectionActivity.Companion.PLACE_RESULT
import com.squareup.moshi.Types
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

// TODO: I'm 99% sure this entire layout bricks when the layout is stopped and then restarted. Deal
// TODO: with this eventually.
class SchedulerActivity: AppCompatActivity() {

    @BindView(R.id.title_edittext)
    lateinit var titleEditText: EditText

    @BindView(R.id.datepickerview)
    lateinit var datePickerView: DatePickerView

    @BindView(R.id.places_textview)
    lateinit var placeTextView: TextView

    @BindView(R.id.account_textview)
    lateinit var accountTextView: TextView

    @BindView(R.id.account_chipgroup)
    lateinit var accountChipGroup: ActionChipGroup<Account>

    @BindView(R.id.description_textview)
    lateinit var descriptionTextView: TextView

    val progressDialog by lazy {
        val progressDialog = ProgressDialog(this)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.sharing))
        progressDialog
    }

    val clientManager = ClientManager.get()

    val foodOrganizerClient = clientManager.organizerClient

    val eventDraftDao by lazy {
        Room.databaseBuilder(applicationContext,
                             OrganizerDatabase::class.java,
                             "organizer_db")
            .build()
            .getEventDraftDao()
    }

    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    var eventDraftFromDb: EventDraft? = null

    // If we're arriving to this activity from DraftsActivity, we'll receive an index in our
    // intent. This is so that we can update the corresponding eventDraftFromDb in DraftsActivity
    // without re-querying SQLite.
    var draftIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)
        ButterKnife.bind(this)

        accountChipGroup.onItemsSelectedListener = Consumer {
            accountTextView.setHint(if (it) R.string.invite_more_people
                                    else R.string.invite_people)
        }

        maybeLoadDraftFromIntent()
    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                data?.getParcelableExtra<Place>(PLACE_RESULT)?.let {
                    placeTextView.text = it.name
                    placeTextView.tag = it
                }
            } else if (requestCode == ACCOUNTS_AUTOCOMPLETE_REQUEST_CODE) {
                data?.getParcelableArrayListExtra<Account>(SELECTED_ACCOUNTS_KEY)?.let(accountChipGroup::setChips)
            }
        }
    }

    @OnClick(R.id.close_icon)
    fun onCloseIconClicked() {
        compositeDisposable.clear()
        finish()
    }

    @OnClick(R.id.save_button)
    fun onSaveButtonClicked() {
        val listMyData = Types.newParameterizedType(List::class.java, Account::class.java)
        val adapter = ClientManager.get().moshi.adapter<List<Account>>(listMyData)

        val currentPlace = placeTextView.tag as Place?

        val eventDraft = EventDraft(id = eventDraftFromDb?.id ?: 0,
                                    title = titleEditText.text.toString(),
                                    placeId = currentPlace?.placeId,
                                    placeName = currentPlace?.name,
                                    scheduledTime = datePickerView.getUserSelectedCurrentTime(),
                                    invitedAccounts = adapter.toJson(accountChipGroup.getModels()),
                                    description = descriptionTextView.text.toString())

        compositeDisposable.add(
            Observable
                .fromCallable { eventDraftDao.upsert(eventDraft) }
                .toDbUIModelStream()
                .subscribe {
                    if (it.state == DbUIModel.State.SUCCESS) {
                        val intent = Intent()
                        intent.putExtra(DRAFT_INDEX_KEY, draftIndex)
                        intent.putExtra(EVENT_DRAFT_KEY, eventDraft)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                })
    }

    @OnClick(R.id.share_button)
    fun onShareButtonClicked() {
        val enteredTitle = titleEditText.text
        val title = if (!enteredTitle.isEmpty()) enteredTitle.toString() else "(No title)"

        val scheduledTime = datePickerView.getCurrentlySelectedTime()
        scheduledTime?.let {
            if (System.currentTimeMillis() > it) {
                Toast.makeText(this,
                               "EventBlurb must be scheduled at a future time!",
                               Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }

        val placeId = (placeTextView.tag as Place?)?.placeId
        if (placeId == null) {
            Toast.makeText(this,
                           "Please select a valid place.",
                           Toast.LENGTH_SHORT)
                .show()
            return
        }

        val invitedAccounts = accountChipGroup.getModels()
        if (invitedAccounts.isEmpty()) {
            Toast.makeText(this,
                           "You must invite at least 1 account.",
                           Toast.LENGTH_SHORT)
                .show()
            return
        }

        var createEventObservable = foodOrganizerClient
            .createEvent(42,
                         ClientEvent(title = title,
                                     scheduledTime = scheduledTime,
                                     invitedAccounts = invitedAccounts,
                                     placeId = placeId))

        // If the event has a local EventDraft db object associated with it, delete the object when
        // we push the event to the backend.
        eventDraftFromDb?.let {
            val deleteDraftObservable = Observable
                .fromCallable { eventDraftDao.deleteDrafts(arrayOf(it)) }

            createEventObservable = Observable
                .zip(createEventObservable,
                     deleteDraftObservable,
                     BiFunction { createEvent, deleteDraft -> createEvent })
        }

        compositeDisposable.add(
            createEventObservable
                .toApiUIModelStream()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.state == ApiUIModel.State.PENDING) {
                            progressDialog.show()
                        } else if (it.state == ApiUIModel.State.SUCCESS) {
                            progressDialog.dismiss()
                            Toast.makeText(this@SchedulerActivity,
                                           "EventBlurb scheduled!",
                                           Toast.LENGTH_SHORT)
                                .show()

                            setResult(RESULT_OK, Intent()
                                .putExtra(DRAFT_INDEX_KEY, draftIndex)
                                .putExtra(EVENT_DRAFT_KEY, null as Parcelable?))
                            finish()
                        } else {
                            compositeDisposable.add(
                                Completable
                                    .fromAction { eventDraftFromDb?.let(eventDraftDao::upsert) }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                        {
                                            progressDialog.dismiss()
                                            Toast.makeText(this@SchedulerActivity,
                                                           "Failed to schedule event.",
                                                           Toast.LENGTH_SHORT)
                                                .show()
                                        },
                                        { Log.e("SchedulerActivity", it.message) }
                                    )
                            )
                        }
                    },
                    { Log.e("SchedulerActivitty", it.message)}))
    }

    @OnClick(R.id.places_textview)
    fun onPlacesTextViewClicked() {
        startActivityForResult(Intent(this, PlaceSelectionActivity::class.java),
                               PLACE_AUTOCOMPLETE_REQUEST_CODE)
    }

    @OnClick(R.id.account_textview)
    fun onAccountTextViewClicked() {
        startActivityForResult(Intent(this, AccountsSelectionActivity::class.java)
                                   .putParcelableArrayListExtra(SELECTED_ACCOUNTS_KEY,
                                                                accountChipGroup.getModels()),
                               ACCOUNTS_AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun maybeLoadDraftFromIntent() {
        intent?.let {
            draftIndex = it.getIntExtra(DRAFT_INDEX_KEY, -1)
            it.getParcelableExtra<EventDraft>(EVENT_DRAFT_KEY)?.let {
                eventDraftFromDb = it
                titleEditText.setText(it.title)

                if (it.placeId != null && it.placeName != null) {
                    val place = Place(it.placeId, it.placeName)
                    placeTextView.text = place.name
                    placeTextView.tag = place
                }

                datePickerView.setSelectedTime(it.scheduledTime)

                descriptionTextView.text = it.description
                it.invitedAccounts?.let {
                    val listMyData = Types.newParameterizedType(List::class.java,
                                                                Account::class.java)
                    val adapter = clientManager.moshi.adapter<List<Account>>(listMyData)
                    accountChipGroup.setChips(ArrayList(adapter.fromJson(it)!!))
                }
            }
        }
    }

    companion object {
        const val DRAFT_INDEX_KEY = "draft_index"
        const val EVENT_DRAFT_KEY = "event_draft"
        const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 1337
        const val ACCOUNTS_AUTOCOMPLETE_REQUEST_CODE = 1338
    }
}