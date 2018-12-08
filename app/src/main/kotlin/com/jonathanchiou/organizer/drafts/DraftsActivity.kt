package com.jonathanchiou.organizer.drafts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import butterknife.BindView
import butterknife.ButterKnife
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.persistence.EventDraft
import com.jonathanchiou.organizer.persistence.EventDraftDao
import com.jonathanchiou.organizer.persistence.OrganizerDatabase
import com.jonathanchiou.organizer.scheduler.SchedulerActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class DraftsActivity : AppCompatActivity() {

    @BindView(R.id.draft_recyclerview)
    lateinit var draftRecyclerView: RecyclerView

    private lateinit var eventDraftDao: EventDraftDao

    protected var getAllDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft)
        ButterKnife.bind(this)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val eventDraftsAdapter = EventDraftsAdapter(draftRecyclerView)
        draftRecyclerView.adapter = eventDraftsAdapter
        draftRecyclerView.layoutManager = LinearLayoutManager(this)
        draftRecyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        eventDraftsAdapter.itemConsumer = object: Consumer<EventDraft> {
            override fun accept(eventDraft: EventDraft) {
                val intent = Intent(this@DraftsActivity,
                                    SchedulerActivity::class.java)
                intent.putExtra(SchedulerActivity.EVENT_DRAFT_KEY, eventDraft)
                startActivity(intent)
            }
        }

        eventDraftDao = Room.databaseBuilder(applicationContext,
                                             OrganizerDatabase::class.java,
                                             "organizer_db")
            .build()
            .getEventDraftDao()

        getAllDisposable = eventDraftDao.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { eventDraftsAdapter.addAll(it) },
                { Log.d("DraftsActivity", it.message) })
    }

    override fun onStop() {
        super.onStop()
        getAllDisposable?.dispose()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            getAllDisposable?.dispose()
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}
