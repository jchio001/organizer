package com.jonathanchiou.foodorganizer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import butterknife.ButterKnife
import butterknife.OnClick

class SchedulerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduler)
        ButterKnife.bind(this)
    }

    @OnClick(R.id.close_icon)
    fun onCloseIconClicked() {
        finish()
    }
}
