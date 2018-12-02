package com.jonathanchiou.organizer.scheduler

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.widget.AutoCompleteTextView
import com.jonathanchiou.organizer.api.model.ApiUIModel
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

    lateinit var apiUiModelObservableSupplier: Function<String, Observable<ApiUIModel<List<T>>>>

    val textEventSubject = PublishSubject.create<TextEvent>()

    var clickedItemConsumer: Consumer<T>? = null

    init {
        setAdapter(autoCompleteAdapter)

        setOnFocusChangeListener { v, hasFocus ->
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
                editable?.let {
                    val editableAsString = it.toString()
                    val matchesInAdapter = autoCompleteAdapter.objects.filter {
                        it.toString() == editableAsString
                    }

                    if (!matchesInAdapter.isEmpty()) {
                        return
                    }

                    textEventSubject.onNext(TextInputEvent(query = editableAsString))
                }
            }
        })

        textEventSubject
            .debounce(200, TimeUnit.MILLISECONDS)
            .switchMap {
                when (it) {
                    is TextInputEvent ->
                        if (!it.query.isEmpty()) apiUiModelObservableSupplier.apply(it.query)
                        else Observable.never<ApiUIModel<List<T>>>()
                    is OtherEvent -> Observable.never<ApiUIModel<List<T>>>()
                }
            }
            .subscribe(object : Observer<ApiUIModel<List<T>>> {
                override fun onSubscribe(d: Disposable) {
                    diposable = d
                }

                override fun onNext(apiUiModel: ApiUIModel<List<T>>) {
                    Log.i("AutoComplete", apiUiModel.state.toString())
                    if (apiUiModel.state == ApiUIModel.State.SUCCESS) {
                        autoCompleteAdapter.objects = apiUiModel.model!!
                        showDropDown()
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