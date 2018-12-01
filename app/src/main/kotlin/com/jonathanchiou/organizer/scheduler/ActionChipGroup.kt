package com.jonathanchiou.organizer.scheduler

import android.content.Context
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.jonathanchiou.organizer.R

class CloseableChip(context: Context) : Chip(context,
                                                                              null,
                                                                              R.style.Widget_MaterialComponents_Chip_Action) {

    init {
        isCheckedIconVisible = false
        isCloseIconVisible = true

        setOnCloseIconClickListener {
            val parent = parent as ViewGroup
            parent.removeView(this)

            if (parent.childCount == 0) {
                parent.visibility = View.GONE
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int,
                           heightMeasureSpec: Int) {
        super.onMeasure(WRAP_CONTENT, WRAP_CONTENT)
    }
}

class ActionChipGroup<T>(context: Context, attributeSet: AttributeSet) :
    ChipGroup(context, attributeSet) {

    val textColor: Int
    val textSize: Int

    init {
        val resources = context.resources
        val attributes = resources.obtainAttributes(attributeSet, R.styleable.ActionChipGroup)
        try {
            textColor = attributes.getColor(
                R.styleable.ActionChipGroup_android_textColor,
                ContextCompat.getColor(context, R.color.text_color))
            textSize = attributes.getDimensionPixelSize(
                R.styleable.ActionChipGroup_android_textSize,
                resources.getDimensionPixelSize(R.dimen.default_chip_text_size))
        } finally {
            attributes.recycle()
        }

        visibility = View.GONE
    }

    fun addChip(chipModel: T) {
        for (i in 0 until childCount) {
            if (getChildAt(i).tag == chipModel) {
                return
            }
        }

        val closeableChip = CloseableChip(context)
        closeableChip.tag = chipModel
        closeableChip.text = chipModel.toString()
        closeableChip.setTextColor(textColor)
        closeableChip.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        addView(closeableChip)

        visibility = View.VISIBLE
    }

    fun getModels(): List<T> {
        return (0 until childCount).asIterable()
            .map { getChildAt(it).tag as T }
    }
}