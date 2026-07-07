package com.example.brand_shoe

fun imageKeyToDrawable(key: String): Int {
    return when (key) {
        "shoe1" -> R.drawable.shoe1
        "shoe2" -> R.drawable.shoe2
        "shoe3" -> R.drawable.shoe3
        "shoe4" -> R.drawable.shoe4
        "shoe5" -> R.drawable.shoe5
        "shoe6" -> R.drawable.shoe6
        else -> R.drawable.shoe1
    }
}

val availableImageKeys = listOf("shoe1", "shoe2", "shoe3", "shoe4", "shoe5", "shoe6")