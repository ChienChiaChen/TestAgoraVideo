package com.example.testagoravideo.utils

import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import com.example.testagoravideo.R


fun View.changeSelectedColor() {
    val iv = this as ImageView
    if (iv.isSelected) {
        iv.isSelected = false
        iv.clearColorFilter()
    } else {
        iv.isSelected = true
        iv.setColorFilter(context.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
    }
}
