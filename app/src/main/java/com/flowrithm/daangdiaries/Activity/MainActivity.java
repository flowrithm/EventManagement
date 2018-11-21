package com.flowrithm.daangdiaries.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.etMobileNo)
    EditText etMobileNo;

    @Bind(R.id.btnRequestOTP)
    Button btnRequestOTP;

    @Bind(R.id.btnRegister)
    Button btnRegister;

    public static String[] permission=
            {
                    "android.permission.WAKE_LOCK",
                    "android.permission.RECEIVE_SMS",
                    "android.permission.READ_SMS",
                    "android.permission.READ_PHONE_STATE"
            };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btnRequestOTP.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        if(CheckPermission(this)==PackageManager.PERMISSION_DENIED) {
            requestPermissions(permission, 101);
        }
    }

    public static int CheckPermission(Context context) {
        int ReceiveMessage = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        int ReadPhoneState = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        int ReadMessage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        int WakeLock = ContextCompat.checkSelfPermission(context, Manifest.permission.WAKE_LOCK);
        if (ReceiveMessage == PackageManager.PERMISSION_GRANTED && ReadPhoneState == PackageManager.PERMISSION_GRANTED
                && ReadMessage == PackageManager.PERMISSION_GRANTED && WakeLock == PackageManager.PERMISSION_GRANTED) {
            return PackageManager.PERMISSION_GRANTED;
        } else {
            return PackageManager.PERMISSION_DENIED;
        }
    }

    public void RequestForOTP(){
        final KProgressHUD progress= Utils.ShowDialog(this);
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.REQUEST_FOR_OTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    Utils.ShowServiceDialog(MainActivity.this,json);
                    if(json.getBoolean("success")){
                        Intent intent=new Intent(MainActivity.this,VerifyOTP.class);
                        intent.putExtra("OTP",json.getString("OTP"));
                        intent.putExtra("UserId",json.getString("UserId"));
                        intent.putExtra("MobileNo",etMobileNo.getText().toString());
                        startActivityForResult(intent,101);
                    }
                }catch (Exception ex){
                    Crashlytics.logException(ex.getCause());
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this,
                            MainActivity.this.getString(R.string.error_network_timeout),
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<>();
                map.put("ContactNo",etMobileNo.getText().toString());
                map.put("CompanyId", Config.CompanyId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request,"REQUEST_FOR_OTP");
    }

    public boolean Validaion(){
        boolean Success=true;
        if(!Utils.CheckEmptyValidation(this,etMobileNo)){
            return false;
        }
        return Success;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnRequestOTP){
            if(Validaion()) {
                RequestForOTP();
            }
        }else if(v.getId()==R.id.btnRegister){
            Intent intent=new Intent(this,UserRegistration.class);
            startActivityForResult(intent,102);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==101 && resultCode==RESULT_OK){
            this.finish();
        }else if(requestCode==102 && resultCode==RESULT_OK){
            this.finish();
        }
    }
}
