package com.flowrithm.daangdiaries.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.flowrithm.daangdiaries.Activity.Passes.TransactionDetail;
import com.flowrithm.daangdiaries.Activity.Registration.CheckOut;
import com.flowrithm.daangdiaries.Adapter.TransactionAdapter;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MTransaction;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrders extends Fragment implements AdapterView.OnItemClickListener {

    @Bind(R.id.lstOrders)
    ListView lstOrders;

    ArrayList<MTransaction> transactions;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_orders, container, false);
        pref=Application.getSharedPreferenceInstance();
        ButterKnife.bind(this,view);
        lstOrders.setOnItemClickListener(this);
        GetOrders();
        return view;
    }

    public void GetOrders(){
        final KProgressHUD progress=Utils.ShowDialog(this.getContext());
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.GET_ORDER_TRANSACTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    if(json.getInt("success")==1){
                        transactions=new ArrayList<>();
                        JSONArray array=json.getJSONArray("Data");
                        for(int i=0;i<array.length();i++){
                            JSONObject obj=array.getJSONObject(i);
                            MTransaction transaction=new MTransaction();
                            transaction.THId=obj.getString("THId");
                            transaction.TxnAmount=obj.getString("TxnAmount");
                            if(obj.getString("Status").contains("CANCEL_TRANSACTION")) {
                                transaction.CustomStatus = "Transaction Failed.";
                            }else{
                                transaction.CustomStatus = "Transaction Success.";
                            }
                            transaction.Status=obj.getString("Status");
                            SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            Date newDate=spf.parse(obj.getString("TxnDateTime"));
                            spf= new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss aaa");
                            transaction.TxnDateTime=spf.format(newDate);

                            transaction.EventName=obj.getString("EventName");
                            transactions.add(transaction);
                        }
                        TransactionAdapter transactionAdapter=new TransactionAdapter(MyOrders.this.getContext(),transactions);
                        lstOrders.setAdapter(transactionAdapter);
                    }
                }catch (Exception ex){
                    Crashlytics.logException(ex.fillInStackTrace());
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(MyOrders.this.getContext(),
                            MyOrders.this.getString(R.string.error_network_timeout),
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
                map.put("UserId",pref.getString("UserId","")+"");
                map.put("CompanyId",Config.CompanyId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(this.getContext(),TransactionDetail.class);
        intent.putExtra("Detail",transactions.get(position));
        startActivity(intent);
    }
}
