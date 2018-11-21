package com.flowrithm.daangdiaries.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.flowrithm.daangdiaries.Listner.SmsItemListner;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Receiver.SmsReceiver;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VerifyOTP extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.etOTP)
    EditText etOTP;

    @Bind(R.id.btnResend)
    TextView btnResend;

    @Bind(R.id.lblMobileNo)
    TextView lblMobileNo;

    @Bind(R.id.btnVerify)
    Button btnVerify;

    @Bind(R.id.lblWaitMessage)
    TextView lblwaitMessage;

    String OTP;
    String UserId;

    public CountDownTimer timer;
    SharedPreferences pref;
    PackageInfo info;
    String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        ButterKnife.bind(this);
        pref = Application.getSharedPreferenceInstance();
        OTP = getIntent().getStringExtra("OTP");
        UserId = getIntent().getStringExtra("UserId");
        lblMobileNo.setText(getIntent().getStringExtra("MobileNo"));
        btnVerify.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        SmsReceiver.bindSmsListner(listner);
        startTimer();
    }

    SmsItemListner listner = new SmsItemListner() {
        @Override
        public void messageReceived(String Sender, String MessageText) {
            if (Sender.contains(getString(R.string.smsSender))) {
                Pattern numberPat = Pattern.compile("\\d+");
                Matcher matcher1 = numberPat.matcher(MessageText);
                if (matcher1.find()) {
                    String otpNumber = MessageText.replaceAll("\\D+", "");
                    etOTP.setText(otpNumber);
                    if (Validation()) {
                        AppLoginLog();
                    }
                }
            }
        }
    };

    //resend OTP Api Call
    public void ReSendOTP() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.REQUEST_FOR_OTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Utils.ShowServiceDialog(VerifyOTP.this, json);
                    if (json.getBoolean("success")) {
                        OTP = json.getString("OTP");
                        UserId = json.getString("UserId");
                        startTimer();
                    }
                } catch (Exception ex) {
                    Crashlytics.logException(ex.getCause());
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(VerifyOTP.this,
                            VerifyOTP.this.getString(R.string.error_network_timeout),
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
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("ContactNo", lblMobileNo.getText().toString());
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request, "REQUEST_FOR_OTP");
    }

    //App Login Logs OTP Api Call
    public void AppLoginLog() {

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final String IMEI = telephonyManager.getDeviceId();

        try {
            PackageManager manager = this.getPackageManager();
            info = manager.getPackageInfo(this.getPackageName(), 0);
            int versionCode = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            versionName = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ex) {
            Crashlytics.logException(ex.getCause());
        }

        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.App_LOGIN_LOG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Utils.ShowServiceDialog(VerifyOTP.this, json);
                    if (json.getBoolean("success")) {
                        SharedPreferences.Editor editor=pref.edit();
                        editor.putString("UserId",json.getString("UserId"));
                        editor.putInt("UserCreationRights",Integer.parseInt(json.getString("UserCreationRights")));
                        editor.putInt("ScanningRights",Integer.parseInt(json.getString("ScanningRights")));
                        editor.putString("Role",json.getString("Role"));
                        editor.commit();
                        Intent intent=new Intent(VerifyOTP.this, Home.class);
                        startActivity(intent);
                        VerifyOTP.this.setResult(RESULT_OK);
                        VerifyOTP.this.finish();
                    }
                } catch (Exception ex) {
                    Crashlytics.logException(ex.getCause());
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(VerifyOTP.this,
                            VerifyOTP.this.getString(R.string.error_network_timeout),
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
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("UserId", UserId);
                map.put("DeviceName", Build.DEVICE);
                map.put("DeviceModel", Build.MODEL);
                map.put("OS","Android");
                Point size = Utils.GetScreenSize(VerifyOTP.this);
                map.put("ScreenSize", size.x + " X " + size.y);
                map.put("OsVersion",Build.VERSION.RELEASE);
                map.put("AppVersion",versionName);
                map.put("Token",pref.getString("TokenId",""));

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

    public void startTimer(){
        timer=new CountDownTimer(180000, 1000) {

            public void onTick(long millisUntilFinished) {

                String text = String.format(Locale.getDefault(), "You can Resend OTP in  %02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                lblwaitMessage.setText(text);
                btnResend.setEnabled(false);
                btnResend.setBackgroundResource(R.drawable.background_blue_blur_rounded);
            }

            public void onFinish() {
                if(lblwaitMessage!=null) {
                    lblwaitMessage.setVisibility(View.GONE);
                    btnResend.setBackgroundResource(R.drawable.background_blue_rounded);
                    btnResend.setEnabled(true);
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    public boolean Validation(){
        boolean success=true;
        if(!OTP.equals(etOTP.getText().toString())){
            success=false;
        }
        return success;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnVerify) {
            if(Validation()){
                AppLoginLog();
            }
        } else if (v.getId() == R.id.btnResend) {
            ReSendOTP();
        }
    }
}
