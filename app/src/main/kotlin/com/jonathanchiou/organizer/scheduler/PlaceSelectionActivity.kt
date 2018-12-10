package com.jonathanchiou.organizer.scheduler

import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.ApiUIModel
import com.jonathanchiou.organizer.api.model.Place
import io.reactivex.Observable

class PlaceSelectionActivity(): AutoCompleteActivity<Place>() {

    val organizerClient = ClientManager.get().organizerClient

    override fun queryForResults(query: CharSequence): Observable<ApiUIModel<List<Place>>> {
        return organizerClient.getPlaces(query.toString(), null)
    }
}
