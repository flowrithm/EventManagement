package com.flowrithm.daangdiaries.Activity.Other;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.flowrithm.daangdiaries.Activity.Events.EventDetail;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaxationDetail extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.layoutInfo)
    LinearLayout layoutInfo;

    @Bind(R.id.btnOk)
    Button btnOk;

    LayoutInflater inflater;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxation_detail);
        ButterKnife.bind(this);
        btnOk.setOnClickListener(this);
        response=getIntent().getStringExtra("Json");
        inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ParsingDisplay();
    }

    public void ParsingDisplay(){
        try{
            JSONObject json=new JSONObject(response);
            if(json.getInt("success")==1){
                JSONArray array=json.getJSONArray("Data");
                for(int i=0;i<array.length();i++){
                    JSONObject obj=array.getJSONObject(i);
                    View view=inflater.inflate(R.layout.item_taxation_layout,null);

                    TextView txtTitle=view.findViewById(R.id.lblTaxType);
                    txtTitle.setText(obj.getString("TaxType") +" : ");

                    TextView txtNumber=view.findViewById(R.id.lblTaxNumber);
                    txtNumber.setText(obj.getString("TaxValue"));
                    layoutInfo.addView(view);
                }
            }
        }catch (Exception ex){
            Crashlytics.logException(ex.fillInStackTrace());
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnOk){
            this.finish();
        }
    }
}
