package com.jonathanchiou.organizer.main

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import butterknife.*
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.EventBlurb
import com.jonathanchiou.organizer.api.model.Notification
import com.jonathanchiou.organizer.api.model.State
import com.jonathanchiou.organizer.api.model.UIModel
import com.jonathanchiou.organizer.scheduler.SchedulerActivity
import com.jonathanchiou.organizer.settings.SettingsActivity
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

class MainActivity : AppCompatActivity() {

    @BindView(R.id.drawer_layout)
    lateinit var debouncedDrawerLayout: DebouncedDrawerLayout

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.main_progress_bar)
    lateinit var mainProgressBar: ProgressBar

    @BindView(R.id.main_recyclerview)
    lateinit var mainRecyclerView: RecyclerView

    @BindView(R.id.scheduler_fab)
    lateinit var schedulerFab: FloatingActionButton

    @BindDrawable(R.drawable.ic_menu)
    lateinit var upButtonIcon: Drawable

    @BindColor(R.color.white)
    @JvmField
    var white = 0

    protected var notificationDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        // Have to dynamically change the icon's color since I'm dynamically changing the icon.
        upButtonIcon.setColorFilter(white, PorterDuff.Mode.SRC_ATOP)
        actionBar.setHomeAsUpIndicator(upButtonIcon)

        debouncedDrawerLayout.itemSelectedConsumer = Consumer { selectedItemId ->
            when (selectedItemId) {
                R.id.settings -> startActivity(Intent(this@MainActivity,
                                                      SettingsActivity::class.java))
            }
        }

        mainRecyclerView.adapter = MainFeedAdapter()
        mainRecyclerView.layoutManager = LinearLayoutManager(this)

        ClientManager.get()
            .organizerClient
            .getMainFeed()
            .subscribe(object: Observer<UIModel<Pair<Notification?, List<EventBlurb>?>>> {
                override fun onSubscribe(disposable: Disposable) {
                    notificationDisposable = disposable
                }

                override fun onNext(uiModel: UIModel<Pair<Notification?, List<EventBlurb>?>>) {
                    if (uiModel.state == State.SUCCESS) {
                        mainProgressBar.visibility = View.GONE
                        mainRecyclerView.visibility = View.VISIBLE
                        schedulerFab.hide()

                        val mainFeedViewModels = ArrayList<MainFeedModel>(3)
                        mainFeedViewModels.add(uiModel.model!!.first!!)
                        mainFeedViewModels.addAll(uiModel.model!!.second!!)

                        val mainFeedAdapter = mainRecyclerView.adapter as MainFeedAdapter
                        mainFeedAdapter.addMainFeedModels(mainFeedViewModels)
                        mainFeedAdapter.notifyDataSetChanged()
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
    }

    override fun onStop() {
        super.onStop()
        notificationDisposable?.dispose()
    }

    @OnClick(R.id.scheduler_fab)
    fun onSchedulerFabClicked() {
        startActivity(Intent(this, SchedulerActivity::class.java))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                debouncedDrawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}