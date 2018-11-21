package com.flowrithm.daangdiaries.Activity.Profile;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.flowrithm.daangdiaries.R;
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

public class Profile extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etContact)
    EditText etContact;

    @Bind(R.id.etAddress)
    EditText etAddress;

    @Bind(R.id.etCity)
    EditText etCity;

    @Bind(R.id.adView)
    AdView adView;

    @Bind(R.id.btnSave)
    Button btnSave;

    @Bind(R.id.etEmail)
    EditText etEmail;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        imgBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        pref = Application.getSharedPreferenceInstance();

        MobileAds.initialize(this, getResources().getString(R.string.AppIdAds));
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("3ACD51E3E5C1B5E51D476F1B02598BA6")
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.e("Error", "Ad Loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("Error", errorCode + "");
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
                Log.e("Error", "Ad Close");
            }
        });
        adView.loadAd(adRequest);

        GetProfile();
    }

    public void GetProfile() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.GET_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        etName.setText(json.getString("Name"));
                        etAddress.setText(json.getString("Address"));
                        etEmail.setText(json.getString("Email"));
                        etCity.setText(json.getString("City"));
                        etContact.setText(json.getString("ContactNo"));
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
                    Toast.makeText(Profile.this,
                            Profile.this.getString(R.string.error_network_timeout),
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
                map.put("UserId", pref.getString("UserId", ""));
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public void UpdateProfile() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.UPDATE_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Utils.ShowServiceDialog(Profile.this, json);
                    if (json.getInt("success") == 1) {
                        GetProfile();
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
                    Toast.makeText(Profile.this,
                            Profile.this.getString(R.string.error_network_timeout),
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
                map.put("UserId", pref.getString("UserId", ""));
                map.put("Name", etName.getText().toString());
                map.put("Address", etAddress.getText().toString());
                map.put("Email",etEmail.getText().toString());
                map.put("City", etCity.getText().toString());
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSave) {
            UpdateProfile();
        } else if (v.getId() == R.id.imgBack) {
            this.finish();
        }
    }
}
