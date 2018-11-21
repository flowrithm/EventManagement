package com.flowrithm.daangdiaries.Firebase;

import android.os.Build;
import android.support.annotation.RequiresApi;


import com.crashlytics.android.Crashlytics;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.e("Notification","From :----  "+remoteMessage.getFrom());
//        Log.e("Notification","Body :----  "+remoteMessage.getData()+"");
        try {
            Utils.sendNewNotification(this,remoteMessage);
        } catch (Exception ex) {
            Crashlytics.logException(ex.fillInStackTrace());
        }

    }


}
