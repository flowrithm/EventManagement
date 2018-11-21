package com.flowrithm.daangdiaries.Activity.Passes;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.flowrithm.daangdiaries.Adapter.RedeemHistoryAdapter;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.Model.MReedem;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReedemHistory extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.lstTransaction)
    ListView lstTransaction;

    SharedPreferences pref;
    ArrayList<MReedem> reedems;
    MEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reedem_history);
        ButterKnife.bind(this);
        event=(MEvent) getIntent().getSerializableExtra("Detail");
        imgBack.setOnClickListener(this);
        pref=Application.getSharedPreferenceInstance();
        GetHistory();
    }

    public void GetHistory(){
        final KProgressHUD progress= Utils.ShowDialog(this);
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.GET_REDEEM_HISTORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    if(json.getInt("success")==1){
                        reedems=new ArrayList<>();
                        JSONArray array=json.getJSONArray("Data");
                        for(int i=0;i<array.length();i++){
                            JSONObject obj=array.getJSONObject(i);
                            MReedem reedem=new MReedem();
                            reedem.EventName=obj.getString("EventName");
                            reedem.DateTime=obj.getString("DateTime");
                            reedem.Type=obj.getString("Type");
                            reedem.Qty=obj.getString("Qty");
                            reedems.add(reedem);
                        }
                        RedeemHistoryAdapter adapter=new RedeemHistoryAdapter(ReedemHistory.this,reedems);
                        lstTransaction.setAdapter(adapter);
                    }
                }catch (Exception ex){
                    Crashlytics.logException(ex);
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ReedemHistory.this,
                            ReedemHistory.this.getString(R.string.error_network_timeout),
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
                map.put("userid",pref.getString("UserId",""));
                map.put("EventId",event.EventId+"");
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imgBack){
            this.finish();
        }
    }

}
