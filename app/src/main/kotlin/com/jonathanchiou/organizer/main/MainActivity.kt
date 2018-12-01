package com.jonathanchiou.organizer.main

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.core.view.GravityCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.widget.FrameLayout
import butterknife.*
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.ClientManager
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

    @BindDrawable(R.drawable.ic_menu)
    lateinit var upButtonIcon: Drawable

    @BindColor(R.color.white)
    @JvmField
    var white = 0

    protected lateinit var fragmentManager: androidx.fragment.app.FragmentManager

    protected var notificationDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        fragmentManager = supportFragmentManager

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

        ClientManager.get()
            .organizerClient
            .getMainFeed()
            .subscribe(object: Observer<UIModel<List<MainFeedModel>?>> {
                override fun onSubscribe(disposable: Disposable) {
                    notificationDisposable = disposable
                }

                override fun onNext(uiModel: UIModel<List<MainFeedModel>?>) {
                    when (uiModel.state) {
                        State.PENDING ->
                            fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, LoadingFragment())
                                .commit()
                        State.SUCCESS -> {
                            fragmentManager.findFragmentById(R.id.fragment_container)?.let {
                                fragmentManager.beginTransaction()
                                    .remove(it)
                                    .commit()
                            }

                            fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container,
                                         MainFeedFragment(uiModel.model!!))
                                .addToBackStack(MainFeedFragment.BACKSTACK_TAG)
                                .commit()
                        }
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

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            super.onBackPressed()
        }
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