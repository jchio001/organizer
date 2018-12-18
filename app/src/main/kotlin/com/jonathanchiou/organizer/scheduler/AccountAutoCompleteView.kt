package com.jonathanchiou.organizer.scheduler

import android.content.Context
import android.util.AttributeSet
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.Account
import com.jonathanchiou.organizer.api.model.ApiUIModel
import io.reactivex.Observable

class AccountAutoCompleteView(context: Context,
                              attributeSet: AttributeSet):
    AutoCompleteView<Account>(context, attributeSet) {

    val organizerClient = ClientManager.get().organizerClient

    override fun onItemSelected(position: Int) {
    }

    override fun queryForResults(query: CharSequence): Observable<ApiUIModel<List<Account>>> {
        return organizerClient.searchAccounts(73, query.toString())
    }
}