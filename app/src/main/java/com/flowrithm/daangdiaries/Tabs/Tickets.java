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
public class Tickets extends Fragment {

    @Bind(R.id.txtDate)
    TextView txtDate;

    @Bind(R.id.txtQty)
    TextView txtQty;

    @Bind(R.id.qrCode)
    ImageView qrCode;

    @Bind(R.id.layoutNoTickets)
    LinearLayout layoutNoTickets;

    @Bind(R.id.layoutPass)
    LinearLayout layoutPass;

    MEvent event;
    String Qty, Date;
    SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);
        ButterKnife.bind(this, view);
        pref = Application.getSharedPreferenceInstance();
        if (event != null) {
            GenerateQRCode();
        }
        if (!Qty.equals("") ) {
            txtQty.setText(Qty);
        }
        if (!Date.equals("")) {
            txtDate.setText(Date);
        }

        if (Date.equals("") || Qty.equals("") || Qty.equals("0")) {
            layoutPass.setVisibility(View.GONE);
            layoutNoTickets.setVisibility(View.GONE);
        }
        return view;
    }

    public void GenerateQRCode() {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap("ticket_" + pref.getString("UserId", "") + "_" + event.EventId + "_" + Qty, BarcodeFormat.QR_CODE, 600, 600);
            qrCode.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void setData(MEvent event) {
        this.event = event;
    }

    public void setQty(String Qty) {
        this.Qty = Qty;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

}
