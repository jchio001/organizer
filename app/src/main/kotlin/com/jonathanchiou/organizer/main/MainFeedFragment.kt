package com.jonathanchiou.organizer.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.scheduler.SchedulerActivity

class MainFeedFragment : Fragment {

    @BindView(R.id.main_recyclerview)
    lateinit var mainRecyclerView: RecyclerView

    var mainFeedAdapter: MainFeedAdapter

    constructor() {
        mainFeedAdapter = MainFeedAdapter()
    }

    constructor(mainFeedModels: List<MainFeedModel>) {
        mainFeedAdapter = MainFeedAdapter(mainFeedModels = mainFeedModels)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        mainRecyclerView.adapter = mainFeedAdapter
        mainRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCHEDULER_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(context,
                               "Draft saved!",
                               Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @OnClick(R.id.scheduler_fab)
    fun onSchedulerFabClicked() {
        startActivityForResult(Intent(context, SchedulerActivity::class.java),
                               SCHEDULER_ACTIVITY_REQUEST_CODE)
    }

    companion object {
        const val BACKSTACK_TAG = "main_feed"
        const val SCHEDULER_ACTIVITY_REQUEST_CODE = 1337
    }
}