package com.jonathanchiou.organizer.scheduler

import android.content.Context
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import butterknife.BindView
import butterknife.ButterKnife
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.model.ApiUIModel
import com.jonathanchiou.organizer.api.model.toApiUIModelStream
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Response
import java.util.concurrent.TimeUnit

abstract class AutoCompleteAdapter<MODEL, VIEWHOLDER>:
    RecyclerView.Adapter<VIEWHOLDER>()
    where MODEL: Parcelable,
          VIEWHOLDER: ViewHolder {

    protected var autoCompleteModels = emptyList<MODEL>()

    private var isVisible = true
    private var isClickable = true

    fun setResults(results: List<MODEL>) {
        this.autoCompleteModels = results
        changeVisibility(true)
    }

    override fun getItemCount(): Int {
        return autoCompleteModels.size
    }

    abstract override fun onCreateViewHolder(parent: ViewGroup,
                                             viewType: Int): VIEWHOLDER

    final override fun onBindViewHolder(viewHolder: VIEWHOLDER,
                                        position: Int) {
        viewHolder.itemView.isClickable = isClickable
        if (isVisible) {
            viewHolder.itemView.visibility = View.VISIBLE
            doBindViewHolder(viewHolder, position)
        } else {
            viewHolder.itemView.visibility = View.INVISIBLE
        }
    }

    abstract fun doBindViewHolder(viewHolder: VIEWHOLDER,
                                  position: Int)

    // This is to deal with some weird issue related to the recyclerview overlapping with a FAB
    // where changing the recyclerview's visibility propagates to the FAB. THANKS BIG DADDY G!
    fun changeVisibility(isVisible: Boolean) {
        this.isVisible = isVisible
        this.isClickable = isVisible
        notifyDataSetChanged()
    }
}

abstract class AutoCompleteView<MODEL, VIEWHOLDER, ADAPTER>(context: Context,
                                                            attributeSet: AttributeSet):
    LinearLayout(context, attributeSet)
    where MODEL: Parcelable,
          VIEWHOLDER: ViewHolder,
          ADAPTER: AutoCompleteAdapter<MODEL, VIEWHOLDER> {

    @BindView(R.id.query_edittext)
    lateinit var queryEditText: EditText

    @BindView(R.id.autocomplete_recyclerview)
    lateinit var autoCompleteRecyclerView: RecyclerView

    val querySubject = PublishSubject.create<CharSequence>()

    var disposable: Disposable? = null

    var autoCompleteAdapter: ADAPTER? = null
        set(value) {
            field = value
            autoCompleteRecyclerView.adapter = value
        }

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

        autoCompleteRecyclerView.layoutManager = LinearLayoutManager(context)

        queryEditText.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                autoCompleteAdapter?.changeVisibility(false)
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
                    return@switchMap Observable.just(Response.success(emptyList<MODEL>()))
                        .toApiUIModelStream()
                }
            }
            .subscribe {
                when (it.state) {
                    ApiUIModel.State.SUCCESS -> {
                        autoCompleteAdapter?.setResults(it.model!!)
                    }
                }
            }
    }

    fun stopAutoCompleting() {
        disposable?.dispose()
    }

    abstract fun queryForResults(query: CharSequence): Observable<ApiUIModel<List<MODEL>>>
}