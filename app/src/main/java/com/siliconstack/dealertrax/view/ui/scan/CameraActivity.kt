package com.siliconstack.dealertrax.view.ui.scan

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import org.greenrobot.eventbus.EventBus
import android.view.Surface
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.databinding.CameraActivityBinding
import com.siliconstack.dealertrax.view.control.CameraOverlayViewGroup
import com.siliconstack.dealertrax.view.eventbus.MainEventBus
import com.siliconstack.dealertrax.view.utility.Utility


class CameraActivity : AppCompatActivity() {

    lateinit var cameraActivityBinding: CameraActivityBinding
    lateinit var animatorSet: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        setListener()
        init()
        loadInfo()
    }

    fun initViewBinding(){
        cameraActivityBinding = DataBindingUtil.setContentView(this, R.layout.camera_activity)
    }

    fun setListener(){
        cameraActivityBinding.btnTake.setOnClickListener {
            cameraActivityBinding.camera.capturePicture()
        }
        cameraActivityBinding.camera.addCameraListener(object :CameraListener(){
            override fun onPictureTaken(data: ByteArray?) {
                val width: Int = cameraActivityBinding.llRoot.width
                val height: Int = cameraActivityBinding.llRoot.height
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                options.inMutable = true
                options.inScaled = false
                options.inSampleSize = 2
                var bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size, options)
                val matrix = Matrix()
                val rotation = this@CameraActivity.windowManager.defaultDisplay.rotation
                var degrees = 0f
                when (rotation) {
                    Surface.ROTATION_0 -> degrees = 90f
                    Surface.ROTATION_90 -> degrees = 0f
                    Surface.ROTATION_180 -> degrees = 270f
                    Surface.ROTATION_270 -> degrees = 180f
                }
                matrix.postRotate(degrees)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                val bh = bitmap!!.height
                val bw = bitmap.width
                val l = cameraActivityBinding.capturedImage.x.toInt() * bw / width
                val t = cameraActivityBinding.capturedImage.y.toInt() * bh / height
                val w = cameraActivityBinding.capturedImage.width * bw / width
                val h = cameraActivityBinding.capturedImage.height * bh / height
                var resizedBitmap = Bitmap.createBitmap(bitmap, l, t, w, h)
                resizedBitmap = Utility.scaleBitmapDown(resizedBitmap,840)
                if (resizedBitmap != null) {
                    val mainEventBus= MainEventBus()
                    mainEventBus.bitmapURL= Utility.saveBitmapToFile(resizedBitmap)
                    EventBus.getDefault().post(mainEventBus)
                    resizedBitmap.recycle()
                    finish()
                }
            }
        });
        cameraActivityBinding.overlayView.cameraOverlayViewGroupListener=object: CameraOverlayViewGroup.CameraOverlayViewGroupListener{
            override fun onTouch(motionEvent: MotionEvent) {
                cameraActivityBinding.camera.onTouchEvent(motionEvent)
            }

        }
        //camera
        cameraActivityBinding.camera.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
        cameraActivityBinding.camera.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER); // Tap to focus!

    }

    fun init(){
        cameraActivityBinding.capturedImage.viewTreeObserver.addOnGlobalLayoutListener (object:ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                cameraActivityBinding.overlayView.visibility= View.VISIBLE
                cameraActivityBinding.capturedImage.viewTreeObserver.removeOnGlobalLayoutListener (this)
            }
        })
        val fadeOut = ObjectAnimator.ofFloat(cameraActivityBinding.capturedImage, "alpha",  1f, .1f);
        fadeOut.duration = 500;
        val fadeIn = ObjectAnimator.ofFloat(cameraActivityBinding.capturedImage, "alpha", .1f, 1f);
        fadeIn.duration = 500;
        animatorSet = AnimatorSet();
        var count=0
        animatorSet.play(fadeOut).after(fadeIn)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation);
                count++
                if(count<10)
                    animatorSet.start();
                else{
                    Handler().postDelayed({
                        count=0
                        animatorSet.start()
                    },5000)

                }
            }
        });
        animatorSet.start()
        cameraActivityBinding.capturedImage.postDelayed({
            cameraActivityBinding.overlayView.myWidth= cameraActivityBinding.capturedImage.width.toFloat()
            cameraActivityBinding.overlayView.myHeight= cameraActivityBinding.capturedImage.height.toFloat()
            cameraActivityBinding.overlayView.requestLayout()

        },500)
    }

    fun loadInfo(){

    }


    override fun onBackPressed() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        cameraActivityBinding.camera.start()
    }

    override fun onPause() {
        super.onPause()
        cameraActivityBinding.camera.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        animatorSet.removeAllListeners()
        animatorSet.cancel()
        cameraActivityBinding.camera.destroy()
    }
}

