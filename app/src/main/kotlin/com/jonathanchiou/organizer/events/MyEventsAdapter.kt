package com.jonathanchiou.organizer.events

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyEventsAdapter: RecyclerView.Adapter<MyEventsViewHolder>() {

    val myEventModels = ArrayList<MyEventModel>(10)

    var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemCount(): Int {
        return myEventModels.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEventsViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: MyEventsViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}