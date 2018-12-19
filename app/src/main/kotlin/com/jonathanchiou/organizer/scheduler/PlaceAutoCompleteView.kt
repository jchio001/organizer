package com.jonathanchiou.organizer.scheduler

import android.content.Context
import android.util.AttributeSet
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.ApiUIModel
import com.jonathanchiou.organizer.api.model.Place
import io.reactivex.Observable

class PlaceAutoCompleteView(context: Context,
                            attributeSet: AttributeSet):
    AutoCompleteView<Place>(context, attributeSet) {

    private val organizerClient = ClientManager.get().organizerClient

    override fun queryForResults(query: CharSequence): Observable<ApiUIModel<List<Place>>> {
        return organizerClient.getPlaces(query.toString(), null)
    }
}