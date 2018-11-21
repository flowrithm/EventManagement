package com.flowrithm.daangdiaries.Activity.Passes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Tabs.Passes;
import com.flowrithm.daangdiaries.Tabs.Tickets;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyPasses extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.tabs)
    TabLayout tabs;

    @Bind(R.id.viewpager)
    ViewPager viewpager;

    @Bind(R.id.imgHistory)
    ImageView imgHistory;

    MEvent event;
    SharedPreferences pref;

    String NoOfPasses = "", NoOfTickets = "", Date = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_passes);
        pref = Application.getSharedPreferenceInstance();
        ButterKnife.bind(this);
        event = (MEvent) getIntent().getSerializableExtra("Detail");
        imgBack.setOnClickListener(this);
        imgHistory.setOnClickListener(this);
        GetPasses();
    }

    public void GetPasses() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.GET_PASSES_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        NoOfPasses = json.getString("passes") == null ? "" : json.getString("passes");
                        NoOfTickets = json.getString("tickets") == null ? "" : json.getString("tickets");
                        Date = json.getString("Date");
                        setupViewPager(viewpager);
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
                    Toast.makeText(MyPasses.this,
                            MyPasses.this.getString(R.string.error_network_timeout),
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
                map.put("eventid", event.EventId + "");
                map.put("userid", pref.getString("UserId", ""));
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request, "Passes");
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(this.getSupportFragmentManager());

        Passes passes = new Passes();
        passes.setData(event);
        passes.setQty(NoOfPasses);

        Tickets tickets = new Tickets();
        tickets.setData(event);
        tickets.setQty(NoOfTickets);
        tickets.setDate(Date);

        adapter.addFragment(passes, "Passes");

        adapter.addFragment(tickets, "Tickets");

        viewPager.setAdapter(adapter);
        pref = Application.getSharedPreferenceInstance();
        viewPager.setOffscreenPageLimit(0);
        tabs.setupWithViewPager(viewPager);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            this.finish();
        }else if(v.getId()==R.id.imgHistory){
            Intent intent=new Intent(this,ReedemHistory.class);
            intent.putExtra("Detail",event);
            startActivity(intent);
        }
    }

}
