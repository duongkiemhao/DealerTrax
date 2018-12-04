package com.siliconstack.dealertrax.view.helper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.View
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.siliconstack.dealertrax.R
import com.siliconstack.dealertrax.view.adapter.FilterListAdapter
import com.siliconstack.dealertrax.view.control.SimpleDividerItemDecoration

class DialogHelper {


    companion object {
        fun materialDialog(message: String, positiveText: String, positiveCallback: MaterialDialog.SingleButtonCallback, context: Context): MaterialDialog =
                MaterialDialog.Builder(context)
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .content(message)
                        .contentGravity(GravityEnum.CENTER)
                        .negativeText(positiveText)
                        .onNegative { dialog, which ->
                            positiveCallback.onClick(dialog, which)
                        }
                        .build()


        fun materialDialog(message: String, negativeText: String, context: Context): MaterialDialog =
                MaterialDialog.Builder(context)
                        .content(message)
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .contentGravity(GravityEnum.CENTER)
                        .negativeText(negativeText)
                        .onNegative { dialog, which ->
                            dialog.dismiss()
                        }
                        .build()

        fun materialDialog(message: String, positiveText: String, negativeText: String, positiveCallback: MaterialDialog.SingleButtonCallback, context: Context): MaterialDialog =
                MaterialDialog.Builder(context)
                        .content(message)
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .contentGravity(GravityEnum.CENTER)
                        .positiveText(positiveText)
                        .negativeText(negativeText)
                        .onPositive { dialog, which ->
                            positiveCallback.onClick(dialog, which)
                        }
                        .onNegative { dialog, which ->
                            dialog.dismiss()
                        }
                        .build()

        fun materialDialog(message: String, positiveText: String, negativeText: String, positiveCallback: MaterialDialog.SingleButtonCallback, negativeCallback: MaterialDialog.SingleButtonCallback, context: Context): MaterialDialog =
                MaterialDialog.Builder(context)
                        .content(message)
                        .contentGravity(GravityEnum.CENTER)
                        .positiveText(positiveText)
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .negativeText(negativeText)
                        .onPositive { dialog, which ->
                            positiveCallback.onClick(dialog, which)
                        }
                        .onNegative { dialog, which ->
                            negativeCallback.onClick(dialog, which)
                        }
                        .build()

        fun materialDialog(message: String, positiveText: String, negativeText: String, naturalText: String, positiveCallback: MaterialDialog.SingleButtonCallback, negativeCallback: MaterialDialog.SingleButtonCallback,
                           naturalCallback: MaterialDialog.SingleButtonCallback, context: Context): MaterialDialog =
                MaterialDialog.Builder(context)
                        .content(message)
                        .contentGravity(GravityEnum.CENTER)
                        .positiveText(positiveText)
                        .negativeText(negativeText)
                        .neutralText(naturalText)
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .onPositive { dialog, which ->
                            positiveCallback.onClick(dialog, which)
                        }
                        .onNegative { dialog, which ->
                            negativeCallback.onClick(dialog, which)
                        }
                        .onNeutral { dialog, which ->
                            naturalCallback.onClick(dialog, which)
                        }
                        .build()


        fun materialDialog(message: String, hint: String, preString: String, positiveText: String, negativeText: String
                           , positiveCallback: MaterialDialog.SingleButtonCallback, context: Context): MaterialDialog =
                MaterialDialog.Builder(context)
                        .content(message)
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .contentGravity(GravityEnum.CENTER)
                        //.inputRange(1,INPUT_MAX_LENGTH)
                        .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .input(hint, preString, MaterialDialog.InputCallback { dialog, input ->
                        }).positiveText(positiveText).negativeText(negativeText).onNegative { dialog, which ->
                            dialog.dismiss()
                        }
                        .onPositive { dialog, which ->
                            positiveCallback.onClick(dialog, which)


                        }
                        .build()

        fun materialProgressDialog(context: Context): MaterialDialog {
            var materialDialog = MaterialDialog.Builder(context).customView(R.layout.simple_progress_dialog, false).build()
            materialDialog.setCanceledOnTouchOutside(false)
            materialDialog.setCancelable(false)
            materialDialog.getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            materialDialog.getWindow().setDimAmount(0F)
            return materialDialog
        }


        fun materialProgressDialog(title: String, context: Context, adapter: RecyclerView.Adapter<FilterListAdapter.ViewHolder>, callback: MaterialDialog.SingleButtonCallback): MaterialDialog {
            var materialDialog = MaterialDialog.Builder(context)
                    //.title(title)
                    .theme(Theme.LIGHT)
                    .contentGravity(GravityEnum.CENTER)
                    .adapter(adapter, null)
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .positiveText("Ok")
                    .negativeText("Close")
                    .onNegative { dialog, which ->
                        dialog.dismiss()
                    }
                    .onPositive { dialog, which ->
                        callback.onClick(dialog, which)
                    }
                    .build()

            //materialDialog.recyclerView.setBackgroundColor(Color.parseColor("#99303C45"))

            materialDialog.titleView.visibility = View.GONE


            materialDialog.recyclerView.addItemDecoration(SimpleDividerItemDecoration(context, R.drawable.selection_divider))
            return materialDialog
        }


        fun materialTitleDialog(title: String, message: String, positiveText: String, positiveCallback: MaterialDialog.SingleButtonCallback, context: Context): MaterialDialog =
                MaterialDialog.Builder(context)
                        .title(title)
                        .content(message)
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .contentGravity(GravityEnum.CENTER)
                        .positiveText(positiveText)
                        .onPositive { dialog, which ->
                            positiveCallback.onClick(dialog, which)
                        }

                        .build()

        fun materialCustomViewDialog(title: String, view: View, positiveText: String, negativeText: String, positiveCallback: MaterialDialog.SingleButtonCallback, negativeCallback: MaterialDialog.SingleButtonCallback, context: Context): MaterialDialog =
                MaterialDialog.Builder(context)
                        .title(title)
                        .customView(view, true)
                        .canceledOnTouchOutside(false)
                        .cancelable(false)
                        .contentGravity(GravityEnum.CENTER)
                        .positiveText(positiveText)
                        .negativeText(negativeText)
                        .onPositive { dialog, which ->
                            positiveCallback.onClick(dialog, which)
                        }
                        .onNegative { dialog, which ->
                            negativeCallback.onClick(dialog, which)
                        }

                        .build()


    }


}