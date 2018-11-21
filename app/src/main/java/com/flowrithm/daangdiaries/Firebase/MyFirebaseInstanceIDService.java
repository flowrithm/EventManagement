package com.flowrithm.daangdiaries.Firebase;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by dev on 3/1/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {


    SharedPreferences pref;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("TokenId",refreshedToken);
        editor.commit();
        Log.d("TOKEN", "Refreshed token: " + refreshedToken);
    }


}
