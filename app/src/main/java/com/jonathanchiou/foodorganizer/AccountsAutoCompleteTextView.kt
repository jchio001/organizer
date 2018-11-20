package com.jonathanchiou.foodorganizer

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.widget.AutoCompleteTextView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

// TODO: This is probably going to break on me. Plz fix.
class AccountsAutoCompleteTextView(context: Context, attributeSet: AttributeSet):
        AutoCompleteTextView(context, attributeSet) {

    val clientManager = ClientManager.get()

    protected var previousDisposable : Disposable? = null

    val autoCompleteAccountAdapter = AutoCompleteAdapter<Account>()

    init {
        setAdapter(autoCompleteAccountAdapter)

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
            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable?.isEmpty() == true) {
                    return
                }

                val editableAsString = editable.toString()
                val matchesInAdapter = autoCompleteAccountAdapter.objects.filter {
                    it.toString() == editableAsString
                }

                if (!matchesInAdapter.isEmpty()) {
                    return
                }

                autoCompleteAccountAdapter.reset()

                previousDisposable?.dispose()
                clientManager.foodOrganizerClient
                        .searchAccounts(1337,
                                        null)
                        .subscribe(object: Observer<UIModel<List<Account>>> {
                            override fun onSubscribe(d: Disposable) {
                                previousDisposable?.dispose()
                                previousDisposable = d
                            }

                            override fun onNext(uiModel: UIModel<List<Account>>) {
                                Log.i("PlacesAutoComplete", uiModel.state.toString())
                                if (uiModel.state == State.SUCCESS) {
                                    autoCompleteAccountAdapter.objects = uiModel.model!!
                                    autoCompleteAccountAdapter.notifyDataSetChanged()
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