package com.jonathanchiou.organizer.scheduler

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.util.closeKeyboard

class AccountsSelectionActivity: AppCompatActivity() {

    @BindView(R.id.account_autocompleteview)
    lateinit var accountAutoCompleteView: AccountAutoCompleteView

    @BindView(R.id.done_fab)
    lateinit var doneFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts_selection)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        accountAutoCompleteView.setAccountsSelectedListener(Consumer {
            if (it) doneFab.show() else doneFab.hide()
        })
    }

    @OnClick(R.id.done_fab)
    fun onDoneFabClicked() {
        setResult(Activity.RESULT_OK,
                  Intent().putParcelableArrayListExtra(SELECTED_ACCOUNTS_KEY,
                                                       accountAutoCompleteView
                                                           .getCurrentlySelectedAccounts()))
        finish()
    }

    override fun onStop() {
        super.onStop()
        closeKeyboard()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val SELECTED_ACCOUNTS_KEY = "selected_accounts"
    }
}