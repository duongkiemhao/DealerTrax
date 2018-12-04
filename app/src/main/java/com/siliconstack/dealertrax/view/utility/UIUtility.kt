package com.siliconstack.dealertrax.view.utility

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Rect
import android.view.View

class UIUtility{
    companion object {
        fun getPivotXY(view: View):IntArray{
            var pivotXY=IntArray(2)
            var rect= Rect()
            view.getLocationOnScreen(pivotXY)
            view.getLocalVisibleRect(rect)
            pivotXY[0]+=rect.width()/2
            pivotXY[1]-=rect.height()/2
            return pivotXY
        }

        fun getObjectAnimatorForDialogFragment(decorView:View,pivotXY:IntArray,isEnter:Boolean):ObjectAnimator{
            var objectAnimator=ObjectAnimator.ofPropertyValuesHolder(decorView,
                    PropertyValuesHolder.ofFloat("scaleX", if(isEnter) 0f else 1.0f, if(isEnter) 1.0f else 0f),
                    PropertyValuesHolder.ofFloat("scaleY", if(isEnter) 0f else 1.0f, if(isEnter) 1.0f else 0f),
                    PropertyValuesHolder.ofFloat("alpha", if(isEnter) 0f else 1.0f, if(isEnter) 1.0f else 0f),
                    PropertyValuesHolder.ofFloat("pivotX", pivotXY[0].toFloat(),pivotXY[0].toFloat()),
                    PropertyValuesHolder.ofFloat("pivotY", pivotXY[1].toFloat(),pivotXY[1].toFloat()))
            objectAnimator.setDuration(300);
            return objectAnimator
        }

    }
}