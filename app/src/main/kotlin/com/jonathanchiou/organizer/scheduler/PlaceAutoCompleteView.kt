package com.jonathanchiou.organizer.scheduler

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import butterknife.internal.DebouncingOnClickListener
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.ApiUIModel
import com.jonathanchiou.organizer.api.model.Place
import io.reactivex.Observable
import io.reactivex.functions.BiConsumer

class PlaceViewHolder(itemView: View): ViewHolder(itemView) {

    fun display(place: Place) {
        (itemView as TextView).text = place.name
    }
}

class PlaceAutoCompleteAdapter(val recyclerView: RecyclerView):
    AutoCompleteAdapter<Place, PlaceViewHolder>() {

    var itemConsumer: BiConsumer<Place, Int>? = null

    private val onClickListener = object: DebouncingOnClickListener() {
        override fun doClick(v: View) {
            val position = recyclerView.getChildAdapterPosition(v)
            itemConsumer?.accept(autoCompleteModels[position], position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_place_autocomplete,
                     parent,
                     false)
        view.setOnClickListener(onClickListener)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        (viewHolder as PlaceViewHolder).display(autoCompleteModels[position])
    }
}

class PlaceAutoCompleteView(context: Context,
                            attributeSet: AttributeSet):
    AutoCompleteView<Place, PlaceViewHolder, PlaceAutoCompleteAdapter>(context, attributeSet) {

    private val organizerClient = ClientManager.get().organizerClient

    init {
        autoCompleteAdapter = PlaceAutoCompleteAdapter(autoCompleteRecyclerView)
    }

    override fun queryForResults(query: CharSequence): Observable<ApiUIModel<List<Place>>> {
        return organizerClient.getPlaces(query.toString(), null)
    }

    fun setOnItemSelectedListener(listener: BiConsumer<Place, Int>) {
        autoCompleteAdapter?.let {
            it.itemConsumer = listener
        } ?: throw IllegalStateException("Adapter not initialized!")
    }
}