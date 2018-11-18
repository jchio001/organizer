package com.jonathanchiou.foodorganizer

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.widget.AutoCompleteTextView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class PlacesAutoCompleteTextView(context: Context, attributeSet: AttributeSet):
        AutoCompleteTextView(context, attributeSet) {

    val clientManager = ClientManager.get()

    val autoCompletePlacesAdapter = AutoCompletePlacesAdapter()

    protected var previousDisposable : Disposable? = null

    init {
        setAdapter(autoCompletePlacesAdapter)

        addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?,
                                           start: Int,
                                           count: Int,
                                           after: Int) {
                dismissDropDown()
            }

            override fun onTextChanged(s: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int) {
                autoCompletePlacesAdapter.reset()
            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable?.isEmpty() == true) {
                    return
                }

                val editableAsString = editable.toString()
                val matchesInAdapter = autoCompletePlacesAdapter.places.filter {
                    it.name ==  editableAsString
                }

                if (!matchesInAdapter.isEmpty()) {
                    return
                }

                previousDisposable?.dispose()
                clientManager.foodOrganizerClient
                        .getPlaces(editableAsString,
                                    null)
                        .subscribe(object: Observer<UIModel<List<Place>>> {
                            override fun onSubscribe(d: Disposable) {
                                previousDisposable?.dispose()
                                previousDisposable = d
                            }

                            override fun onNext(uiModel: UIModel<List<Place>>) {
                                Log.i("PlacesAutoComplete", uiModel.state.toString())
                                if (uiModel.state == State.SUCCESS) {
                                    autoCompletePlacesAdapter.places = uiModel.model!!
                                    autoCompletePlacesAdapter.notifyDataSetChanged()
                                    showDropDown()
                                }
                            }

                            override fun onError(e: Throwable) {
                                Log.i("PlacesAutoComplete", e.message)
                            }

                            override fun onComplete() {
                            }
                        })
            }
        })
    }

    fun cancelPendingRequest() {
        previousDisposable?.dispose()
    }
}