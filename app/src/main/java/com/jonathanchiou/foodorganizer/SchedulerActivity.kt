package com.jonathanchiou.foodorganizer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import butterknife.*
import butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED
import butterknife.OnTextChanged.Callback.BEFORE_TEXT_CHANGED
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class SchedulerActivity : AppCompatActivity() {

    @BindView(R.id.location_textview)
    lateinit var locationTextView: AutoCompleteTextView

    lateinit var autoCompletePlacesAdapter : AutoCompletePlacesAdapter

    protected var previousDisposable : Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)
        ButterKnife.bind(this)

        autoCompletePlacesAdapter = AutoCompletePlacesAdapter()
        locationTextView.setAdapter(autoCompletePlacesAdapter)
        locationTextView.setOnItemClickListener { _, _, position: Int, _ ->
            locationTextView.setText(autoCompletePlacesAdapter.places[position].name)
        }
    }

    @OnClick(R.id.close_icon)
    fun onCloseIconClicked() {
        finish()
    }

    @OnTextChanged(value = [R.id.location_textview],
                   callback = BEFORE_TEXT_CHANGED)
    fun beforeTextChanged() {
        locationTextView.dismissDropDown()
    }

    @OnTextChanged(value = [R.id.location_textview],
                   callback = AFTER_TEXT_CHANGED)
    fun afterTextChanged(editable : Editable) {
        ClientManager.get()
                .foodOrganizerClient
                .getPlaces(editable.toString(),
                           null)
                .subscribe(object: Observer<UIModel<List<Place>>> {
                    override fun onSubscribe(d: Disposable) {
                        previousDisposable?.dispose()
                        previousDisposable = d
                    }

                    override fun onNext(uiModel: UIModel<List<Place>>) {
                        Log.i("SchedulerActivity", uiModel.state.toString())
                        if (uiModel.state == State.SUCCESS) {
                            autoCompletePlacesAdapter.places = uiModel.model!!
                            autoCompletePlacesAdapter.notifyDataSetChanged()
                            locationTextView.showDropDown()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.i("SchedulerActivity", e.message)
                    }

                    override fun onComplete() {
                    }

                })
    }
}
