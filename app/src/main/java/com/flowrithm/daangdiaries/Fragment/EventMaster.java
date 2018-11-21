package com.flowrithm.daangdiaries.Fragment;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.flowrithm.daangdiaries.Activity.Events.AddEvent;
import com.flowrithm.daangdiaries.Adapter.EventAdapter;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.flowrithm.daangdiaries.Api.WebApi.GET_EVENTS;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventMaster extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    @Bind(R.id.lstEvents)
    ListView lstEvents;

    @Bind(R.id.btnAdd)
    FloatingActionButton btnAdd;

    ArrayList<MEvent> events;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_event_master, container, false);
        ButterKnife.bind(this,view);
        pref=Application.getSharedPreferenceInstance();
        lstEvents.setOnItemClickListener(this);
        btnAdd.setOnClickListener(this);
        GetEvents();
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
                            event.StartDate=obj.getString("StartDate");
                            event.EndDate=obj.getString("EndDate");
                            event.Venue=obj.getString("Venue");
                            event.ContactPerson=obj.getString("ContactPerson");
                            event.ContactNumber=obj.getString("ContactNumber");
                            event.Status=obj.getInt("Status");
                            event.Image=obj.getString("Image");
                            event.PassPrice=obj.getString("PassPrice");
                            events.add(event);
                        }
                        EventAdapter adapter=new EventAdapter(EventMaster.this.getContext(),events);
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
                    Toast.makeText(EventMaster.this.getContext(),
                            EventMaster.this.getString(R.string.error_network_timeout),
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
        Intent intent=new Intent(EventMaster.this.getContext(),AddEvent.class);
        intent.putExtra("Detail",events.get(position));
        startActivityForResult(intent,101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==101 && resultCode== Activity.RESULT_OK){
            GetEvents();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnAdd){
            Intent intent=new Intent(this.getContext(), AddEvent.class);
            startActivityForResult(intent,101);
        }
    }
}
