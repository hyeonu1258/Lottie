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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var startX = event.x
        var startY = event.y
        var deltaX = 0f
        var deltaY = 0f

        when (event.action) {
            ACTION_DOWN -> {
                Log.d("MyLog", "x = $x, y = $y, event.x = ${event.x}, event.y = ${event.y}, event.rawX = ${event.rawX}, event.rawY = ${event.rawY}, translationX = $translationX, translationY = $translationY")
                playAnimation()
                repeatCount = INFINITE
                scaleX = 1.3f
                scaleY = 1.3f
                deltaX = startX - translationX
                deltaY = startY - translationY
            }
            ACTION_UP -> {
                Log.d("MyLog", "x = $x, y = $y, event.x = ${event.x}, event.y = ${event.y}, event.rawX = ${event.rawX}, event.rawY = ${event.rawY}, translationX = $translationX, translationY = $translationY")
                repeatCount = 0
                scaleX = 1f
                scaleY = 1f
            }
            ACTION_MOVE -> {
                Log.d("MyLog", "x = $x, y = $y, event.x = ${event.x}, event.y = ${event.y}, event.rawX = ${event.rawX}, event.rawY = ${event.rawY}, translationX = $translationX, translationY = $translationY")
                translationX = startX - deltaX
                translationY = startY - deltaY
            }
        }
        return true
    }
}

