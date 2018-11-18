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

    @BindView(R.id.places_autocompletetextview)
    lateinit var placesAutoCompleteTextView: PlacesAutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)
        ButterKnife.bind(this)
    }

    override fun onStop() {
        placesAutoCompleteTextView.cancelPendingRequest()
        super.onStop()
    }

    @OnClick(R.id.close_icon)
    fun onCloseIconClicked() {
        placesAutoCompleteTextView.cancelPendingRequest()
        finish()
    }
}