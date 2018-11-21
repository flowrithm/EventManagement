package com.flowrithm.daangdiaries.Tabs;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class Passes extends Fragment {

    @Bind(R.id.qrCode)
    ImageView qrCode;

    @Bind(R.id.txtQty)
    TextView txtQty;

    @Bind(R.id.layoutPass)
    LinearLayout layoutPass;

    @Bind(R.id.layoutNoPass)
    LinearLayout layoutNoPass;

    MEvent event;
    String Qty;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passes, container, false);
        ButterKnife.bind(this, view);
        pref = Application.getSharedPreferenceInstance();
        if (event != null) {
            GenerateQRCode();
        }

        if (Qty.equals("")) {
            layoutNoPass.setVisibility(View.GONE);
            layoutPass.setVisibility(View.GONE);
        }else{
            txtQty.setText(Qty);
        }

        return view;
    }

    public void GenerateQRCode() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap("pass_" + pref.getString("UserId", "") + "_" + event.EventId+ "_" + Qty, BarcodeFormat.QR_CODE, 600, 600);
            qrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void setQty(String Qty) {
        this.Qty = Qty;
    }

    public void setData(MEvent event) {
        this.event = event;
    }

}
