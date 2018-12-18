package com.jonathanchiou.organizer.scheduler

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.jonathanchiou.organizer.R
import com.jonathanchiou.organizer.api.model.Account

class AccountsSelectionActivity: AppCompatActivity() {

    @BindView(R.id.account_chipgroup)
    lateinit var accountChipGroup: ActionChipGroup<Account>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts_selection)
        ButterKnife.bind(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}