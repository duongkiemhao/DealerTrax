package com.siliconstack.dealertrax.view.ui.scan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ZXingScannerActivity : Activity(), ZXingScannerView.ResultHandler {
    private var mScannerView: ZXingScannerView? = null



    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this)    // Programmatically initialize the scanner view
        setContentView(mScannerView)                // Set the scanner view as the content view
    }

    public override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera()          // Start camera on resume
    }

    public override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera()           // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        // Do something with the result here
//        Log.v(TAG, rawResult.getContents()) // Prints scan results
//        Log.v(TAG, rawResult.getBarcodeFormat().getName()) // Prints the scan format (qrcode, pdf417 etc.)
        val intent=Intent()
        intent.putExtra("result",rawResult.text)
        setResult(Activity.RESULT_OK,intent)
        finish()
        // If you would like to resume scanning, call this method below:
        //mScannerView!!.resumeCameraPreview(this)
    }
}