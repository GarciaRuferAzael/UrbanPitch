package com.example.urbanpitch.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

fun resizeDrawable(context: Context, drawableRes: Int, width: Int, height: Int): Drawable {
    val original = ContextCompat.getDrawable(context, drawableRes) as BitmapDrawable
    val scaledBitmap = Bitmap.createScaledBitmap(original.bitmap, width, height, true)
    return BitmapDrawable(context.resources, scaledBitmap)
}
