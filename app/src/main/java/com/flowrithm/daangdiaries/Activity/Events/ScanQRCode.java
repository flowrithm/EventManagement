package com.flowrithm.daangdiaries.Activity.Events;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScanQRCode extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.btnRedeem)
    Button btnRedeem;

    @Bind(R.id.imgMinus)
    ImageView imgMinus;

    @Bind(R.id.imgPlus)
    ImageView imgPlus;

    @Bind(R.id.txtName)
    TextView txtName;

    @Bind(R.id.txtTicketCount)
    TextView txtTicketCount;

    @Bind(R.id.txtContact)
    TextView txtContact;

    @Bind(R.id.txtQuantity)
    TextView txtQuantity;

    @Bind(R.id.txtPassRemaining)
    TextView txtPassRemaining;

    @Bind(R.id.txtPassIssued)
    TextView txtPassIssued;

    @Bind(R.id.txtDate)
    TextView txtDate;

    int Quantity = 1, NoOfTicket = 0, IssueTicket = 0, RemainingTicket = 0;
    SharedPreferences pref;
    String QRCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);
        ButterKnife.bind(this);
        pref = Application.getSharedPreferenceInstance();
        QRCode = getIntent().getStringExtra("QRCode");
        GetDetail(QRCode);
        imgPlus.setOnClickListener(this);
        imgMinus.setOnClickListener(this);
        btnRedeem.setOnClickListener(this);
    }

    public void RedeemQRCode() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.REDEEM_PASS_TICKET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        ScanQRCode.this.setResult(RESULT_OK);
                        ScanQRCode.this.finish();
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
                    Toast.makeText(ScanQRCode.this,
                            ScanQRCode.this.getString(R.string.error_network_timeout),
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
                map.put("QRCode", QRCode);
                map.put("QTY", Quantity + "");
                map.put("InsBy", pref.getString("UserId", ""));
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public void GetDetail(final String QRCode) {
        final String[] data = QRCode.split("_");
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.VERIFY_PASS_TICKET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        txtName.setText(json.getString("Name"));
                        txtContact.setText(json.getString("ContactNo"));
                        NoOfTicket = Integer.parseInt(json.getString("NoOfTicket"));
                        IssueTicket = Integer.parseInt(json.getString("IssueTicket"));
                        if (data[0].equals("pass")) {
                            RemainingTicket = NoOfTicket - IssueTicket;
                            txtTicketCount.setText(json.getString("NoOfTicket") + " Passes");
                        } else if (data[0].equals("ticket")) {
                            RemainingTicket = NoOfTicket;
                            txtTicketCount.setText(json.getString("NoOfTicket") + " Tickets");
                        }

                        DateFormat dateformate = new SimpleDateFormat("dd-MMM-yyyy");
                        final String date = dateformate.format(Utils.getDateFromString(json.getString("Date")));

                        txtDate.setText(date);

                        txtPassRemaining.setText(RemainingTicket+"\n Remaining");

                        txtPassIssued.setText(IssueTicket +"\n Issued");

                        if (RemainingTicket == 0) {
                            btnRedeem.setBackgroundResource(R.drawable.background_blue_blur_rounded);
                            btnRedeem.setEnabled(false);
                        }
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
                    Toast.makeText(ScanQRCode.this,
                            ScanQRCode.this.getString(R.string.error_network_timeout),
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
                map.put("QRCode", QRCode);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request, "VERIFY");
    }

    public void setQuantity() {
        txtQuantity.setText(Quantity + "");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgPlus) {
            if (RemainingTicket > Quantity) {
                Quantity++;
            }
        } else if (v.getId() == R.id.imgMinus) {
            if (Quantity != 1) {
                Quantity--;
            }
        } else if (v.getId() == R.id.btnRedeem) {
            RedeemQRCode();
        }
        setQuantity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

}
