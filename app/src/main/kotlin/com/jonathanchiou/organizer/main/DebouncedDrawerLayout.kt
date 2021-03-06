package com.jonathanchiou.organizer.main

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import io.reactivex.functions.Consumer

class DebouncedDrawerLayout(context: Context,
                            attributeSet: AttributeSet):
    DrawerLayout(context, attributeSet) {

    protected lateinit var navigationView: NavigationView

    private var navigationItemSelectedEnabled = true

    protected var selectedItemId = NOTHING_SELECTED_ID

    var itemSelectedConsumer: Consumer<Int>? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        val maybeNavigationView: View? = getChildAt(1)
        if (maybeNavigationView == null || maybeNavigationView !is NavigationView) {
            throw IllegalStateException("Missing navigation view.")
        }

        maybeNavigationView.setNavigationItemSelectedListener {
            if (navigationItemSelectedEnabled) {
                navigationItemSelectedEnabled = false
                it.isChecked = true
                selectedItemId = it.itemId
                navigationItemSelectedEnabled = true
            }

            closeDrawer(GravityCompat.START)
            true
        }

        navigationView = maybeNavigationView

        addDrawerListener(object: DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                itemSelectedConsumer?.accept(selectedItemId)
                if (selectedItemId != NOTHING_SELECTED_ID) {
                    navigationView.menu.findItem(selectedItemId).isChecked = false
                }
                selectedItemId = NOTHING_SELECTED_ID
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    companion object {
        const val NOTHING_SELECTED_ID = -1
    }
}