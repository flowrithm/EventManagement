package com.flowrithm.daangdiaries.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.flowrithm.daangdiaries.Activity.Home.Home;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity {

    @Bind(R.id.txtVersion)
    TextView txtVersion;

    SharedPreferences pref;
    PackageInfo info;
    String versionName;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        pref=Application.getSharedPreferenceInstance();
        ButterKnife.bind(this);
        UserId=pref.getString("UserId","");
        try {
            PackageManager manager = this.getPackageManager();
            info = manager.getPackageInfo(this.getPackageName(), 0);
            int versionCode = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            versionName = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ex) {
            Crashlytics.logException(ex.getCause());
        }
        txtVersion.setText(" V "+versionName);
        if(!UserId.equals("")) {
            AppLoginLog();
        }else{
            new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                    SplashScreen.this.finish();

                }
            }.start();
        }
    }

    //App Login Logs OTP Api Call
    public void AppLoginLog() {

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final String IMEI = telephonyManager.getDeviceId();
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.App_LOGIN_LOG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject json = new JSONObject(response);
                    Utils.ShowServiceDialog(SplashScreen.this, json);
                    if (json.getBoolean("success")) {
                        if(json.getInt("IsActive")==1) {
                            if(json.getInt("UpdateApp")==1){
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(SplashScreen.this);
                                builder1.setTitle("Update Application");
                                builder1.setMessage(json.getString("AppMessage" +
                                        ""));
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                try {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(json.getString("AppLink")));
                                                    startActivity(intent);
                                                    SplashScreen.this.finish();
                                                }catch (Exception ex){
                                                    Crashlytics.logException(ex.getCause());
                                                }
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }else {
                                new CountDownTimer(3000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        try {
                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString("UserId", json.getString("UserId"));
                                            editor.putInt("UserCreationRights", Integer.parseInt(json.getString("UserCreationRights")));
                                            editor.putInt("ScanningRights", Integer.parseInt(json.getString("ScanningRights")));
                                            editor.putString("Role", json.getString("Role"));
                                            editor.commit();
                                            Intent intent = new Intent(SplashScreen.this, Home.class);
                                            startActivity(intent);
                                            SplashScreen.this.finish();
                                        } catch (Exception ex) {
                                            Crashlytics.logException(ex);
                                            SplashScreen.this.finish();
                                        }
                                    }
                                }.start();
                            }
                        }else{
                            Toast.makeText(SplashScreen.this,"Your account has been blocked. Please contact to administration's",Toast.LENGTH_LONG).show();
                            pref.edit().clear().commit();
                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(intent);
                        }

                    }
                } catch (Exception ex) {
                    Crashlytics.logException(ex.getCause());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(SplashScreen.this,
                            SplashScreen.this.getString(R.string.error_network_timeout),
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                } else if (error instanceof ServerError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                } else if (error instanceof NetworkError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                } else if (error instanceof ParseError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                }
                SplashScreen.this.finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("UserId", UserId);
                map.put("DeviceName", Build.DEVICE);
                map.put("DeviceModel", Build.MODEL);
                map.put("OS","Android");
                Point size = Utils.GetScreenSize(SplashScreen.this);
                map.put("ScreenSize", size.x + " X " + size.y);
                map.put("OsVersion",Build.VERSION.RELEASE);
                map.put("AppVersion",versionName);
                map.put("Token",pref.getString("TokenId",""));
                map.put("CompanyId",Config.CompanyId);
                try {
                    map.put("IMEI", IMEI);
                }catch (Exception ex){
                    Crashlytics.logException(ex.getCause());
                }

                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request, "REQUEST_FOR_OTP");
    }

}
