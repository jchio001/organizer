package com.jonathanchiou.foodorganizer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import io.reactivex.Observable
import io.reactivex.functions.Function

class SchedulerActivity : AppCompatActivity() {

    @BindView(R.id.places_autocompletetextview)
    lateinit var placeAutoCompleteTextView: ServerSidedAutoCompleteTextView<Place>

    @BindView(R.id.account_autocompletetextview)
    lateinit var accountAutoCompleteTextView: ServerSidedAutoCompleteTextView<Account>

    @BindView(R.id.account_chipgroup)
    lateinit var accountChipGroup: ActionChipGroup<Account>

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
}