package com.jonathanchiou.organizer.drafts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.persistence.EventDraft
import com.jonathanchiou.organizer.persistence.OrganizerDatabase
import com.jonathanchiou.organizer.scheduler.SchedulerActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class DraftsActivity: AppCompatActivity() {

    @BindView(R.id.draft_activity_parent)
    lateinit var parent: CoordinatorLayout

    @BindView(R.id.draft_recyclerview)
    lateinit var draftRecyclerView: RecyclerView

    protected var compositeDisposable = CompositeDisposable()

    lateinit var eventDraftsAdapter: EventDraftsAdapter

    val eventDraftDao by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Room.databaseBuilder(applicationContext,
                             OrganizerDatabase::class.java,
                             "organizer_db")
            .build()
            .getEventDraftDao()
    }

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


        eventDraftsAdapter.deleteConsumer = Consumer { deletedDrafts ->
            compositeDisposable.add(
                Observable
                    .fromCallable { eventDraftDao.deleteDrafts(deletedDrafts) }
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        { Log.d("DraftsActivity", "Deleted drafts!") },
                        {
                            Log.e("DraftsActivity",
                                  "Failed to delete drafts: ${it.message}")
                        }))

            val snackbar = Snackbar
                .make(parent,
                      "${deletedDrafts.size} deleted.",
                      Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    compositeDisposable.add(
                        Observable.fromCallable { eventDraftDao.upsertMany(deletedDrafts) }
                            .subscribeOn(Schedulers.io())
                            .subscribe(
                                { Log.d("DraftsActivity", "Re-added drafts!") },
                                {
                                    Log.e("DraftsActivity",
                                          "Failed to restore drafts: ${it.message}")
                                }))

                    eventDraftsAdapter.undoDeletion(deletedDrafts)
                }
                .setActionTextColor(ContextCompat.getColor(this@DraftsActivity,
                                                           R.color.white))

            snackbar.view.setBackgroundColor(ContextCompat.getColor(this@DraftsActivity,
                                                                    R.color.colorPrimary))

            snackbar.show()
        }

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

        compositeDisposable.add(
            Observable.fromCallable { eventDraftDao.getAll() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { eventDraftsAdapter.addAll(it) },
                    { Log.e("DraftsActivity", it.message) }))
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
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
            compositeDisposable.dispose()
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val SCHEDULER_ACTIVITY_REQUEST_CODE = 73
    }
}
