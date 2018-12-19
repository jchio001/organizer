package com.jonathanchiou.organizer.scheduler

import android.content.Context
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.internal.DebouncingOnClickListener
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.model.ApiUIModel
import com.jonathanchiou.organizer.api.model.Place
import com.jonathanchiou.organizer.api.model.toApiUIModelStream
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Response
import java.util.concurrent.TimeUnit

class AutoCompleteAdapter<T: AutoCompleteModel>(private val recyclerView: RecyclerView):
    RecyclerView.Adapter<AutoCompleteViewHolder>() {

    private var autoCompleteModels = emptyList<T>()

    var itemConsumer: BiConsumer<T, Int>? = null

    private val onClickListener = object: DebouncingOnClickListener() {
        override fun doClick(v: View) {
            val position = recyclerView.getChildAdapterPosition(v)
            itemConsumer?.accept(autoCompleteModels[position], position)
        }
    }

    fun setResults(results: List<T>) {
        this.autoCompleteModels = results
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T {
        return autoCompleteModels.get(position)
    }

    override fun getItemCount(): Int {
        return autoCompleteModels.size
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AutoCompleteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_autocomplete,
                     parent,
                     false)
        view.setOnClickListener(onClickListener)
        return AutoCompleteViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: AutoCompleteViewHolder,
                                  position: Int) {
        viewHolder.display(autoCompleteModels[position])
    }
}

abstract class AutoCompleteView<T>(context: Context,
                                   attributeSet: AttributeSet):
    LinearLayout(context, attributeSet) where T : AutoCompleteModel, T : Parcelable {

    @BindView(R.id.query_edittext)
    lateinit var queryEditText: EditText

    @BindView(R.id.autocomplete_recyclerview)
    lateinit var autoCompleteRecyclerView: RecyclerView

    val querySubject = PublishSubject.create<CharSequence>()

    var disposable: Disposable? = null

    var autoCompleteAdapter: AutoCompleteAdapter<T>

    init {
        val resources = context.resources
        val attributes = resources.obtainAttributes(attributeSet, R.styleable.AutoCompleteView)
        try {
            val layoutResource = attributes.getResourceId(R.styleable.AutoCompleteView_layout,
                                                          R.layout.view_auto_complete)
            val hintStringId = attributes.getResourceId(R.styleable.AutoCompleteView_hint, -1)

            inflate(context, layoutResource, this)
            orientation = VERTICAL
            ButterKnife.bind(this)

            queryEditText.setHint(hintStringId)
        } finally {
            attributes.recycle()
        }

        autoCompleteAdapter = AutoCompleteAdapter(autoCompleteRecyclerView)

        autoCompleteRecyclerView.adapter = autoCompleteAdapter
        autoCompleteRecyclerView.layoutManager = LinearLayoutManager(context)

        queryEditText.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence,
                                       start: Int,
                                       before: Int,
                                       count: Int) {
                querySubject.onNext(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        disposable = querySubject
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .switchMap {
                if (!it.isEmpty()) {
                    return@switchMap queryForResults(it)
                } else {
                    return@switchMap Observable.just(Response.success(emptyList<T>()))
                        .toApiUIModelStream()
                }
            }
            .subscribe{
                when (it.state) {
                    ApiUIModel.State.PENDING -> autoCompleteRecyclerView.visibility = View.GONE
                    ApiUIModel.State.SUCCESS -> {
                        autoCompleteAdapter.setResults(it.model!!)
                        autoCompleteRecyclerView.visibility = View.VISIBLE
                    }
                }
            }
    }

    fun setOnItemSelectedListener(listener: BiConsumer<T, Int>) {
        autoCompleteAdapter.itemConsumer = listener
    }

    fun stopAutoCompleting() {
        disposable?.dispose()
    }

    abstract fun queryForResults(query: CharSequence): Observable<ApiUIModel<List<T>>>
}