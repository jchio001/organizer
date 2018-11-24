package com.jonathanchiou.organizer.scheduler

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function

class SchedulerActivity : AppCompatActivity() {

    @BindView(R.id.title_edittext)
    lateinit var titleEditText: EditText

    @BindView(R.id.datepickerview)
    lateinit var datePickerView: DatePickerView

    @BindView(R.id.places_autocompletetextview)
    lateinit var placeAutoCompleteTextView: ServerSidedAutoCompleteTextView<Place>

    @BindView(R.id.account_autocompletetextview)
    lateinit var accountAutoCompleteTextView: ServerSidedAutoCompleteTextView<Account>

    @BindView(R.id.account_chipgroup)
    lateinit var accountChipGroup: ActionChipGroup<Account>

    val progressDialog by lazy {
        val progressDialog = ProgressDialog(this)
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.sharing))
        progressDialog
    }

    val foodOrganizerClient = ClientManager.get().foodOrganizerClient

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)
        ButterKnife.bind(this)

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

        accountAutoCompleteTextView.clickedItemConsumer = object : Consumer<Account> {
            override fun accept(account: Account) {
                accountAutoCompleteTextView.text = null
                accountChipGroup.addChip(account)
            }
        }
    }

    override fun onStop() {
        placeAutoCompleteTextView.cancelPendingRequest()
        accountAutoCompleteTextView.cancelPendingRequest()
        disposable?.dispose()
        super.onStop()
    }

    @OnClick(R.id.close_icon)
    fun onCloseIconClicked() {
        placeAutoCompleteTextView.cancelPendingRequest()
        accountAutoCompleteTextView.cancelPendingRequest()
        disposable?.dispose()
        finish()
    }

    @OnClick(R.id.share_button)
    fun onShareButtonClicked() {
        val enteredTitle = titleEditText.text
        val title = if (!enteredTitle.isEmpty()) enteredTitle.toString() else "(No title)"

        val scheduledTime = datePickerView.getCurrentlySelectedTime()

        if (System.currentTimeMillis() >= scheduledTime) {
            Toast.makeText(this,
                           "Event must be scheduled at a future time!",
                           Toast.LENGTH_SHORT)
                .show()
            return
        }

        val placeId = placeAutoCompleteTextView.getCurrentlySelectedItem()?.placeId
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

        foodOrganizerClient.createEvent(42,
                                        ClientEvent(title = title,
                                                    scheduledTime = scheduledTime,
                                                    invitedAccounts = invitedAccounts,
                                                    placeId = placeId))
            .subscribeWith(object : Observer<UIModel<Event>> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(uiModel: UIModel<Event>) {
                    if (uiModel.state == State.PENDING) {
                        progressDialog.show()
                    } else if (uiModel.state == State.SUCCESS) {
                        progressDialog.dismiss()
                        Toast.makeText(this@SchedulerActivity,
                                       "Event scheduled!",
                                       Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
    }
}