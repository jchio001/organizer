package com.jonathanchiou.foodorganizer

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.widget.AutoCompleteTextView
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function

class ServerSidedAutoCompleteTextView<T>(context: Context, attributeSet: AttributeSet):
        AutoCompleteTextView(context, attributeSet) {

    val autoCompleteAdapter = AutoCompleteAdapter<T>()

    protected var previousDisposable : Disposable? = null

    lateinit var uiModelObservableSupplier : Function<String, Observable<UIModel<List<T>>>>

    init {
        setAdapter(autoCompleteAdapter)

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
                val matchesInAdapter = autoCompleteAdapter.objects.filter {
                    it.toString() == editableAsString
                }

                if (!matchesInAdapter.isEmpty()) {
                    return
                }

                autoCompleteAdapter.reset()

                previousDisposable?.dispose()
                uiModelObservableSupplier.apply(editableAsString)
                        .subscribe(object: Observer<UIModel<List<T>>> {
                            override fun onSubscribe(d: Disposable) {
                                previousDisposable?.dispose()
                                previousDisposable = d
                            }

                            override fun onNext(uiModel: UIModel<List<T>>) {
                                Log.i("PlacesAutoComplete", uiModel.state.toString())
                                if (uiModel.state == State.SUCCESS) {
                                    autoCompleteAdapter.objects = uiModel.model!!
                                    autoCompleteAdapter.notifyDataSetChanged()
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

    inline fun doOnItemClicked(crossinline consumer: (T) -> Unit) {
        setOnItemClickListener { _, _, position, _ ->
            consumer(autoCompleteAdapter.getItem(position))
        }
    }

    fun cancelPendingRequest() {
        previousDisposable?.dispose()
    }
}