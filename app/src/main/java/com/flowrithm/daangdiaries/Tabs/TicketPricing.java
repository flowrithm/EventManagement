package com.flowrithm.daangdiaries.Tabs;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TicketPricing extends Fragment implements OnDateSelectedListener, View.OnClickListener {

    @Bind(R.id.calendarView)
    MaterialCalendarView calendar;

    @Bind(R.id.etPrice)
    EditText etPrice;

    @Bind(R.id.layoutAddPrice)
    LinearLayout layoutAddPrice;

    @Bind(R.id.btnSave)
    Button btnSave;

    MEvent event;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_ticket_pricing, container, false);
        ButterKnife.bind(this,view);
        calendar.setSelectedDate(Utils.getDateFromString(event.StartDate));
        pref=Application.getSharedPreferenceInstance();
        if(IsInBetween(calendar.getSelectedDate())){
            GetPrice(calendar.getSelectedDate());
            layoutAddPrice.setVisibility(View.VISIBLE);
        }else{
            layoutAddPrice.setVisibility(View.GONE);
        }
        btnSave.setOnClickListener(this);
//        GetPrice(calendar.getSelectedDate().getDate().toString());
        calendar.setOnDateChangedListener(this);
        return view;
    }

    public void GetPrice(final CalendarDay calendarDate){
        final KProgressHUD progress= Utils.ShowDialog(this.getContext());
        DateFormat dateformate = new SimpleDateFormat("dd/MM/yyyy");
        final String date = dateformate.format(calendarDate.getCalendar().getTime());
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.GET_EVENT_DATE_PRICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    Utils.ShowServiceDialog(TicketPricing.this.getContext(),json);
                    if(json.getInt("success")==1){
                        etPrice.setText(json.getString("Price")+"");
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
                    Toast.makeText(TicketPricing.this.getContext(),
                            TicketPricing.this.getString(R.string.error_network_timeout),
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

    public void setData(MEvent event) {
        this.event = event;
    }

    public void AddPrice(final CalendarDay calendarDate){
        final KProgressHUD progress= Utils.ShowDialog(this.getContext());
        DateFormat dateformate = new SimpleDateFormat("dd/MM/yyyy");
        final String date = dateformate.format(calendarDate.getCalendar().getTime());
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.ADD_TICKET_PRICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    Utils.ShowServiceDialog(TicketPricing.this.getContext(),json);
                    if(json.getInt("success")==1){
                        etPrice.setText(json.getString("Price")+"");
                        ((AddPricing)TicketPricing.this.getActivity()).setResult(Activity.RESULT_OK);
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
                    Toast.makeText(TicketPricing.this.getContext(),
                            TicketPricing.this.getString(R.string.error_network_timeout),
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
                map.put("Type","Event");
                map.put("Price",etPrice.getText().toString());
                map.put("Date",date);
                map.put("UserId",pref.getString("UserId",""));
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request,"REQUEST_FOR_OTP");
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        if(IsInBetween(date)){
            GetPrice(date);
            layoutAddPrice.setVisibility(View.VISIBLE);
        }else{
            layoutAddPrice.setVisibility(View.GONE);
        }
    }

    public boolean IsInBetween(CalendarDay date) {
        Date startDate=Utils.getDateFromString(event.StartDate);
        Date endDate=Utils.getDateFromString(event.EndDate);
        if (date.getCalendar().getTimeInMillis() >= startDate.getTime() && date.getCalendar().getTimeInMillis() <= endDate.getTime()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSave){
            if(!etPrice.getText().toString().equals("")){
                AddPrice(calendar.getSelectedDate());
            }else{
                etPrice.setError("Mandatory");
            }
        }
    }
}
