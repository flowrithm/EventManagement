package com.flowrithm.daangdiaries.Activity.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.flowrithm.daangdiaries.Activity.Profile.Profile;
import com.flowrithm.daangdiaries.Fragment.AboutUs;
import com.flowrithm.daangdiaries.Fragment.Blogs;
import com.flowrithm.daangdiaries.Fragment.ContactUs;
import com.flowrithm.daangdiaries.Fragment.Dashboard.AdminDashboard;
import com.flowrithm.daangdiaries.Fragment.Dashboard.AgentDashboard;
import com.flowrithm.daangdiaries.Fragment.Dashboard.VisitorDashboard;
import com.flowrithm.daangdiaries.Fragment.EventMaster;
import com.flowrithm.daangdiaries.Fragment.Events;
import com.flowrithm.daangdiaries.Activity.MainActivity;
import com.flowrithm.daangdiaries.Adapter.MenuAdapter;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Fragment.MyOrders;
import com.flowrithm.daangdiaries.Fragment.MyPasses;
import com.flowrithm.daangdiaries.Fragment.Users;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.google.android.gms.ads.*;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

import static com.flowrithm.daangdiaries.Api.WebApi.LOGOUT;

public class Home extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.lstMenu)
    ListView lstMenu;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    ArrayList<com.flowrithm.daangdiaries.Model.Menu> menuItems;
    MenuAdapter adapter;
    String CurrentTag="";
    DrawerLayout drawer;
    SharedPreferences pref;
    InterstitialAd adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, getResources().getString(R.string.AppIdAds));
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pref=Application.getSharedPreferenceInstance();
        ButterKnife.bind(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        menuItems = com.flowrithm.daangdiaries.Model.Menu.parseMenu(pref.getString("Role",""));
        adapter = new MenuAdapter(this, menuItems);
        lstMenu.setAdapter(adapter);
        lstMenu.setOnItemClickListener(this);
        lstMenu.performItemClick(null,2,0);


        adView = new InterstitialAd(this);
        adView.setAdUnitId(getString(R.string.InterstitialAdId));
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.e("Errro",errorCode+"");
            }

            @Override
            public void onAdOpened() {

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                Home.this.finish();
            }
        });
        AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                    .addTestDevice("3ACD51E3E5C1B5E51D476F1B02598BA6")
                .build();
        adView.loadAd(adRequest);

    }

    public void CallUserMaster(){
        lstMenu.performItemClick(null,5,0);
    }

    public void CallMyPasses(){
        lstMenu.performItemClick(null,3,0);
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        }else if(!CurrentTag.equals("Dashboard")){
//            lstMenu.performItemClick(null,0,0);
//        }else {
//            adView.show();
//        }
        this.finish();
    }

    public void Logout(){
        final KProgressHUD progress= Utils.ShowDialog(this);
        StringRequest request=new StringRequest(Request.Method.POST, LOGOUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Utils.ShowServiceDialog(Home.this,json);
                    if(json.getInt("success")==1){
                        pref.edit().clear().commit();
                        Intent intent=new Intent(Home.this, MainActivity.class);
                        startActivity(intent);
                        Home.this.setResult(RESULT_OK);
                        Home.this.finish();
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
                    Toast.makeText(Home.this,
                            Home.this.getString(R.string.error_network_timeout),
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
                map.put("UserId",pref.getString("UserId","")+"");
                map.put("CompanyId", Config.CompanyId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request,"AddUser");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        com.flowrithm.daangdiaries.Model.Menu menu = menuItems.get(position);
        Fragment fragment = null;
        switch (menu.Tag) {
            case "Dashboard":
                if(pref.getString("Role","").equals("Admin")) {
                    fragment = new AdminDashboard();
                }else  if(pref.getString("Role","").equals("Agent")) {
                    fragment=new AgentDashboard();
                }else if(pref.getString("Role","").equals("Visitor")){
                    fragment=new VisitorDashboard();
                }
                break;
            case "UserManagement":
                fragment = new Users();
                break;
            case "EventManagement":
                fragment=new EventMaster();
                break;
            case "Events":
                fragment = new Events();
                break;
            case "Profile":
                Intent intent=new Intent(this, Profile.class);
                startActivity(intent);
                break;
            case "MyPass":
                fragment = new MyPasses();
                break;
            case "MyOrders":
                fragment = new MyOrders();
                break;
            case "Blogs":
                fragment = new Blogs();
                break;
            case "AboutUs":
                fragment = new AboutUs();
                break;
            case "PoweredBy":
                fragment = new ContactUs();
                break;
            case "Logout":
                Logout();
                break;
        }
        if (!menu.Tag.equals("Profile") && !menu.Tag.equals("Voice") && !menu.Tag.equals("VoiceMessages")) {
            toolbar.setTitle(menu.MenuName);
            CurrentTag = menu.Tag;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        drawer.closeDrawer(GravityCompat.START);
    }


}
