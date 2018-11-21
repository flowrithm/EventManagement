package com.flowrithm.daangdiaries.Tabs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import com.flowrithm.daangdiaries.Activity.Registration.CheckOut;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.flowrithm.daangdiaries.Api.WebApi.PURCHASE_TICKET;


public class EventTicket extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    @Bind(R.id.txtDate)
    TextView txtDate;

    @Bind(R.id.txtPrice)
    TextView txtPrice;

    @Bind(R.id.txtQuantity)
    TextView txtQuantity;

    @Bind(R.id.imgMinus)
    ImageView imgMinus;

    @Bind(R.id.imgPlus)
    ImageView imgPlus;

    @Bind(R.id.btnGrab)
    Button btnGrab;

    @Bind(R.id.layoutTicketBuy)
    LinearLayout layoutTicketBuy;

    @Bind(R.id.layoutGrabPass)
    LinearLayout layoutGrabPass;

    @Bind(R.id.txtNote)
    TextView txtNote;

    MEvent event;
    int Quantity = 0;
    String Price="";

    public boolean IsVisible = false;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_ticket, container, false);
        ButterKnife.bind(this, view);
        btnGrab.setOnClickListener(this);
        imgMinus.setOnClickListener(this);
        imgPlus.setOnClickListener(this);
        txtDate.setOnClickListener(this);
        pref=Application.getSharedPreferenceInstance();
//        if(IsVisible){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date=new Date();
        txtDate.setText(Utils.ConvertDateFormate(date));
        GetLatestTicket(sdf.format(date));
        if(event!=null){
            if(!event.IsTicketAvailable){
                btnGrab.setVisibility(View.GONE);
            }
        }
//        }
        return view;
    }

//    @Override
//    public void setMenuVisibility(boolean menuVisible) {
//        super.setMenuVisibility(menuVisible);
//        if(menuVisible && isResumed()){
//           IsVisible=true;
//        }else{
//            IsVisible=false;
//        }
//    }

    public void GetLatestTicket(final String Date) {
        final KProgressHUD progress = Utils.ShowDialog(this.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.GET_EVENT_DATE_PRICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("Price") == 0) {
                        layoutTicketBuy.setVisibility(View.VISIBLE);
                        layoutGrabPass.setVisibility(View.GONE);
                        txtNote.setVisibility(View.GONE);
                        txtPrice.setText(json.getString("message"));
                    } else {
                        layoutGrabPass.setVisibility(View.VISIBLE);
                        Price=json.getString("Price");
                        txtPrice.setText(json.getString("Price") + " Rs.");
                        txtNote.setVisibility(View.VISIBLE);
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
                    Toast.makeText(EventTicket.this.getContext(),
                            EventTicket.this.getString(R.string.error_network_timeout),
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
                map.put("EventId", event.EventId + "");
                map.put("Date",Date+"");
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public void setData(MEvent event) {
        this.event = event;
    }

    public void setQuantity() {
        txtQuantity.setText(Quantity + "");
    }

    public String GetSelectedDate(){
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
        Date date=null;
        try {
            date = format.parse(txtDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String SelectedDate=sdf.format(date);
        return SelectedDate;
    }

    public void GrabPass(){
        final KProgressHUD progress = Utils.ShowDialog(this.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, PURCHASE_TICKET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        Toast.makeText(EventTicket.this.getContext(),json.getString("message"),Toast.LENGTH_SHORT).show();
                        ((com.flowrithm.daangdiaries.Activity.Events.EventDetail)EventTicket.this.getActivity()).setResult(Activity.RESULT_OK);
                        ((EventDetail)EventTicket.this.getActivity()).finish();
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
                    Toast.makeText(EventTicket.this.getContext(),
                            EventTicket.this.getString(R.string.error_network_timeout),
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
                map.put("UserId", pref.getString("UserId",""));
                map.put("EventId",event.EventId+"");
                map.put("EventName",event.Name);
                map.put("CompanyId", Config.CompanyId);
                map.put("Type","Ticket");
                map.put("SelectedDate",GetSelectedDate());
                map.put("Price",Price);
                map.put("InsBy",pref.getString("UserId",""));
                map.put("QTY",Quantity+"");
                map.put("TransationId","TEST");
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnGrab) {
//            GrabPass();
            Intent intent=new Intent(this.getContext(),CheckOut.class);
            intent.putExtra("Detail",event);
            intent.putExtra("Quantity",Quantity+"");
            intent.putExtra("Type","Pass");
            intent.putExtra("Price",Price);
            intent.putExtra("Date",GetSelectedDate());
            startActivity(intent);
        } else if (v.getId() == R.id.imgPlus) {
            Quantity++;
        } else if (v.getId() == R.id.imgMinus) {
            if (Quantity != 1) {
                Quantity--;
            }
        } else if (v.getId() == R.id.txtDate) {
            Utils.PickDateIntoTextView(this.getContext(), this, event.StartDate, event.EndDate);
        }
        setQuantity();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = Utils.formatDate(year, month, dayOfMonth);
        txtDate.setText(date);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, dayOfMonth);
        Date date1 = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        GetLatestTicket(sdf.format(date1));

    }
}