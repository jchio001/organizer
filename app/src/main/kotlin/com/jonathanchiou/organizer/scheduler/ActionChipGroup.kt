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

class CloseableChip<T>(context: Context): Chip(context,
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

class ActionChipGroup<T>(context: Context, attributeSet: AttributeSet):
    ChipGroup(context, attributeSet) {

    val textColor: Int
    val textSize: Int

    // true = this view has chips, false = this view is empty
    var onItemsSelectedListener: Consumer<Boolean>? = null
        set(value) {
            field = value
            value?.accept(childCount != 0)
        }

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
        populate(closeableChip, chipModel)
        addView(closeableChip)

        visibility = View.VISIBLE

        if (previousChildCount == 0) {
            onItemsSelectedListener?.accept(true)
        }
    }

    private fun populate(closeableChip: CloseableChip<T>, chipModel: T) {
        closeableChip.tag = chipModel
        closeableChip.text = chipModel.toString()
        closeableChip.setTextColor(textColor)
        closeableChip.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        closeableChip.onItemsSelectedListener = onItemsSelectedListener
        closeableChip.onItemClosedListener = onItemClosedListener
    }

    fun setChips(chipModels: ArrayList<T>) {
        val previousChildCount = childCount
        val newChildCount = chipModels.size
        val reuseIndex = Math.min(newChildCount, previousChildCount)

        for (i in 0 until reuseIndex) {
            populate(getChildAt(i) as CloseableChip<T>, chipModels[i])
        }

        val context = context
        for (i in previousChildCount until newChildCount) {
            val closeableChip = CloseableChip<T>(context)
            populate(closeableChip, chipModels[i])
            addView(closeableChip)
        }

        for (i in newChildCount until previousChildCount) {
            removeViewAt(newChildCount)
        }

        visibility = if (newChildCount != 0) View.VISIBLE else View.GONE

        if (previousChildCount == 0 && newChildCount != 0) {
            onItemsSelectedListener?.accept(true)
        } else if (previousChildCount != 0 && newChildCount == 0) {
            onItemsSelectedListener?.accept(false)
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