package com.flowrithm.daangdiaries.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.flowrithm.daangdiaries.Activity.Events.EventDetail;
import com.flowrithm.daangdiaries.Adapter.EventListAdapter;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.flowrithm.daangdiaries.Api.WebApi.GET_EVENTS;

public class Events extends Fragment implements AdapterView.OnItemClickListener {

    @Bind(R.id.lstEvents)
    ListView lstEvents;

    @Bind(R.id.adView)
    AdView adView;

    ArrayList<MEvent> events;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this,view);
        pref=Application.getSharedPreferenceInstance();
        lstEvents.setOnItemClickListener(this);
        GetEvents();
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
        return view;
    }

    public void GetEvents(){
        final KProgressHUD progress= Utils.ShowDialog(this.getContext());
        StringRequest request=new StringRequest(Request.Method.POST, GET_EVENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    if(json.getInt("success")==1){
                        events=new ArrayList<>();
                        JSONArray array=json.getJSONArray("Data");
                        for(int i=0;i<array.length();i++){
                            JSONObject obj=array.getJSONObject(i);
                            MEvent event=new MEvent();
                            event.EventId=obj.getInt("EventId");
                            event.Name=obj.getString("EventName");
                            Date Startdate=Utils.getDateFromString(obj.getString("StartDate"));
                            event.StartDate=Utils.ConvertDateFormate(Startdate).replace('-','\n');
                            if(!obj.getString("StartDate").equals(obj.getString("EndDate"))) {
                                Date EndDate = Utils.getDateFromString(obj.getString("EndDate"));
                                event.EndDate = Utils.ConvertDateFormate(EndDate).replace('-', '\n');
                            }else{
                                event.EndDate="";
                            }
                            event.Venue=obj.getString("Venue");
                            event.ContactPerson=obj.getString("ContactPerson");
                            event.ContactNumber=obj.getString("ContactNumber");
                            event.Status=obj.getInt("Status");
                            event.Image=obj.getString("Image");
                            event.PassPrice=obj.getString("PassPrice");
                            events.add(event);
                        }
                        EventListAdapter adapter=new EventListAdapter(Events.this.getActivity(),events);
                        lstEvents.setAdapter(adapter);
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
                    Toast.makeText(Events.this.getContext(),
                            Events.this.getString(R.string.error_network_timeout),
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
                map.put("CompanyId", Config.CompanyId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(Events.this.getContext(),EventDetail.class);
        intent.putExtra("Detail",events.get(position));
        startActivityForResult(intent,101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==101 && resultCode== Activity.RESULT_OK){
            GetEvents();
        }
    }
}
