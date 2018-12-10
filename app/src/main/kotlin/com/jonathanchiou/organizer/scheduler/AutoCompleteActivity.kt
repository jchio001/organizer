package com.jonathanchiou.organizer.scheduler

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.internal.DebouncingOnClickListener
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.model.ApiUIModel
import com.jonathanchiou.organizer.api.model.ApiUIModel.State
import com.jonathanchiou.organizer.api.model.toUIModelStream
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Response
import java.util.concurrent.TimeUnit

class AutoCompleteAdapter<T: AutoCompleteModel>(private val recyclerView: RecyclerView):
    RecyclerView.Adapter<AutoCompleteViewHolder>() {

    private var autoCompleteModels = emptyList<T>()

    var itemConsumer: Consumer<T>? = null

    private val onClickListener = object: DebouncingOnClickListener() {
        override fun doClick(v: View) {
            itemConsumer?.accept(autoCompleteModels.get(recyclerView.getChildAdapterPosition(v)))
        }
    }

    fun setResults(results: List<T>) {
        this.autoCompleteModels = results
        notifyDataSetChanged()
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

abstract class AutoCompleteActivity<T> :
    AppCompatActivity() where T : AutoCompleteModel, T : Parcelable {

    @BindView(R.id.query_edittext)
    lateinit var queryTextView: EditText

    @BindView(R.id.autocomplete_recyclerview)
    lateinit var autoCompleteRecyclerView: RecyclerView

    val querySubject = PublishSubject.create<CharSequence>()

    var disposable: Disposable? = null

    lateinit var autoCompleteAdapter: AutoCompleteAdapter<T>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autocomplete)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        autoCompleteAdapter = AutoCompleteAdapter(autoCompleteRecyclerView)
        autoCompleteAdapter.itemConsumer = object: Consumer<T> {
            override fun accept(listItem: T) {
                val intent = Intent()
                intent.putExtra(AUTO_COMPLETE_RESULT_KEY, listItem)
                this@AutoCompleteActivity.setResult(Activity.RESULT_OK, intent)
                finish();
            }
        }

        autoCompleteRecyclerView.adapter = autoCompleteAdapter
        autoCompleteRecyclerView.layoutManager = LinearLayoutManager(this)

        queryTextView.addTextChangedListener(object: TextWatcher {

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
                        .toUIModelStream()
                }
            }
            .subscribe{
                when (it.state) {
                    State.PENDING -> autoCompleteRecyclerView.visibility = View.GONE
                    State.SUCCESS -> {
                        autoCompleteAdapter.setResults(it.model!!)
                        autoCompleteRecyclerView.visibility = View.VISIBLE
                    }
                }
            }
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    abstract fun queryForResults(query: CharSequence): Observable<ApiUIModel<List<T>>>

    companion object {
        const val AUTO_COMPLETE_RESULT_KEY = "autocomplete_result";
    }
}