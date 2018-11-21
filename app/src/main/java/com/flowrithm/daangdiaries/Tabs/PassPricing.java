package com.flowrithm.daangdiaries.Tabs;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.flowrithm.daangdiaries.Activity.Events.AddPricing;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PassPricing extends Fragment implements View.OnClickListener {

    @Bind(R.id.etPrice)
    EditText etPrice;

    @Bind(R.id.btnSave)
    Button btnSave;

    MEvent event;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_pass_pricing, container, false);
        ButterKnife.bind(this,view);
        pref=Application.getSharedPreferenceInstance();
        btnSave.setOnClickListener(this);
        if(event!=null){
            etPrice.setText(event.PassPrice);
        }
        return view;
    }

    public void AddPrice(){
        final KProgressHUD progress= Utils.ShowDialog(this.getContext());
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.ADD_TICKET_PRICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    Utils.ShowServiceDialog(PassPricing.this.getContext(),json);
                    if(json.getInt("success")==1){
                        etPrice.setText(json.getString("Price")+"");
                        ((AddPricing)PassPricing.this.getActivity()).setResult(Activity.RESULT_OK);
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
                    Toast.makeText(PassPricing.this.getContext(),
                            PassPricing.this.getString(R.string.error_network_timeout),
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
                map.put("EventId",event.EventId+"");
                map.put("Type","Pass");
                map.put("Price",etPrice.getText().toString());
                map.put("UserId",pref.getString("UserId",""));
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request,"REQUEST_FOR_OTP");
    }

    public void setData(MEvent event) {
        this.event = event;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSave){
            AddPrice();
        }
    }
}
