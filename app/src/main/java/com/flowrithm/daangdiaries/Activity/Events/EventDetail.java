package com.flowrithm.daangdiaries.Activity.Events;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
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
import com.flowrithm.daangdiaries.Activity.Registration.RegisterUser;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.Model.MTax;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Tabs.EventRegistration;
import com.flowrithm.daangdiaries.Tabs.EventTicket;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.flowrithm.daangdiaries.Api.WebApi.GET_EVENT_DETAIL;

public class EventDetail extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.viewpager)
    ViewPager viewpager;

    @Bind(R.id.tabs)
    TabLayout tabs;

    @Bind(R.id.imgEvent)
    ImageView imgEvent;

    @Bind(R.id.imgPricing)
    ImageView imgPricing;

    @Bind(R.id.imgScanning)
    ImageView imgScanning;

    @Bind(R.id.imgRegister)
    ImageView imgRegister;

    @Bind(R.id.imgBack)
    ImageView imgBack;

    MEvent event;
    boolean EventPassAvailable,EventTicketAvailable;
    SharedPreferences pref;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        pref = Application.getSharedPreferenceInstance();
        ButterKnife.bind(this);
        imgPricing.setOnClickListener(this);
        imgScanning.setOnClickListener(this);
        imgRegister.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        if (pref.getString("Role", "").equals("Admin")) {
            imgPricing.setVisibility(View.VISIBLE);
        }
        if(pref.getString("Role", "").equals("Admin") || pref.getString("Role", "").equals("Agent")){
            imgRegister.setVisibility(View.VISIBLE);
        }
        if (Application.getSharedPreferenceInstance().getInt("ScanningRights", 0) == 1 || pref.getString("Role", "").equals("Admin")) {
            imgScanning.setVisibility(View.VISIBLE);
        }
        event = (MEvent) getIntent().getSerializableExtra("Detail");
        Picasso.with(this).load(WebApi.DOMAIN + event.Image).into(imgEvent);

        qrScan = new IntentIntegrator(this);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        qrScan.setPrompt("Scan a QR Code Here");
        qrScan.setCameraId(0);  // Use a specific camera of the device
        qrScan.setBeepEnabled(true);
        qrScan.setOrientationLocked(false);
        qrScan.setBarcodeImageEnabled(true);

        GetEventDetail();
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(this.getSupportFragmentManager());

        com.flowrithm.daangdiaries.Tabs.EventDetail eventDetail = new com.flowrithm.daangdiaries.Tabs.EventDetail();
        eventDetail.setData(event);

        EventRegistration eventRegistration = new EventRegistration();
        eventRegistration.setData(event);

        EventTicket eventTicket = new EventTicket();
        eventTicket.setData(event);

        adapter.addFragment(eventDetail, "Details");

        adapter.addFragment(eventRegistration, "Passes");

        adapter.addFragment(eventTicket, "Tickets");

        viewPager.setAdapter(adapter);
        pref = Application.getSharedPreferenceInstance();
        viewPager.setOffscreenPageLimit(0);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgPricing) {
            Intent intent = new Intent(this, AddPricing.class);
            intent.putExtra("Detail", event);
            startActivityForResult(intent, 101);
        } else if (v.getId() == R.id.imgRegister) {
            Intent intent = new Intent(this, RegisterUser.class);
            intent.putExtra("Detail", event);
            startActivity(intent);
        } else if(v.getId()==R.id.imgBack){
            this.finish();
        }else {
            qrScan.initiateScan();
//            Intent intent = new Intent(this, ScanQRCode.class);
//            intent.putExtra("Detail", event);
//            startActivityForResult(intent, 101);
        }
    }

    public void GetEventDetail() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, GET_EVENT_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        event.EventId = json.getInt("EventId");
                        event.Name = json.getString("EventName");
                        event.StartDate = json.getString("StartDate");
                        event.EndDate = json.getString("EndDate");
                        event.Venue = json.getString("Venue");
                        event.ContactPerson = json.getString("ContactPerson");
                        event.ContactNumber = json.getString("ContactNumber");
                        event.Status = json.getInt("Status");
                        event.Image = json.getString("Image");
                        event.PassPrice = json.getString("PassPrice");
                        event.IsPassAvailable=json.getBoolean("DisplayPass");
                        event.IsTicketAvailable=json.getBoolean("DisplayTicket");
                        if(!event.IsTicketAvailable){
                            imgScanning.setVisibility(View.GONE);
                            imgRegister.setVisibility(View.GONE);
                            imgPricing.setVisibility(View.GONE);
                        }
                        ArrayList<MTax> taxes=new ArrayList<>();
                        JSONArray array=json.getJSONArray("IncludedTaxes");
                        if(array.length()>0){
                            for(int i=0;i<array.length();i++){
                                MTax tax=new MTax();
                                JSONObject jsontax=array.getJSONObject(i);
                                tax.Tax=jsontax.getString("Tax");
                                tax.TaxType=jsontax.getString("TaxName");
                                tax.TaxId=jsontax.getString("TaxId");
                                taxes.add(tax);
                            }
                        }
                        event.Tax=taxes;
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
                    Toast.makeText(EventDetail.this,
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
                map.put("eventid", event.EventId + "");
                map.put("CompanyId", Config.CompanyId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101) {
            GetEventDetail();
            EventDetail.this.setResult(RESULT_OK);
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ScanQRCode.class);
                intent.putExtra("QRCode", result.getContents());
                startActivityForResult(intent, 103);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 103 && resultCode == RESULT_OK) {
            qrScan.initiateScan();
        }

    }
}
