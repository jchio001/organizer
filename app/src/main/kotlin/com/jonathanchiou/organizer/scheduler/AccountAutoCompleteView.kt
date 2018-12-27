package com.jonathanchiou.organizer.scheduler

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.Consumer
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

    fun display(account: Account, isSelected: Boolean?) {
        checkIcon.visibility = if (isSelected == true) View.VISIBLE else View.INVISIBLE
        accountTextView.text = account.toString()
    }
}

class AccountAutoCompleteAdapter(val recyclerView: RecyclerView,
                                 val accountChipGroup: ActionChipGroup<Account>):
    AutoCompleteAdapter<Account, AccountViewHolder>() {

    // NOTE: I'm pretty sure this code isn't completely safe from all the possible combinations of
    // asynchronous UI interactions that require changes to the adapter, but I'm fairly certain my
    // code will work unless someone's actively spamming all the UI interactions ever.
    private val accountToIsSelectedMap = HashMap<Account, Boolean>()

    private val onClickListener = object: DebouncingOnClickListener() {
        override fun doClick(v: View) {
            val position = recyclerView.getChildAdapterPosition(v)
            val account = autoCompleteModels[position]

            val isSelected = accountToIsSelectedMap[account]
            if (isSelected == true) {
                accountToIsSelectedMap[account] = false
                accountChipGroup.removeChip(account)
            } else {
                accountToIsSelectedMap[account] = true
                accountChipGroup.addChip(autoCompleteModels[position])
            }

            notifyItemChanged(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_account_autocomplete,
                     parent,
                     false)
        view.setOnClickListener(onClickListener)
        return AccountViewHolder(view)
    }

    override fun doBindViewHolder(viewHolder: AccountViewHolder, position: Int) {
        val account = autoCompleteModels[position]
        viewHolder.display(account, accountToIsSelectedMap[account])
    }

    fun addSelectedAccounts(accounts: ArrayList<Account>,
                            notifyDataSetChanged: Boolean = false) {
        for (i in 0 until accounts.size) {
            accountToIsSelectedMap[accounts[i]] = true
        }

        if (notifyDataSetChanged) {
            notifyDataSetChanged()
        }
    }

    fun setCheckedState(account: Account, isChecked: Boolean) {
        accountToIsSelectedMap[account] = isChecked
        for (i in 0 until autoCompleteModels.size) {
            if (autoCompleteModels[i] == account) {
                notifyItemChanged(i)
                return
            }
        }
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
        accountChipGroup.onItemClosedListener = Consumer {
            autoCompleteAdapter?.setCheckedState(it, false)
        }
    }

    override fun queryForResults(query: CharSequence): Observable<ApiUIModel<List<Account>>> {
        return organizerClient.searchAccounts(73, query.toString())
    }

    fun setAccountsSelectedListener(onAccountsSelectedListener: Consumer<Boolean>) {
        accountChipGroup.onItemsSelectedListener = onAccountsSelectedListener
    }

    fun getSelectedAccounts(): ArrayList<Account> {
        return accountChipGroup.getModels()
    }

    fun setSelectedAccounts(accounts: ArrayList<Account>) {
        autoCompleteAdapter?.addSelectedAccounts(accounts)
        accountChipGroup.setChips(accounts)
    }
}