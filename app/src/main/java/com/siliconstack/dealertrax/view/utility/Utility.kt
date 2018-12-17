package com.siliconstack.dealertrax.view.utility

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.siliconstack.dealertrax.AppApplication.Companion.gson
import com.siliconstack.dealertrax.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import android.util.TypedValue
import com.siliconstack.dealertrax.AppApplication
import java.io.*
import android.content.pm.PackageManager
import android.graphics.Bitmap






class Utility{



    companion object {

        fun getMAC(context: Context) : String {

            val wifiManager =  context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            var wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getMacAddress().replace(":","")


        }

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            return if (connectivityManager is ConnectivityManager) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                networkInfo?.isConnected ?: false
            } else false
        }

        fun convertDPtoPIXEL( number: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, number, AppApplication.instance.resources.displayMetrics);
        }

        fun convertSPtoPIXEL(number: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, number, AppApplication.instance.resources.displayMetrics);
        }


        // 1:> ,0:= ,-1<
        fun compare2DatePart(start: Date, end: Date): Int {
            if (start.getYear() !== end.getYear())
                return start.getYear() - end.getYear()
            return if (start.getMonth() !== end.getMonth()) start.getMonth() - end.getMonth() else start.getDate() - end.getDate()

        }

        fun hideKeyboard(view: View, context: Context) {
            val imm = (context as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm!!.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }

        fun hideKeyboard(context: Context){
            val inputManager = context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow((context as Activity)
                    .currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
        }

        fun showKeyboard(context: Context){
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        }

        fun isJSONValid(jsonInString: String): Boolean {
            try {
                gson.fromJson(jsonInString, Any::class.java)
                return true
            } catch (ex: com.google.gson.JsonSyntaxException) {
                return false
            }
        }

        fun returnNullIfEmpty(value: String?): String?{
            if(value.isNullOrBlank())
                return null
            else return value
        }



        fun createImageFile(context: Context) : File {
            // Create an image file name
            var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
            var imageFileName = "JPEG_" + timeStamp + "_";
            var storageDir = context.cacheDir
            var image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            // Save a file: path for use with ACTION_VIEW intents

            return image;
        }

        fun saveBitmapToFile(bitmap: Bitmap):String?{
            var file=createImageFile(AppApplication.instance)
            try{
                // Compress the bitmap and save in jpg format
                val stream: OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
                stream.flush()
                stream.close()
            }catch (e: IOException){
                e.printStackTrace()
                return null
            }
            //bitmap.recycle()
            return file.absolutePath

        }

        fun deleteFile(uri:Uri,context: Context){
            //context.getContentResolver().deleteItem(uri, null, null);
        }

        fun deleteFile(url:String){
            //File(url).deleteItem()
        }

        fun convertToBitmapTimeStampBase64FromRawURL(filePath: String):String {
            val bm = BitmapFactory.decodeFile(filePath)
            val dest = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateTime = sdf.format(Calendar.getInstance().time) // reading local time in the system

            val cs = Canvas(dest)
            val tPaint = Paint()
            tPaint.textSize=Utility.convertSPtoPIXEL(20F)
            tPaint.color = AppApplication.instance.resources.getColor(R.color.orange)
            tPaint.isAntiAlias=true
            tPaint.style = Paint.Style.FILL
            cs.drawBitmap(bm, 0F, 0F, null)
            var rect=Rect()
            tPaint.getTextBounds(dateTime,0,dateTime.length,rect)
            cs.drawText(dateTime, (bm.width-rect.width()-20).toFloat(), (bm.height-rect.height()-20).toFloat(), tPaint)
            val baos = ByteArrayOutputStream()
            dest.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArrayImage = baos.toByteArray()
            dest.recycle()
            bm.recycle()
            return android.util.Base64.encodeToString(byteArrayImage,android.util.Base64.NO_WRAP)
        }

        fun convertBitmapToBase64(filePath: String):String {
            val bm = BitmapFactory.decodeFile(filePath)
            val dest = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888)
            val baos = ByteArrayOutputStream()
            dest.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArrayImage = baos.toByteArray()
            return android.util.Base64.encodeToString(byteArrayImage,android.util.Base64.NO_WRAP)
        }


        fun convertToBitmapTimeStampFromRawURL(filePath: String):Bitmap {
            val bitmap= BitmapFactory.decodeFile(filePath)
            val dest = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val dateTime = sdf.format(Calendar.getInstance().time) // reading local time in the system

            val cs = Canvas(dest)
            val tPaint = Paint()
            tPaint.textSize=Utility.convertSPtoPIXEL(20F)
            tPaint.color = AppApplication.instance.resources.getColor(R.color.orange)
            tPaint.isAntiAlias=true
            tPaint.style = Paint.Style.FILL
            cs.drawBitmap(bitmap, 0F, 0F, null)
            var rect=Rect()
            tPaint.getTextBounds(dateTime,0,dateTime.length,rect)
            cs.drawText(dateTime, (bitmap.width-rect.width()-20).toFloat(), (bitmap.height-rect.height()-10).toFloat(), tPaint)
            val baos = ByteArrayOutputStream()
            dest.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArrayImage = baos.toByteArray()
            dest.recycle()
            bitmap.recycle()
            return BitmapFactory.decodeByteArray(byteArrayImage,0,byteArrayImage.size)
        }

        fun convertBitmapToBase64(bitmap: Bitmap):String {

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArrayImage = baos.toByteArray()
            return android.util.Base64.encodeToString(byteArrayImage,android.util.Base64.DEFAULT)
        }

        fun convertScaleBitmapToBase64(context: Context,resId: Int,width:Int,height:Int):String {
            var bitmapOption = BitmapFactory.Options()
            bitmapOption.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmapOption.inMutable = true
            bitmapOption.inScaled = false
            val bitmap= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources, resId, bitmapOption),
                    context.resources.getDimension(R.dimen.car_box_width).toInt(), 100, true)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val byteArrayImage = baos.toByteArray()
            return android.util.Base64.encodeToString(byteArrayImage,android.util.Base64.DEFAULT)
        }

        fun getBitmapFromURL(url: String):Bitmap {
            return BitmapFactory.decodeFile(url)
        }

        fun convertBase64ToBitmap(encodedImage: String):Bitmap {
            val decodedString = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }

        fun isTablet(context: Context):Boolean{
            return context.resources.getBoolean(R.bool.isTablet)
        }

        fun isOnlyDigit(content:String):Boolean{
            val pattern = Pattern.compile("^\\d{5,}")
            val matcher = pattern.matcher(content)
            return matcher.matches()
        }

        fun saveScreenShot(view : View):String?{
            view.setDrawingCacheEnabled(true);
            val screenshot= view.getDrawingCache(true).copy(Bitmap.Config.ARGB_8888, false);
            view.destroyDrawingCache();
            return Utility.saveBitmapToFile(screenshot)
        }
        fun isFileExist(url:String?): Boolean {
            try {
                val file=File(url)
                return file.exists()
            } catch (ex: com.google.gson.JsonSyntaxException) {
                return false
            }
        }


        fun getDirSize(cacheDir:File):Long{
            var size: Long = 0
            val files = cacheDir.listFiles()

            for (file in files) {
                if (file.isFile) {
                    size += file.length()
                }
            }
            return size
        }

        fun getAppVersionName(): String{
            try
            {
                val pInfo = AppApplication.instance.getPackageManager().getPackageInfo(AppApplication.instance.getPackageName(), 0);
                return pInfo.versionName;
            } catch (e :PackageManager.NameNotFoundException)
            {
                return ""
            }
        }

         fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {

            val originalWidth = bitmap.width
            val originalHeight = bitmap.height
            var resizedWidth = maxDimension
            var resizedHeight = maxDimension

            if (originalHeight > originalWidth) {
                resizedHeight = maxDimension
                resizedWidth = (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
            } else if (originalWidth > originalHeight) {
                resizedWidth = maxDimension
                resizedHeight = (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
            } else if (originalHeight == originalWidth) {
                resizedHeight = maxDimension
                resizedWidth = maxDimension
            }
            return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
        }

    }
}