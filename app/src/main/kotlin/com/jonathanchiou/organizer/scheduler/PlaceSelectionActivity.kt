package com.jonathanchiou.organizer.scheduler

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.jonathanchiou.organizer.R
import io.reactivex.functions.BiConsumer

class PlaceSelectionActivity: AppCompatActivity() {

    @BindView(R.id.place_autocompleteview)
    lateinit var placeAutoCompleteView: PlaceAutoCompleteView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_selection)
        ButterKnife.bind(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        placeAutoCompleteView.setOnItemSelectedListener(BiConsumer { place, _ ->
            val intent = Intent()
            intent.putExtra(PLACE_RESULT, place)
            setResult(Activity.RESULT_OK, intent)
            finish()
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val PLACE_RESULT = "place_result"
    }
}
