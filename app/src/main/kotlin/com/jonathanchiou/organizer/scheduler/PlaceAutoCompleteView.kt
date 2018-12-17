package com.jonathanchiou.organizer.scheduler

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.ApiUIModel
import com.jonathanchiou.organizer.api.model.Place
import io.reactivex.Observable

class PlaceAutoCompleteView(context: Context,
                            attributeSet: AttributeSet):
    AutoCompleteView<Place>(context, attributeSet) {

    private val organizerClient = ClientManager.get().organizerClient

    override fun onItemSelected(position: Int) {
        val intent = Intent()
        intent.putExtra(PLACE_RESULT,
                        autoCompleteAdapter.getItem(position))
        val activity = context as Activity
        activity.setResult(Activity.RESULT_OK, intent)
        activity.finish()
    }

    override fun queryForResults(query: CharSequence): Observable<ApiUIModel<List<Place>>> {
        return organizerClient.getPlaces(query.toString(), null)
    }

    companion object {
        const val PLACE_RESULT = "place_result"
    }
}