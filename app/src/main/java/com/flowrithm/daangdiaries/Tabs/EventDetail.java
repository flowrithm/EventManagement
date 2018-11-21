package com.flowrithm.daangdiaries.Tabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.flowrithm.daangdiaries.Api.WebApi.GET_ISSUED_PASSES;

public class EventDetail extends Fragment {

    @Bind(R.id.txtTicketCount)
    TextView txtTicketCount;

    @Bind(R.id.txtContact)
    TextView txtContact;

    @Bind(R.id.txtVenue)
    TextView txtVenue;

    @Bind(R.id.txtDate)
    TextView txtDate;

    @Bind(R.id.txtEventName)
    TextView txtEventName;


    MEvent event;
    SharedPreferences pref;
    boolean IsVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);
        ButterKnife.bind(this, view);
        pref=Application.getSharedPreferenceInstance();
        if (event != null) {
            txtEventName.setText(event.Name);
            pref = Application.getSharedPreferenceInstance();
            txtContact.setText(event.ContactNumber);
            txtDate.setText(event.StartDate + " - " + event.EndDate);
            txtVenue.setText(event.Venue);
        }
        GetIssuedPasses();
        return view;
    }


//    @Override
//    public void setMenuVisibility(boolean menuVisible) {
//        super.setMenuVisibility(menuVisible);
//        if (menuVisible && isResumed()) {
//            GetIssuedPasses();
//        } else {
//            IsVisible = false;
//        }
//    }

    public void GetIssuedPasses() {
        final KProgressHUD progress = Utils.ShowDialog(this.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, GET_ISSUED_PASSES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        txtTicketCount.setText(json.getInt("issuedPasses") + "");
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
                    Toast.makeText(EventDetail.this.getContext(),
                            EventDetail.this.getString(R.string.error_network_timeout),
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
                map.put("userid", pref.getString("UserId", ""));
                map.put("EventId", event.EventId + "");
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public void setData(MEvent event) {
        this.event = event;
    }

}
