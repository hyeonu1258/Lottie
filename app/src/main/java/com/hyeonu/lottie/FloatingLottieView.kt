package com.hyeonu.lottie

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable.*

class FloatingLottieView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                   defStyleAttr: Int = 0)
    : LottieAnimationView(context, attrs, defStyleAttr) {
    var deltaX = 0f
    var deltaY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            ACTION_DOWN -> {
                playAnimation()
                repeatCount = INFINITE
                deltaX = event.rawX - translationX
                deltaY = event.rawY - translationY
            }
            ACTION_UP -> {
                repeatCount = 0
            }
            ACTION_MOVE -> {
                translationX = event.rawX - deltaX
                translationY = event.rawY - deltaY
            }
        }
        return true
    }
}

