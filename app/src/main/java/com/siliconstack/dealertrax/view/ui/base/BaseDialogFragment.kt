package com.siliconstack.dealertrax.view.ui.base

import android.animation.Animator
import android.support.v4.app.DialogFragment
import android.view.KeyEvent
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.view.helper.DialogHelper
import com.siliconstack.dealertrax.view.utility.UIUtility

open class BaseDialogFragment :DialogFragment(){

    lateinit var pivotXY:IntArray

    lateinit var progressDialog: MaterialDialog

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window.setLayout(width, height)
            dialog.window.setBackgroundDrawable(resources.getDrawable(R.drawable.transparent))

            if(::pivotXY.isInitialized) {
                var scaleDown = UIUtility.getObjectAnimatorForDialogFragment(dialog.window.decorView, pivotXY, true)
                scaleDown.start();
                dialog.setOnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                        dismissDialog()
                    }
                    false
                }
            }
            progressDialog = DialogHelper.materialProgressDialog(context!!)
        }
    }

    open  fun dismissDialog() {
        if(::pivotXY.isInitialized) {
            var scaleDown = UIUtility.getObjectAnimatorForDialogFragment(dialog.window.decorView, pivotXY, false)
            scaleDown.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                    this@BaseDialogFragment.dismiss()
                }

                override fun onAnimationEnd(animation: Animator?) {
                    this@BaseDialogFragment.dismiss()
                }

                override fun onAnimationCancel(animation: Animator?) {
                    this@BaseDialogFragment.dismiss()
                }

                override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {

                }

                override fun onAnimationStart(animation: Animator?) {

                }
            })
            scaleDown.start();
        }
        else this@BaseDialogFragment.dismiss()
    }

}