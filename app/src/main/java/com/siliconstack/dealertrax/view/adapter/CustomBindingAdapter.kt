package com.siliconstack.dealertrax.view.adapter


class CustomBindingAdapter {
//    companion object {
//        @JvmStatic
//        @BindingAdapter("visibleGone")
//        fun showHide(view: View, show: Boolean) {
//            view.visibility = if (show) View.VISIBLE else View.GONE
//        }
//
//        @JvmStatic
//        @BindingAdapter("invisible")
//        fun showHideInvisible(view: View, show: Boolean) {
//            view.visibility = if (show) View.VISIBLE else View.INVISIBLE
//        }
//
//        @JvmStatic
//        @BindingAdapter("dealerKey")
//        fun dealerKey(view: View, show: Boolean) {
//            (view as TextView).text = if (!show) "Show Dealer Key" else "Hide Dealer Key"
//        }
//
//        @JvmStatic
//        @BindingAdapter(value = ["loginKey", "password"], requireAll = false)
//        fun loginKey(view: View, isLoginManually: Boolean,password : String?) {
//            (view as TextView).text = if (isLoginManually) "Login" else "Login with Fingerprint"
//            if (isLoginManually)
//                (view as TextView).setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_login, 0,0,0);
//            else (view as TextView).setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_finger,0,0,0);
//        }
//
//        @JvmStatic
//        @BindingAdapter("licenseFrontImage")
//        fun licenseFrontImage(view: View, licenseFrontImage: String?) {
//            (view as TextView).text = if (licenseFrontImage.isNullOrBlank()) "Scan Licence" else "View Licence"
//
//        }
//
//        @JvmStatic
//        @BindingAdapter("disableColor")
//        fun disableColor(view: View, isEnableAll: Boolean) {
//            if(!isEnableAll)
//                view.setBackgroundColor(view.context.resources.getColor(R.color.disable))
//            else view.setBackgroundColor(view.context.resources.getColor(R.color.white))
//        }
//
//        @JvmStatic
//        @BindingAdapter("todayEnable")
//        fun todayEnable(view: View, isEnable: Boolean) {
//            if(isEnable) {
//                (view as TextView).setTextColor(view.context.resources.getColor(R.color.button_blue))
//                view.isEnabled=true
//            }
//            else {
//                (view as TextView).setTextColor(view.context.resources.getColor(R.color.button_blue_disable))
//                view.isEnabled=false
//            }
//        }
//        @JvmStatic
//        @BindingAdapter(value = ["isPreNextEnable", "isPreNext"], requireAll = false )
//        fun preNextEnable(view: View, isEnable: Boolean,isPre: Boolean) {
//            if(isPre) {
//                (view as ImageView).setImageResource(if(isEnable) R.drawable.ic_left else R.drawable.ic_left_disable)
//            }
//            else {
//                (view as ImageView).setImageResource(if(isEnable) R.drawable.ic_right else R.drawable.ic_right_disable)
//            }
//            view.isEnabled=isEnable
//        }
//
//        @JvmStatic
//        @BindingAdapter("todayBackground")
//        fun todayBackground(view: View, isToday: Boolean) {
//            if(isToday) {
//                view.setBackgroundResource(R.drawable.today_bg)
//                (view as TextView).setTextColor(Color.WHITE)
//            }
//            else {
//                view.setBackgroundResource(R.drawable.transparent)
//                (view as TextView).setTextColor(view.context.resources.getColor(R.color.color_1))
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("enableBackground")
//        fun enableBackground(view: View, isEnable: Boolean) {
//            if(isEnable) {
//                (view as TextView).background=view.context.resources.getDrawable(R.drawable.textbox_white)
//                view.isEnabled=true
//            }
//            else {
//                (view as TextView).background=view.context.resources.getDrawable(R.drawable.textbox_disable)
//                view.isEnabled=false
//            }
//        }
//
//        @JvmStatic
//        @BindingAdapter("checkInButton")
//        fun checkInButton(view: View, isEnable: Boolean) {
//            if(isEnable) {
//                (view as TextView).background=view.context.resources.getDrawable(R.drawable.button_stroke_blue)
//                view.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_check_in_out,0,0,0)
//                view.setTextColor(view.context.resources.getColor(R.color.button_blue))
//                view.isEnabled=true
//            }
//            else {
//                (view as TextView).background=view.context.resources.getDrawable(R.drawable.button_stroke_blue_disable)
//                view.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_check_in_out_disable,0,0,0)
//                view.setTextColor(view.context.resources.getColor(R.color.button_blue_disable))
//                view.isEnabled=false
//            }
//        }
//    }
}