package com.jonathanchiou.organizer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import com.jonathanchiou.organizer.persistence.EventDraft
import com.jonathanchiou.organizer.persistence.EventDraftDao
import com.jonathanchiou.organizer.persistence.OrganizerDatabase
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DraftsActivity : AppCompatActivity() {

    private lateinit var eventDraftDao: EventDraftDao

    protected var getAllDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft)

        eventDraftDao = Room.databaseBuilder(applicationContext,
                                             OrganizerDatabase::class.java,
                                             "organizer_db")
            .build()
            .getEventDraftDao()

        eventDraftDao.getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: Observer<List<EventDraft>> {
                override fun onSubscribe(disposable: Disposable) {
                    getAllDisposable = disposable
                }

                override fun onNext(t: List<EventDraft>) {
                    Toast.makeText(this@DraftsActivity,
                                   "Event drafts fetched from SQLlite!",
                                   Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onError(e: Throwable) {
                    Log.d("DraftsActivity", e.message)
                }

                override fun onComplete() {
                    Log.d("DraftsActivity", "OnComplete!")
                }
            })
    }

    override fun onStop() {
        super.onStop()
        getAllDisposable?.dispose()
    }
}
