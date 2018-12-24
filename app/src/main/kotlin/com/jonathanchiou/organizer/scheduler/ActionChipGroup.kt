package com.jonathanchiou.organizer.scheduler

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.jonathanchiou.organizer.R

class CloseableChip<T>(context: Context) : Chip(context,
                                             null,
                                             R.style.Widget_MaterialComponents_Chip_Action) {

    var onItemsSelectedListener: Consumer<Boolean>? = null

    var onItemClosedListener: Consumer<T>? = null

    init {
        isCheckedIconVisible = false
        isCloseIconVisible = true

        setOnCloseIconClickListener {
            val parent = parent as ViewGroup
            parent.removeView(this)

            if (parent.childCount == 0) {
                parent.visibility = View.GONE
                onItemsSelectedListener?.accept(false)
            }

            onItemClosedListener?.accept(tag as T)
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

    // true = this view has chips, false = this view is empty
    var onItemsSelectedListener: Consumer<Boolean>? = null

    var onItemClosedListener: Consumer<T>? = null

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
        val previousChildCount = childCount

        for (i in 0 until previousChildCount) {
            if (getChildAt(i).tag == chipModel) {
                return
            }
        }

        val closeableChip = CloseableChip<T>(context)
        closeableChip.tag = chipModel
        closeableChip.text = chipModel.toString()
        closeableChip.setTextColor(textColor)
        closeableChip.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        closeableChip.onItemsSelectedListener = onItemsSelectedListener
        closeableChip.onItemClosedListener = onItemClosedListener
        addView(closeableChip)

        visibility = View.VISIBLE

        if (previousChildCount == 0) {
            onItemsSelectedListener?.accept(true)
        }
    }

    fun addChips(chipModels: List<T>) {
        for (chipModel in chipModels) {
            addChip(chipModel)
        }
    }

    fun removeChip(chipModel: T) {
        val previousChildCount = childCount

        for (i in 0 until previousChildCount) {
            if (getChildAt(i).tag == chipModel) {
                removeViewAt(i)

                if (previousChildCount == 1) {
                    onItemsSelectedListener?.accept(false)
                }

                return
            }
        }
    }

    fun getModels(): ArrayList<T> {
        val modelsList = ArrayList<T>(childCount)

        for (i in 0 until childCount) {
            modelsList.add(getChildAt(i).getTag() as T)
        }

        return modelsList
    }
}