package com.jonathanchiou.organizer.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.closeKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    var view = currentFocus
    if (view == null) {
        view = View(this)
    }

    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}