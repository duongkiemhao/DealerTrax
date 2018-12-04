package com.siliconstack.dealertrax.view.utility

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import com.siliconstack.dealertrax.config.Constant
import com.siliconstack.dealertrax.config.Constant.Companion.COMBINE_DATE_TIME_FORMAT
import com.siliconstack.dealertrax.view.control.DateTimePickupListener
import org.joda.time.DateTime
import org.joda.time.DateTimeComparator
import java.text.SimpleDateFormat
import java.util.*

class DateUtility{
    companion object {

        val PATTERN_DDMMYYYY = ("^.*?(\\d{2}|\\d)[.|/|-](\\d{2}|\\d{1})[.|/|-](\\d{4}|\\d{2})"
                + "( \\W | )?" +
                "(\\d{2}|\\d)?[.|/|-]?(\\d{2}|\\d)?[.|/|-]?(\\d{4}|\\d{2})?.*");

        val PATTERN_DDMMMYYYY = ("^.*?(\\d{2}|\\d)[ |.|/|-]?(\\D{3}|\\D{4})[ |.|/|-]?(\\d{4}|\\d{2})"
                + "( \\W | )?" +
                "(\\d{2}|\\d)?[ |.|/|-]?(\\D{3}|\\D{4})?[ |.|/|-]?(\\d{4}|\\d{2})?.*");

        val DDMMYYYY = "ddMMyyyy"
        val DDMMMYYYY = "ddMMMyyyy"


//        fun convertServerDateTimeStrToDateTimeStr(serverDateStr: String?, timePattern:String) : String{
//            if(!serverDateStr.isNullOrBlank()) {
//                try {
//                    var dateTime = SimpleDateFormat(Constant.SERVER_DATE_FORMAT).parse(serverDateStr)
//                    return SimpleDateFormat(timePattern).format(dateTime)
//                }
//                catch (exp:Exception){
//                    return ""
//                }
//            }
//            return ""
//        }

        fun createDateFromDatePart(dateStr: String) : Date?{
            try {

                return SimpleDateFormat(Constant.UI_DATE_FORMAT).parse(dateStr)
            }
            catch (exp:Exception){
                return null
            }
        }

        fun createDateFromTimePart(timeStr: String) : Date?{
            try {

                return SimpleDateFormat(Constant.UI_TIME_FORMAT).parse(timeStr)
            }
            catch (exp:Exception){
                return null
            }


        }

        fun showDatePickup(calendar: Calendar, context: Context, datetimePickupListener: DateTimePickupListener) {
            Utility.hideKeyboard(context)
            var allowDismiss = false
            var dialog = object : DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                datetimePickupListener.onDateTimeSelect(DateUtility.parseDateToDateTimeStr(Constant.UI_DATE_FORMAT,calendar.time)?:"")


            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    if (which == DialogInterface.BUTTON_POSITIVE || which == DialogInterface.BUTTON_NEGATIVE) {
                        allowDismiss = true
                    }
                    super.onClick(dialog, which)
                }

                override fun dismiss() {
                    if (allowDismiss)
                        super.dismiss()
                }
            }

            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Today", DialogInterface.OnClickListener { dialogInterface, which ->
                calendar.time=Date()
                dialog.datePicker.init(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null)

            })
            dialog.show()
        }

        fun showTimePickup(calendar: Calendar, context: Context, datetimePickupListener: DateTimePickupListener) {
            Utility.hideKeyboard(context!!)
            var allowDismiss = false
            var dialog = object : TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                datetimePickupListener.onDateTimeSelect(DateUtility.parseDateToDateTimeStr(Constant.UI_TIME_FORMAT, calendar.time)?:"")
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false) {

                override fun onClick(dialog: DialogInterface, which: Int) {
                    if (which == DialogInterface.BUTTON_POSITIVE || which == DialogInterface.BUTTON_NEGATIVE) {
                        allowDismiss = true
                    }
                    super.onClick(dialog, which)
                }

                override fun dismiss() {
                    if (allowDismiss)
                        super.dismiss()
                }

            }

            dialog.show()
        }

//        fun combineDateAndTimeStrToServerDateStr(dateStr:String,timeStr:String):String{
//            try {
//                val date = SimpleDateFormat(COMBINE_DATE_TIME_FORMAT).parse(dateStr + " " + timeStr)
//                return SimpleDateFormat(SERVER_DATE_FORMAT).format(date)
//            }
//            catch (exp:Exception){
//                return ""
//            }
//        }

        fun combineDateAndTimeStrToDate(dateStr:String?, timeStr:String?):Date?{
            try {
                return SimpleDateFormat(COMBINE_DATE_TIME_FORMAT).parse(dateStr + " " + timeStr)
            }
            catch (exp:Exception){
                return null
            }
        }

        fun checkValidDate(pattern:String,dateStr:String?):Boolean{
            try{
                 SimpleDateFormat(pattern).parse(dateStr)
                return true
            }
            catch (exp:Exception){
                return false
            }
        }

        fun checkDOBDate(pattern:String,dateStr:String?):Boolean{
            try{
                val dobDate=DateTime (SimpleDateFormat(pattern).parse(dateStr))
                if(DateTimeComparator.getDateOnlyInstance().compare(Date(),dobDate)>0)
                    return true
                else return false
            }
            catch (exp:Exception){
                return false
            }
        }

        fun parseDateToDateTimeStr(pattern:String, date:Date?):String?{
            try{
                return SimpleDateFormat(pattern).format(date)
            }
            catch (exp:Exception){
                return null
            }
        }

        fun parseDateTimeStrToDate(pattern:String, dateStr:String?):Date?{
            try{
                return SimpleDateFormat(pattern).parse(dateStr)
            }
            catch (exp:Exception){
                return null
            }
        }

    }
}