package com.jonathanchiou.foodorganizer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife

class ViewHolder(view: View) {

    @BindView(R.id.textview)
    lateinit var textView : TextView

    init {
        ButterKnife.bind(this, view)
    }
}

class AutoCompletePlacesAdapter : Filterable, BaseAdapter() {

    var places = EMPTY_LIST

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        lateinit var viewHolder: ViewHolder
        lateinit var view: View

        if (convertView == null) {
            view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_text,
                             parent,
                             false)
            viewHolder = ViewHolder(view)
            view.setTag(viewHolder)
        } else {
            view = convertView
            viewHolder = view.getTag() as ViewHolder
        }

        viewHolder.textView.setText(places.get(position).name)

        return view
    }

    override fun getItem(position: Int): Any {
        return places[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return places.size
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                filterResults.values = places
                filterResults.count = places.size

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?,
                                        results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }

    fun reset() {
        places = EMPTY_LIST
    }

    companion object {
        @JvmStatic
        val EMPTY_LIST = emptyList<Place>()
    }
}