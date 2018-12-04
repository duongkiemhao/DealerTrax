package com.siliconstack.dealertrax.view.control

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.siliconstack.dealertrax.view.utility.Utility

class CameraOverlayViewGroup : FrameLayout {


    private var mBackgroundPaint: Paint? = null
    private var mCx = -1f
    private var mCy = -1f

    private val mTutorialColor = Color.parseColor("#99000000")
    var cameraOverlayViewGroupListener:CameraOverlayViewGroupListener?=null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        cameraOverlayViewGroupListener!!.onTouch(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun init() {
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        mBackgroundPaint = Paint()
        mBackgroundPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mCx = event.x
        mCy = event.y
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(mTutorialColor)
        canvas.drawRect(
                (canvas.width - Utility.convertDPtoPIXEL(270F)) / 2,
                (canvas.height - Utility.convertDPtoPIXEL(150F)) / 2,
                (canvas.width - Utility.convertDPtoPIXEL(270F)) / 2 + Utility.convertDPtoPIXEL(270F),
                (canvas.height - Utility.convertDPtoPIXEL(150F)) / 2 + Utility.convertDPtoPIXEL(150F), mBackgroundPaint!!)

    }


    interface CameraOverlayViewGroupListener {
        fun onTouch(motionEvent: MotionEvent)
    }

    companion object {
        private val RADIUS = 200f
    }
}
