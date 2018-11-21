package com.flowrithm.daangdiaries.Fragment.Dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.flowrithm.daangdiaries.Activity.Home.Home;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AdminDashboard extends Fragment implements View.OnClickListener {

    @Bind(R.id.cardRegisteredUsers)
    LinearLayout cardRegisteredUsers;

    @Bind(R.id.cardVerificationRights)
    LinearLayout cardVerificationRights;

    @Bind(R.id.cardUserCreation)
    LinearLayout cardUserCreation;

    @Bind(R.id.cardActiveUsers)
    LinearLayout cardActiveUsers;

    @Bind(R.id.txtRegisteredUsers)
    TextView txtRegisteredUsers;

    @Bind(R.id.txtVerificationRights)
    TextView txtVerificationRights;

    @Bind(R.id.txtUserCreation)
    TextView txtUserCreation;

    @Bind(R.id.txtActiveUsers)
    TextView txtActiveUsers;

    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        ButterKnife.bind(this,view);
        pref=Application.getSharedPreferenceInstance();
        cardRegisteredUsers.setOnClickListener(this);
        cardVerificationRights.setOnClickListener(this);
        cardUserCreation.setOnClickListener(this);
        cardActiveUsers.setOnClickListener(this);
        GetActiveUsers();
        return view;
    }

    public void GetActiveUsers(){
        final KProgressHUD progress= Utils.ShowDialog(this.getContext());
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.GET_ADMIN_DASHBOARD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    if(json.getInt("success")==1){
                        txtRegisteredUsers.setText(json.getInt("RegisteredUsers")+"");
                        txtVerificationRights.setText(json.getInt("ScanningRights")+"");
                        txtActiveUsers.setText(json.getInt("ActiveUser")+"");
                        txtUserCreation.setText(json.getInt("UserCreation")+"");
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
                    Toast.makeText(AdminDashboard.this.getContext(),
                            AdminDashboard.this.getContext().getString(R.string.error_network_timeout),
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
                map.put("CompanyId", Config.CompanyId);
                map.put("UserId",pref.getString("UserId",""));
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.cardRegisteredUsers){
            ((Home)getActivity()).CallUserMaster();
        }else if(v.getId()==R.id.cardVerificationRights){
            ((Home)getActivity()).CallUserMaster();
        }else if(v.getId()==R.id.cardUserCreation){
            ((Home)getActivity()).CallUserMaster();
        }else if(v.getId()==R.id.cardActiveUsers){
            ((Home)getActivity()).CallUserMaster();
        }
    }
}
