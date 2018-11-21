package com.flowrithm.daangdiaries.Activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class UserRegistration extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.btnRegister)
    Button btnRegister;

    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etContact)
    EditText etContact;

    @Bind(R.id.etAddress)
    EditText etAddress;

    @Bind(R.id.etCity)
    EditText etCity;

    @Bind(R.id.etEmail)
    EditText etEmail;

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        btnRegister.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    public void CheckMobileNumbr(){
        final KProgressHUD progress=Utils.ShowDialog(this);
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.CHECK_MOBILE_NUMBER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    Utils.ShowServiceDialog(UserRegistration.this,json);
                    if(json.getString("success").equals("1")){
                        Intent intent=new Intent(UserRegistration.this,MobileVerification.class);
                        intent.putExtra("OTP",json.getString("OTP"));
                        intent.putExtra("Name",etName.getText().toString());
                        intent.putExtra("ContactNo",etContact.getText().toString());
                        intent.putExtra("Address",etAddress.getText().toString());
                        intent.putExtra("City",etCity.getText().toString());
                        intent.putExtra("Email",etEmail.getText().toString());
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
                    Toast.makeText(UserRegistration.this,
                            UserRegistration.this.getString(R.string.error_network_timeout),
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
                map.put("ContactNo",etContact.getText().toString());
                map.put("CompanyId", Config.CompanyId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }



    public boolean Validation(){
        boolean Success=true;
        if(!Utils.CheckEmptyValidation(this,etName)){
            Success=false;
        }
        if(!Utils.CheckEmptyValidation(this,etContact)){
            Success=false;
        }
        if(etContact.getText().toString().length()!=10){
           Success=false;
           etContact.setError("Enter Valid MobileNo");
        }else{
            etContact.setError(null);
        }
        if(!etEmail.getText().toString().equals("")) {
            if(Utils.CheckEmailValidation(this,etEmail)){
                Success=false;
                etEmail.setError("Enter Valid Email");
            }
        }
        return Success;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnRegister){
            if(Validation()) {
                CheckMobileNumbr();
            }
        }else if(v.getId()==R.id.imgBack){
            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==101 && resultCode==RESULT_OK){
            this.setResult(RESULT_OK);
            this.finish();
        }
    }
}
