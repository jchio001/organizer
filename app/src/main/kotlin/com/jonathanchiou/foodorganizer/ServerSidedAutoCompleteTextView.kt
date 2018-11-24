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
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

sealed class TextEvent
class TextInputEvent(val query: String) : TextEvent()
class OtherEvent : TextEvent()

class ServerSidedAutoCompleteTextView<T>(context: Context, attributeSet: AttributeSet) :
        AutoCompleteTextView(context, attributeSet) {

    val autoCompleteAdapter = AutoCompleteAdapter<T>()

    protected var diposable: Disposable? = null

    lateinit var uiModelObservableSupplier: Function<String, Observable<UIModel<List<T>>>>

    val textEventSubject = PublishSubject.create<TextEvent>()

    var clickedItemConsumer: Consumer<T>? = null

    init {
        setAdapter(autoCompleteAdapter)

        setOnFocusChangeListener{ v, hasFocus ->
            if (!hasFocus) {
                dismissDropDown()
            }
        }

        setOnItemClickListener { _, _, position, _ ->
            textEventSubject.onNext(OtherEvent())
            clickedItemConsumer?.accept(autoCompleteAdapter.getItem(position))
        }

        addTextChangedListener(object : TextWatcher {
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

                textEventSubject.onNext(TextInputEvent(query = editableAsString))
            }
        })

        textEventSubject
                .debounce(200, TimeUnit.MILLISECONDS)
                .switchMap {
                    when (it) {
                        is TextInputEvent -> uiModelObservableSupplier.apply(it.query)
                        is OtherEvent -> Observable.never<UIModel<List<T>>>()
                    }
                }
                .subscribe(object : Observer<UIModel<List<T>>> {
                    override fun onSubscribe(d: Disposable) {
                        diposable = d
                    }

                    override fun onNext(uiModel: UIModel<List<T>>) {
                        Log.i("AutoComplete", uiModel.state.toString())
                        if (uiModel.state == State.SUCCESS) {
                            autoCompleteAdapter.objects = uiModel.model!!
                            autoCompleteAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.i("AutoComplete", e.message)
                    }

                    override fun onComplete() {
                    }
                })
    }

    fun getCurrentlySelectedItem(): T? {
        textEventSubject.onNext(OtherEvent())
        return autoCompleteAdapter.objects.firstOrNull { it.toString() == text.toString() }
    }

    fun cancelPendingRequest() {
        diposable?.dispose()
    }
}