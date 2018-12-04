package com.siliconstack.dealertrax.view.utility

import java.text.DecimalFormat

class NumericUtility{
    companion object {
        fun isNumeric(strValue : String): Boolean{
            return strValue.matches("-?\\d+(\\.\\d+)?".toRegex())

        }

        fun format2Decimal(number : Number) : String{
            return DecimalFormat("0.00").format(number)
        }
    }
}