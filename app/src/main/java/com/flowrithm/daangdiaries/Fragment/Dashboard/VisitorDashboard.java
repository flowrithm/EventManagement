package com.flowrithm.daangdiaries.Fragment.Dashboard;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.flowrithm.daangdiaries.Activity.Home.Home;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class VisitorDashboard extends Fragment implements View.OnClickListener {

    @Bind(R.id.txtRegisteredEvent)
    TextView txtRegisteredEvent;

    @Bind(R.id.txtUpcomingEvent)
    TextView txtUpcomingEvent;

    @Bind(R.id.txtAttendedEvents)
    TextView txtAttendedEvents;

    @Bind(R.id.adView)
    AdView adView;

    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_visitor_dashboard, container, false);
        ButterKnife.bind(this,view);
        pref=Application.getSharedPreferenceInstance();
        GetVisitorDashboard();

        MobileAds.initialize(this.getActivity(), getResources().getString(R.string.AppIdAds));
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("3ACD51E3E5C1B5E51D476F1B02598BA6")
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.e("Error","Ad Loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("Error",errorCode+"");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                Log.e("Error","Ad Close");
            }
        });
        adView.loadAd(adRequest);

        txtRegisteredEvent.setOnClickListener(this);
        return view;
    }

    public void GetVisitorDashboard(){
        final KProgressHUD progress= Utils.ShowDialog(this.getContext());
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.GET_Visitor_DASHBOARD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    if(json.getInt("success")==1){
                        txtRegisteredEvent.setText(json.getInt("RegisteredEvent")+"");
                        txtUpcomingEvent.setText(json.getInt("UpcomingEvent")+"");
                        txtAttendedEvents.setText(json.getInt("AttendedEvent")+"");
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
                    Toast.makeText(VisitorDashboard.this.getContext(),
                            VisitorDashboard.this.getContext().getString(R.string.error_network_timeout),
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
                map.put("UserId",pref.getString("UserId",""));
                map.put("CompanyId", Config.CompanyId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txtRegisteredEvent){
            ((Home)getActivity()).CallMyPasses();
        }
    }
}
