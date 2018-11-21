package com.flowrithm.daangdiaries.Activity.Registration;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.flowrithm.daangdiaries.Activity.Events.EventDetail;
import com.flowrithm.daangdiaries.Activity.Events.TaxInfo;
import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.Model.MTax;
import com.flowrithm.daangdiaries.Model.Paytm;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Tabs.EventRegistration;
import com.flowrithm.daangdiaries.Tabs.EventTicket;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.flowrithm.daangdiaries.Api.WebApi.PURCHASE_TICKET;

public class

CheckOut extends AppCompatActivity implements View.OnClickListener, PaytmPaymentTransactionCallback {

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.txtType)
    TextView txtType;

    @Bind(R.id.lblTypeQty)
    TextView lblTypeQty;

    @Bind(R.id.txtTypePrice)
    TextView txtTypePrice;

    @Bind(R.id.txtQuantity)
    TextView txtQuantity;

    @Bind(R.id.txtTotal)
    TextView txtTotal;

    @Bind(R.id.txtTax)
    TextView txtTax;

    @Bind(R.id.txtGrandTotal)
    TextView txtGrandTotal;

    @Bind(R.id.btnProcess)
    Button btnProcess;

    @Bind(R.id.lblTaxes)
    TextView lblTaxes;

    @Bind(R.id.adView)
    AdView adView;

    @Bind(R.id.imgInfo)
    ImageView imgInfo;

    MEvent event;
    String Quantity, Type, Price, SelectedDate;
    Double Tax = 2.5;
    SharedPreferences pref;
    String orderId = "";
    String custId = "";
    Double FinalPrice = 0.0;
    PaytmPGService Service;
    Double BasePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        ButterKnife.bind(this);
        pref = Application.getSharedPreferenceInstance();
        event = (MEvent) getIntent().getSerializableExtra("Detail");
        Quantity = getIntent().getStringExtra("Quantity");
        Price = getIntent().getStringExtra("Price");
        Type = getIntent().getStringExtra("Type");
        SelectedDate = getIntent().getStringExtra("Date");
        ReflectAd();
        if (event != null) {
            txtType.setText(Type);
            lblTypeQty.setText(Type + " Price");
            txtTypePrice.setText(Price + " Rs.");
            txtQuantity.setText(Quantity);

            Double Total = (Double.parseDouble(Quantity) * Double.parseDouble(Price));

            txtTotal.setText(Total + " Rs.");

            BasePrice=Total;

            Double TotalTax = 0.0;

            if (event.Tax != null) {
                for (MTax tax : event.Tax) {
                    TotalTax += Double.parseDouble(tax.Tax);
                }
            }

            lblTaxes.setText("Taxes ( " + TotalTax + " % )");

            DecimalFormat twoDForm = new DecimalFormat("#.##");

            Double TaxPrice = Double.valueOf(twoDForm.format((Total * TotalTax) / 100));

            txtTax.setText(TaxPrice + " Rs.");


            FinalPrice = Double.valueOf(twoDForm.format(TaxPrice + Total));

            txtGrandTotal.setText(FinalPrice + " Rs.");

        }
        btnProcess.setOnClickListener(this);
        imgInfo.setOnClickListener(this);
    }

    public void ReflectAd() {
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
    }

    public void

    Paytm(String CheckSum) {
        Service = PaytmPGService.getProductionService();

        HashMap<String, String> map = new HashMap<>();
        map.put("MID", Paytm.MerchantId);
        map.put("ORDER_ID", orderId);
        map.put("CUST_ID", custId);
        map.put("INDUSTRY_TYPE_ID", Paytm.IndustryType);
        map.put("CHANNEL_ID", Paytm.ChannelId);
        map.put("TXN_AMOUNT", FinalPrice + "");
        map.put("WEBSITE", Paytm.Website);
        map.put("CALLBACK_URL", Paytm.CallbackUrl + orderId);
        map.put("CHECKSUMHASH", CheckSum);

        PaytmOrder order = new PaytmOrder(map);
        Service.initialize(order, null);
        Service.startPaymentTransaction(CheckOut.this, true, true, this);
    }

    public void GenerateCheckSum() {
        orderId = event.Name + "_" + Config.CompanyId + "_" + event.EventId + "_" + System.currentTimeMillis();
        custId = event.Name + "_" + pref.getString("UserId", "");
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.GENERATE_PAYTM_CHECKSUM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("payt_STATUS").equals("1")) {
                        Paytm(json.getString("CHECKSUMHASH"));
                    }
                } catch (Exception ex) {
                    Log.e("Error", ex.getMessage());
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(CheckOut.this,
                            CheckOut.this.getString(R.string.error_network_timeout),
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
                map.put("MID", Paytm.MerchantId);
                map.put("ORDER_ID", orderId);
                map.put("CUST_ID", custId);
                map.put("INDUSTRY_TYPE_ID", Paytm.IndustryType);
                map.put("CHANNEL_ID", Paytm.ChannelId);
                map.put("TXN_AMOUNT", FinalPrice + "");
                map.put("WEBSITE", Paytm.Website);
                map.put("CALLBACK_URL", Paytm.CallbackUrl + orderId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public void GetPayMentStatus(final String UserStatus) {
        orderId = Service.getOrderId();
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.GET_PAYMENT_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getString("STATUS").equals("TXN_SUCCESS")) {
                        if (Type.equals("Pass")) {
                            GrabPass(json.getString("ORDERID"));
                        } else {
                            GrabTicket(json.getString("ORDERID"));
                        }
                    } else {
                        Toast.makeText(CheckOut.this, "Your Transaction is Fail Please try again...", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Log.e("Error", ex.getMessage());
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(CheckOut.this,
                            CheckOut.this.getString(R.string.error_network_timeout),
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
                map.put("MerchantId", Paytm.MerchantId);
                map.put("OrderId", orderId);
                map.put("MerchantKey", Paytm.MerchantKey);
                map.put("CompanyId",Config.CompanyId);
                map.put("EventId",event.EventId+"");
                map.put("Price",Price+"");
                map.put("UserStatus",UserStatus);
                map.put("Qty",Quantity);
                map.put("Type",Type);
                map.put("InsBy", pref.getString("UserId", ""));
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public void GrabPass(final String OrderId) {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, PURCHASE_TICKET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        Toast.makeText(CheckOut.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        (CheckOut.this).setResult(Activity.RESULT_OK);
                        (CheckOut.this).finish();
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
                    Toast.makeText(CheckOut.this,
                            CheckOut.this.getString(R.string.error_network_timeout),
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
                map.put("EventId", event.EventId + "");
                map.put("EventName", event.Name);
                map.put("CompanyId", Config.CompanyId);
                map.put("Type", Type);
                map.put("Price", event.PassPrice);
                map.put("InsBy", pref.getString("UserId", ""));
                map.put("QTY", Quantity + "");
                map.put("TransationId", OrderId + "");
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public void GrabTicket(final String OrderId) {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, PURCHASE_TICKET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        Toast.makeText(CheckOut.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        (CheckOut.this).setResult(Activity.RESULT_OK);
                        (CheckOut.this).finish();
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
                    Toast.makeText(CheckOut.this,
                            CheckOut.this.getString(R.string.error_network_timeout),
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
                map.put("EventId", event.EventId + "");
                map.put("EventName", event.Name);
                map.put("CompanyId", Config.CompanyId);
                map.put("Type", Type);
                map.put("SelectedDate", SelectedDate);
                map.put("Price", Price);
                map.put("InsBy", pref.getString("UserId", ""));
                map.put("QTY", Quantity + "");
                map.put("TransationId", OrderId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        Log.e("Order Id", Service.getOrderId());
        GetPayMentStatus(inResponse.getString("STATUS"));
        if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
//            GrabPass(inResponse.getString("ORDERID"));
            Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void networkNotAvailable() {
        GetPayMentStatus("CANCEL_TRANSACTION_NETWORK_NOT_AVAILABLE");
        Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        Toast.makeText(this, inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Toast.makeText(this, inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        GetPayMentStatus("CANCEL_TRANSACTION_WEB_PAGE_LOADING_ERROR");
        Toast.makeText(this, inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        GetPayMentStatus("CANCEL_TRANSACTION_BACK_PRESSED");
        Log.e("Order Id", Service.getOrderId());
        Toast.makeText(this, "Back Pressed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        GetPayMentStatus("CANCEL_TRANSACTION");
        Toast.makeText(this, inErrorMessage + inResponse.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnProcess) {
            GenerateCheckSum();
        } else if (v.getId() == R.id.imgBack) {
            this.finish();
        }else if(v.getId()==R.id.imgInfo){
            Intent intent=new Intent(this,TaxInfo.class);
            intent.putExtra("Detail",event);
            intent.putExtra("BasePrice",BasePrice);
            startActivity(intent);
        }
    }
}
