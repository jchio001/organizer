package com.jonathanchiou.organizer.drafts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
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
import com.jonathanchiou.organizer.scheduler.SchedulerActivity.Companion.DRAFT_INDEX_KEY
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class DraftsActivity : AppCompatActivity() {

    @BindView(R.id.draft_recyclerview)
    lateinit var draftRecyclerView: RecyclerView

    private lateinit var eventDraftDao: EventDraftDao

    protected var getAllDisposable: Disposable? = null

    lateinit var eventDraftsAdapter: EventDraftsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft)
        ButterKnife.bind(this)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        eventDraftsAdapter = EventDraftsAdapter(draftRecyclerView)
        draftRecyclerView.adapter = eventDraftsAdapter
        draftRecyclerView.layoutManager = LinearLayoutManager(this)
        draftRecyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        eventDraftsAdapter.itemConsumer = object: Consumer<Int> {
            override fun accept(position: Int) {
                val intent = Intent(this@DraftsActivity,
                                    SchedulerActivity::class.java)
                val eventDraft = eventDraftsAdapter.getItem(position)

                intent.putExtra(SchedulerActivity.DRAFT_INDEX_KEY, position)
                intent.putExtra(SchedulerActivity.EVENT_DRAFT_KEY, eventDraft)
                startActivityForResult(intent, SCHEDULER_ACTIVITY_REQUEST_CODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCHEDULER_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    eventDraftsAdapter.updateItem(
                        it.getIntExtra(SchedulerActivity.DRAFT_INDEX_KEY, -1),
                        it.getParcelableExtra(SchedulerActivity.EVENT_DRAFT_KEY))
                    Toast.makeText(this, "Updated draft!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            getAllDisposable?.dispose()
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val SCHEDULER_ACTIVITY_REQUEST_CODE = 73
    }
}
