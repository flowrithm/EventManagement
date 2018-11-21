package com.flowrithm.daangdiaries.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.flowrithm.daangdiaries.Listner.SmsItemListner;


/**
 * Created by dev on 3/1/2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    private static SmsItemListner smsListner;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data=intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();
            if(smsListner!=null) {
                smsListner.messageReceived(sender, messageBody);
            }
        }
    }

    public static void bindSmsListner(SmsItemListner listner)
    {
        smsListner=listner;
    }

}
