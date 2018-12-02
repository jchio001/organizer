package com.jonathanchiou.organizer.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.scheduler.SchedulerActivity

class MainFeedFragment(): Fragment() {

    @BindView(R.id.main_recyclerview)
    lateinit var mainRecyclerView: RecyclerView

    var mainFeedModels: List<MainFeedModel>? = null

    constructor(mainFeedModels: List<MainFeedModel>): this() {
        this.mainFeedModels = mainFeedModels
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        val mainFeedAdapter = MainFeedAdapter()
        mainRecyclerView.adapter = mainFeedAdapter
        mainRecyclerView.layoutManager = LinearLayoutManager(context)
        mainFeedModels?.let(mainFeedAdapter::addMainFeedModels)
    }

    @OnClick(R.id.scheduler_fab)
    fun onSchedulerFabClicked() {
        startActivity(Intent(context, SchedulerActivity::class.java))
    }

    companion object {
        const val BACKSTACK_TAG = "main_feed"
    }
}