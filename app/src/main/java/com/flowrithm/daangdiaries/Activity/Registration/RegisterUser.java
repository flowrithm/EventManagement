package com.flowrithm.daangdiaries.Activity.Registration;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Tabs.DynamicPass;
import com.flowrithm.daangdiaries.Tabs.DynamicTicket;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.etSearch)
    EditText etSearch;

    @Bind(R.id.btnSearch)
    Button btnSearch;

    @Bind(R.id.txtName)
    TextView txtName;

    @Bind(R.id.txtContact)
    TextView txtContact;

    @Bind(R.id.imgPlus)
    ImageView imgPlus;

    @Bind(R.id.imgMinus)
    ImageView imgMinus;

    @Bind(R.id.txtQuantity)
    TextView txtQuantity;

    @Bind(R.id.layoutDetail)
    LinearLayout layoutDetail;

    @Bind(R.id.btnRedeem)
    Button btnRedeem;

    @Bind(R.id.txtPrice)
    TextView txtPrice;

    @Bind(R.id.chkPayment)
    CheckBox chkPayment;

    @Bind(R.id.tabs)
    TabLayout tabs;

    @Bind(R.id.viewpager)
    ViewPager viewpager;

    MEvent event;
    int Quantity = 1;
    String UserId="",PassPrice="";
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        ButterKnife.bind(this);
        pref=Application.getSharedPreferenceInstance();
        event = (MEvent) getIntent().getSerializableExtra("Detail");

        imgBack.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnRedeem.setOnClickListener(this);
        imgPlus.setOnClickListener(this);
        imgMinus.setOnClickListener(this);
        chkPayment.setOnCheckedChangeListener(this);
    }

//    public void RegisterPass(){
//        final KProgressHUD progress=Utils.ShowDialog(this);
//        StringRequest request=new StringRequest(Request.Method.POST, WebApi.REGISTER_PASS_TICKET, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try{
//                    JSONObject json=new JSONObject(response);
//                    Utils.ShowServiceDialog(RegisterUser.this,json);
//                    if(json.getInt("success")==1){
//                        RegisterUser.this.setResult(RESULT_OK);
//                        RegisterUser.this.finish();
//                    }
//                }catch (Exception ex){
//                    Crashlytics.logException(ex.getCause());
//                }
//                progress.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                    Toast.makeText(RegisterUser.this,
//                            RegisterUser.this.getString(R.string.error_network_timeout),
//                            Toast.LENGTH_LONG).show();
//                } else if (error instanceof AuthFailureError) {
//                    //TODO
//                    Crashlytics.log(error.getMessage());
//                } else if (error instanceof ServerError) {
//                    //TODO
//                    Crashlytics.log(error.getMessage());
//                } else if (error instanceof NetworkError) {
//                    //TODO
//                    Crashlytics.log(error.getMessage());
//                } else if (error instanceof ParseError) {
//                    //TODO
//                    Crashlytics.log(error.getMessage());
//                }
//                progress.dismiss();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap map=new HashMap<String,String>();
//                map.put("UserId",UserId);
//                map.put("EventId",event.EventId+"");
//                map.put("Type","Pass");
//                map.put("Price",PassPrice);
//                map.put("CompanyId","1");
//                map.put("InsBy",pref.getString("UserId",""));
//                map.put("QTY",Quantity+"");
//                return map;
//            }
//        };
//        Application.getInstance().addToRequestQueue(request);
//    }

    public void GetActiveUser() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.GET_ACTIVE_USERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Utils.ShowServiceDialog(RegisterUser.this,json);
                    if (json.getInt("success") == 1) {
                        layoutDetail.setVisibility(View.VISIBLE);
                        txtName.setText(json.getString("Name"));
                        txtContact.setText(json.getString("ContactNo"));
                        UserId=json.getString("UserId");
                        PassPrice=event.PassPrice;
                        txtPrice.setText(event.PassPrice+" Rs.");
                        setupViewPager(viewpager);
                    }else{
                        layoutDetail.setVisibility(View.GONE);
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
                    Toast.makeText(RegisterUser.this,
                            RegisterUser.this.getString(R.string.error_network_timeout),
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
                map.put("ContactNo", etSearch.getText().toString());
                map.put("EventId", event.EventId + "");
                map.put("CompanyId", Config.CompanyId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(this.getSupportFragmentManager());

        DynamicPass passes = new DynamicPass();
        passes.setPassPrice(PassPrice);
        passes.setUserId(UserId);
        passes.setEvent(event);
        adapter.addFragment(passes, "Passes");

        DynamicTicket ticket=new DynamicTicket();
        ticket.setData(event);
        ticket.setUserId(UserId);
        adapter.addFragment(ticket, "Tickets");

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
        } else if (v.getId() == R.id.btnSearch) {
            GetActiveUser();
        } else if (v.getId() == R.id.imgMinus) {
            if (Quantity != 1) {
                Quantity--;
            }
            setQuantity();
        } else if (v.getId() == R.id.imgPlus) {
            Quantity++;
            setQuantity();
        } else if (v.getId() == R.id.btnRedeem) {
//            RegisterPass();
        }
    }

    public void setQuantity(){
        txtQuantity.setText(Quantity+"");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId()==R.id.chkPayment){
            if(isChecked){
                btnRedeem.setEnabled(true);
                btnRedeem.setBackgroundResource(R.drawable.background_blue_rounded);
            }else{
                btnRedeem.setEnabled(false);
                btnRedeem.setBackgroundResource(R.drawable.background_blue_blur_rounded);
            }
        }
    }
}
