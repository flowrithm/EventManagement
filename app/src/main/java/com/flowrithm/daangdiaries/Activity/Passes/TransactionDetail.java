package com.flowrithm.daangdiaries.Activity.Passes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.flowrithm.daangdiaries.Model.MTransaction;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TransactionDetail extends AppCompatActivity {

    @Bind(R.id.txtEventName)
    TextView txtEventName;

    @Bind(R.id.txtOrderId)
    TextView txtOrderId;

    @Bind(R.id.txtDate)
    TextView txtDate;

    @Bind(R.id.txtAmount)
    TextView txtAmount;

    @Bind(R.id.txtStatus)
    TextView txtStatus;

    @Bind(R.id.layoutOrder)
    LinearLayout layoutOrder;

    @Bind(R.id.txtTypeDetail)
    TextView txtTypeDetail;

    @Bind(R.id.txtPrice)
    TextView txtPrice;

    @Bind(R.id.txtQty)
    TextView txtQty;

    MTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        ButterKnife.bind(this);
        transaction = (MTransaction) getIntent().getSerializableExtra("Detail");
        GetTransactionDetail();
    }

    public void GetTransactionDetail() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.GET_ORDER_TRANSACTION_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        txtEventName.setText(json.getString("EventName"));
                        txtOrderId.setText(json.getString("OrderId"));
                        txtAmount.setText(json.getString("TxnAmount") +" Rs.");
                        txtStatus.setText(json.getString("UserStatus"));
                        if (json.getString("UserStatus").equals("TXN_SUCCESS")) {
                            layoutOrder.setBackgroundResource(R.drawable.background_green_top_rounded);
                            txtStatus.setText("Transaction Success.");
                        } else {
                            layoutOrder.setBackgroundResource(R.drawable.background_red_top_rounded);
                            txtStatus.setText("Transaction Failed.");
                        }

                        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        Date newDate=spf.parse(json.getString("TxnDate"));
                        spf= new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss aaa");

                        txtDate.setText(spf.format(newDate));
                        txtTypeDetail.setText(json.getString("Type")+" Detail");
                        txtPrice.setText(json.getString("Price") +" Rs.");
                        txtQty.setText(json.getString("Qty"));
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
                    Toast.makeText(TransactionDetail.this,
                            TransactionDetail.this.getString(R.string.error_network_timeout),
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
                map.put("THid",transaction.THId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

}
