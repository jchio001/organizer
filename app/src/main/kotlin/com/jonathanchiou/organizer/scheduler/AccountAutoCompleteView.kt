package com.jonathanchiou.organizer.scheduler

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.internal.DebouncingOnClickListener
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.ClientManager
import com.jonathanchiou.organizer.api.model.Account
import com.jonathanchiou.organizer.api.model.ApiUIModel
import io.reactivex.Observable

class AccountViewHolder(itemView: View): ViewHolder(itemView) {

    @BindView(R.id.check_icon)
    lateinit var checkIcon: ImageView

    @BindView(R.id.account_textview)
    lateinit var accountTextView: TextView

    init {
        ButterKnife.bind(this, itemView)
    }

    fun display(account: Account, isSelected: Boolean) {
        checkIcon.visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
        accountTextView.text = account.toString()
    }
}

class AccountAutoCompleteAdapter(val recyclerView: RecyclerView,
                                 val accountChipGroup: ActionChipGroup<Account>):
    AutoCompleteAdapter<Account, AccountViewHolder>() {

    private val onClickListener = object: DebouncingOnClickListener() {
        override fun doClick(v: View) {
            val position = recyclerView.getChildAdapterPosition(v)
            accountChipGroup.addChip(autoCompleteModels[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_account_autocomplete,
                     parent,
                     false)
        view.setOnClickListener(onClickListener)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        (viewHolder as AccountViewHolder).display(autoCompleteModels[position], false)
    }
}

class AccountAutoCompleteView(context: Context,
                              attributeSet: AttributeSet):
    AutoCompleteView<Account, AccountViewHolder, AccountAutoCompleteAdapter>(context,
                                                                             attributeSet) {

    @BindView(R.id.account_chipgroup)
    lateinit var accountChipGroup: ActionChipGroup<Account>

    val organizerClient = ClientManager.get().organizerClient

    init {
        ButterKnife.bind(this, this)
        autoCompleteAdapter = AccountAutoCompleteAdapter(autoCompleteRecyclerView,
                                                         accountChipGroup)
    }

    override fun queryForResults(query: CharSequence): Observable<ApiUIModel<List<Account>>> {
        return organizerClient.searchAccounts(73, query.toString())
    }
}