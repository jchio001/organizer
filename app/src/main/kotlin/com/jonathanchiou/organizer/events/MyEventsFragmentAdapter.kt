package com.jonathanchiou.organizer.events

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.jonathanchiou.organizer.R

class MyEventsFragmentAdapter(fragmentManager: FragmentManager,
                              private val context: Context):
    FragmentPagerAdapter(fragmentManager) {

    private val titles = intArrayOf(R.string.my_events, R.string.invited)

    override fun getItem(position: Int): Fragment {
        return MyEventsBaseFragment()
    }

    override fun getCount(): Int {
        return titles.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.getString(titles[position])
    }
}