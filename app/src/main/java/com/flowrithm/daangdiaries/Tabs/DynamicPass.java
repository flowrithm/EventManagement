package com.flowrithm.daangdiaries.Tabs;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class DynamicPass extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.imgPlus)
    ImageView imgPlus;

    @Bind(R.id.imgMinus)
    ImageView imgMinus;

    @Bind(R.id.txtQuantity)
    TextView txtQuantity;

    @Bind(R.id.btnRedeem)
    Button btnRedeem;

    @Bind(R.id.txtPrice)
    TextView txtPrice;

    @Bind(R.id.chkPayment)
    CheckBox chkPayment;

    String Price;
    int Quantity = 1;
    SharedPreferences pref;
    MEvent event;
    String UserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dynamic_pass, container, false);
        ButterKnife.bind(this,view);
        if(event!=null){
            if(!event.IsPassAvailable){
                btnRedeem.setVisibility(View.GONE);
            }
        }
        pref=Application.getSharedPreferenceInstance();
        txtPrice.setText(Price);
        imgPlus.setOnClickListener(this);
        btnRedeem.setOnClickListener(this);
        imgMinus.setOnClickListener(this);
        chkPayment.setOnCheckedChangeListener(this);
        return view;
    }

    public void setPassPrice(String Price){
        this.Price=Price;
    }

    public void RegisterPass(){
        final KProgressHUD progress= Utils.ShowDialog(this.getContext());
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.REGISTER_PASS_TICKET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    Utils.ShowServiceDialog(DynamicPass.this.getContext(),json);
                    if(json.getInt("success")==1){
                        DynamicPass.this.getActivity().setResult(RESULT_OK);
                        DynamicPass.this.getActivity().recreate();
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
                    Toast.makeText(DynamicPass.this.getContext(),
                            DynamicPass.this.getString(R.string.error_network_timeout),
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
                HashMap map=new HashMap<String,String>();
                map.put("UserId",UserId);
                map.put("EventId",event.EventId+"");
                map.put("EventName",event.Name);
                map.put("Type","Pass");
                map.put("Price",Price);
                map.put("CompanyId", Config.CompanyId);
                map.put("InsBy",pref.getString("UserId",""));
                map.put("QTY",Quantity+"");
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public void setQuantity(){
        txtQuantity.setText(Quantity+"");
    }

    public void setEvent(MEvent event){
          this.event=event;
    }

    public void setUserId(String UserId){
        this.UserId=UserId;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgMinus) {
            if (Quantity != 1) {
                Quantity--;
            }
            setQuantity();
        } else if (v.getId() == R.id.imgPlus) {
            Quantity++;
            setQuantity();
        } else if (v.getId() == R.id.btnRedeem) {
            RegisterPass();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId()==R.id.chkPayment){
            if(isChecked){
                btnRedeem.setEnabled(true);
                btnRedeem.setBackgroundResource(R.drawable.background_blue_rounded);
            }else{
                btnRedeem.setEnabled(false);
                btnRedeem.setBackgroundResource(R.drawable.background_blue_blur_rounded);
            }
        }
    }
}
