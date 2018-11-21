package com.flowrithm.daangdiaries.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.flowrithm.daangdiaries.Activity.Other.TaxationDetail;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutUs extends Fragment implements View.OnClickListener {

    @Bind(R.id.imgFlowrithm)
    ImageView imgFlowrithm;

    @Bind(R.id.imgTaxation)
    ImageView imgTaxation;

    String Json="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_about_us, container, false);
        ButterKnife.bind(this,view);
        imgFlowrithm.setOnClickListener(this);
        imgTaxation.setOnClickListener(this);
        GetTaxationDetail();
        return view;
    }

    public void GetTaxationDetail(){
        final KProgressHUD progress=Utils.ShowDialog(this.getContext());
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.GET_TAXATION_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Json=response;
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(AboutUs.this.getContext(),
                            AboutUs.this.getString(R.string.error_network_timeout),
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
                map.put("CompanyId",Config.CompanyId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imgFlowrithm){
            Utils.OpenBrowser(this.getContext(),"https://flowrithmsolution.com");
        }else if(v.getId()==R.id.imgTaxation){
            try {
                JSONObject json = new JSONObject(Json);
                if (!Json.equals("") && json.getInt("success")==1) {
                    Intent intent = new Intent(this.getContext(), TaxationDetail.class);
                    intent.putExtra("Json",Json);
                    startActivity(intent);
                }
            }catch (Exception ex){
                Crashlytics.logException(ex.fillInStackTrace());
            }
        }
    }
}
