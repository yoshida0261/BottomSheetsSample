package com.stah.bottomsheetssample

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class CustomBottomSheetBehavior <T : View>(private val context: Context, attrs: AttributeSet) :
    BottomSheetBehavior<T>(context, attrs), GestureDetector.OnGestureListener {

    private lateinit var customBottomSheetCallback: CustomBottomSheetCallback<T>

    private val  gestureDetector = GestureDetector(context, this)
    interface BottomSheetStateChangeListener {
        fun changeBottomSheetState(state: Int)
    }

    fun setupCallback(listener: BottomSheetStateChangeListener) {
        customBottomSheetCallback = CustomBottomSheetCallback(this, listener)
        this.setBottomSheetCallback(customBottomSheetCallback)
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: T, event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(parent, child, event)
    }

    override fun onDown(motionEvent: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(motionEvent: MotionEvent) {

    }

    override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
        state = STATE_EXPANDED
        return false
    }

    override fun onScroll(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(motionEvent: MotionEvent) {

    }

    override fun onFling(
        motionEvent: MotionEvent,
        motionEvent1: MotionEvent,
        v: Float,
        v1: Float
    ): Boolean {
        // フリックの方向を取得する
        if(motionEvent.y < motionEvent1.y) {
            // 上スワイプ
            Log.d("onFling", "下スワイプ")
            customBottomSheetCallback.directFling = DirectFling.Down
        } else {
            Log.d("onFling", "上スワイプ")
            customBottomSheetCallback.directFling = DirectFling.UP
        }
        return false
    }

    enum class DirectFling {
        UP, Down
    }

    internal inner class CustomBottomSheetCallback<T : View>(private val bottomSheetBehavior: CustomBottomSheetBehavior<T>, private val listener: BottomSheetStateChangeListener) :
        BottomSheetBehavior.BottomSheetCallback() {

        // 4(Collapsed) or 3(Expanded) or 6(Half_Expanded)
        var preState = 4

        var directFling: DirectFling? = null

        private var offset: Float = 0.toFloat()

        override fun onStateChanged(view: View, state: Int) {
            if(state == STATE_SETTLING) {
                when(preState) {
                    STATE_COLLAPSED -> {
                        if (directFling == DirectFling.UP) {
                            bottomSheetBehavior.state = STATE_HALF_EXPANDED
                        }
                    }

                    STATE_EXPANDED -> {
                        if (directFling == DirectFling.Down) {
                            bottomSheetBehavior.state = STATE_HALF_EXPANDED
                        }
                    }
                }

            }

            if (state == STATE_COLLAPSED) {
                preState = STATE_COLLAPSED
                listener.changeBottomSheetState(STATE_COLLAPSED)
            }
            if(state == STATE_EXPANDED) {
                preState = STATE_EXPANDED
                listener.changeBottomSheetState(STATE_EXPANDED)
            }

            if (state == STATE_HALF_EXPANDED) {
                preState = STATE_HALF_EXPANDED
                listener.changeBottomSheetState(STATE_HALF_EXPANDED)
            }
        }

        override fun onSlide(view: View, offset: Float) {
            this.offset = offset
        }
    }
}
