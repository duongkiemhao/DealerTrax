package com.siliconstack.dealertrax.view.listeners;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

import com.siliconstack.dealertrax.view.ui.MainActivityListener;

public  class MyPhoneStateListener extends PhoneStateListener
{
    private MainActivityListener mainActivityListener;
    public MyPhoneStateListener(MainActivityListener mainActivityListener){
        this.mainActivityListener=mainActivityListener;
    }
    /* Get the Signal strength from the provider, each time there is an update */
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength)
    {
        super.onSignalStrengthsChanged(signalStrength);
        mainActivityListener.onSignalReceived(signalStrength);
//        Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = "
//                + String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();
    }

};


