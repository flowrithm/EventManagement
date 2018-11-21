package com.flowrithm.daangdiaries.Tabs;


import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class DynamicTicket extends Fragment implements View.OnClickListener,DatePickerDialog.OnDateSetListener, CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.txtDate)
    TextView txtDate;

    @Bind(R.id.txtPrice)
    TextView txtPrice;

    @Bind(R.id.imgPlus)
    ImageView imgPlus;

    @Bind(R.id.imgMinus)
    ImageView imgMinus;

    @Bind(R.id.chkPayment)
    CheckBox chkPayment;

    @Bind(R.id.txtQuantity)
    TextView txtQuantity;

    @Bind(R.id.btnRedeem)
    Button btnRedeem;

    MEvent event;
    String UserId,Price;
    int Quantity = 1;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dynamic_ticket, container, false);
        ButterKnife.bind(this,view);
        pref=Application.getSharedPreferenceInstance();
        btnRedeem.setOnClickListener(this);
        imgMinus.setOnClickListener(this);
        imgPlus.setOnClickListener(this);
        txtDate.setOnClickListener(this);

        if(event!=null){
            if(!event.IsTicketAvailable){
                btnRedeem.setVisibility(View.GONE);
            }
        }

        chkPayment.setOnCheckedChangeListener(this);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        GetPrice( sdf.format(date));
        txtDate.setText(Utils.formatDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)));

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnRedeem){
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date date=null;
            try {
                date = format.parse(txtDate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            RegisterPass(sdf.format(date));
        }else if(v.getId()==R.id.imgMinus){
            if (Quantity != 1) {
                Quantity--;
            }
            setQuantity();
        }else if(v.getId()==R.id.imgPlus){
            Quantity++;
            setQuantity();
        }else if(v.getId()==R.id.txtDate){
            Utils.PickDateIntoTextView(this.getContext(), DynamicTicket.this,txtDate,txtDate.getText().toString(),false);
        }
    }

    public void setQuantity(){
        txtQuantity.setText(Quantity+"");
    }

    public void GetPrice(final String calendarDate){
        final KProgressHUD progress= Utils.ShowDialog(this.getContext());
        DateFormat dateformate = new SimpleDateFormat("dd/MM/yyyy");
        final String date = dateformate.format(Utils.getDateFromString(calendarDate));
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.GET_EVENT_DATE_PRICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    Utils.ShowServiceDialog(DynamicTicket.this.getContext(),json);
                    if(json.getInt("success")==1){
                        Price=json.getString("Price");
                        txtPrice.setText(json.getString("Price")+" Rs.");
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
                    Toast.makeText(DynamicTicket.this.getContext(),
                            DynamicTicket.this.getString(R.string.error_network_timeout),
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
                map.put("Date",date);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request,"REQUEST_FOR_OTP");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, dayOfMonth);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        GetPrice( sdf.format(date));
        txtDate.setText(Utils.formatDate(year,month,dayOfMonth));
    }

    public void setUserId(String UserId){
        this.UserId=UserId;
    }

    public void setData(MEvent event){
        this.event=event;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId()==R.id.chkPayment){
            if(isChecked && !Price.equals("0")){
                btnRedeem.setBackgroundResource(R.drawable.background_blue_rounded);
                btnRedeem.setEnabled(true);
            }else{
                btnRedeem.setBackgroundResource(R.drawable.background_blue_blur_rounded);
                btnRedeem.setEnabled(false);
            }
        }
    }

    public void RegisterPass(final String Date){
        final KProgressHUD progress= Utils.ShowDialog(this.getContext());
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.REGISTER_PASS_TICKET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    Utils.ShowServiceDialog(DynamicTicket.this.getContext(),json);
                    if(json.getInt("success")==1){
                        DynamicTicket.this.getActivity().setResult(RESULT_OK);
                        DynamicTicket.this.getActivity().finish();
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
                    Toast.makeText(DynamicTicket.this.getContext(),
                            DynamicTicket.this.getString(R.string.error_network_timeout),
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
                map.put("Type","Ticket");
                map.put("Price",Price);
                map.put("CompanyId", Config.CompanyId);
                map.put("SelectedDate",Date);
                map.put("InsBy",pref.getString("UserId",""));
                map.put("QTY",Quantity+"");
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

}
