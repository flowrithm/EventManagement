package com.flowrithm.daangdiaries;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.flowrithm.daangdiaries.Migration.DataMigration;

import io.realm.Realm;
import io.realm.RealmConfiguration;


/**
 * Created by dev on 04-05-2017.
 */

public class Application extends android.app.Application {

    private static Application application;
    public static final String TAG = android.app.Application.class
            .getSimpleName();
    private RequestQueue requestQueue;
    public static Realm realm;
    private static SharedPreferences pref;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
        final RealmConfiguration configuration = new RealmConfiguration.Builder(this).name("TextTraders.realm").migration(new DataMigration()).schemaVersion(1).build();
        realm=Realm.getInstance(configuration);

//        Fresco.initialize(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this ;
        pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public static SharedPreferences getSharedPreferenceInstance(){
        return pref;
    }

    public static Realm getRealmInstance(){
        return realm;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public static Application getInstance() {
        return application;
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

}
