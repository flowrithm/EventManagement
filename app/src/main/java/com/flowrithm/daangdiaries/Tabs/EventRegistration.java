package com.flowrithm.daangdiaries.Tabs;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.flowrithm.daangdiaries.Activity.Events.EventDetail;
import com.flowrithm.daangdiaries.Activity.Registration.CheckOut;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.Model.Paytm;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.google.gson.JsonObject;
import com.kaopiz.kprogresshud.KProgressHUD;

import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.flowrithm.daangdiaries.Api.WebApi.PURCHASE_TICKET;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventRegistration extends Fragment implements View.OnClickListener {

    @Bind(R.id.btnGrab)
    Button btnGrab;

    @Bind(R.id.imgMinus)
    ImageView imgMinus;

    @Bind(R.id.imgPlus)
    ImageView imgPlus;

    @Bind(R.id.txtQuantity)
    TextView txtQuantity;

    @Bind(R.id.txtPrice)
    TextView txtPrice;

    SharedPreferences pref;
    MEvent event;
    int Quantity = 1;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_registration, container, false);
        ButterKnife.bind(this, view);
        btnGrab.setOnClickListener(this);
        imgMinus.setOnClickListener(this);
        imgPlus.setOnClickListener(this);
        pref=Application.getSharedPreferenceInstance();
        if(event!=null) {
            if(!event.IsPassAvailable){
                btnGrab.setVisibility(View.GONE);
            }
            txtQuantity.setText(Quantity+"");
            txtPrice.setText(event.PassPrice + " Rs.");
        }
        return view;
    }

//    public String  GenerateCheckSum(){
//        TreeMap<String, String> paytmParams = new TreeMap<String, String>();
//        paytmParams.put("MID",merchantMid);
//        paytmParams.put("ORDER_ID",orderId);
//        paytmParams.put("CHANNEL_ID",channelId);
//        paytmParams.put("CUST_ID",custId);
//        paytmParams.put("MOBILE_NO",mobileNo);
//        paytmParams.put("EMAIL",email);
//        paytmParams.put("TXN_AMOUNT", (Double.parseDouble(event.PassPrice) * Quantity)+"");
//        paytmParams.put("WEBSITE",website);
//        paytmParams.put("INDUSTRY_TYPE_ID",industryTypeId);
//        paytmParams.put("CALLBACK_URL", callbackUrl);
//        try {
//            String paytmChecksum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(merchantKey, paytmParams);
//            return paytmChecksum;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }




    public void setData(MEvent event) {
        this.event = event;
    }

    public void setQuantity() {
        txtQuantity.setText(Quantity + "");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtQuantity) {

        } else if (v.getId() == R.id.imgPlus) {
            Quantity++;
        } else if (v.getId() == R.id.imgMinus) {
            if (Quantity != 1) {
                Quantity--;
            }
        }else if(v.getId()==R.id.btnGrab){
//            GenerateCheckSum();
            Intent intent=new Intent(this.getContext(),CheckOut.class);
            intent.putExtra("Detail",event);
            intent.putExtra("Quantity",Quantity+"");
            intent.putExtra("Type","Pass");
            intent.putExtra("Price",event.PassPrice);
            startActivity(intent);
        }
        setQuantity();
    }

}
