package com.flowrithm.daangdiaries.Activity.Events;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.Model.MTax;
import com.flowrithm.daangdiaries.R;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TaxInfo extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.layoutInfo)
    LinearLayout layoutInfo;

    @Bind(R.id.txtTotal)
    TextView txtTotal;

    @Bind(R.id.btnOk)
    Button btnOk;

    MEvent event;
    Double BasePrice = 0.0,Total=0.0;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_info);
        ButterKnife.bind(this);
        BasePrice = getIntent().getDoubleExtra("BasePrice", 0.0);
        event = (MEvent) getIntent().getSerializableExtra("Detail");
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        for (MTax tax : event.Tax) {

            View view = inflater.inflate(R.layout.layout_tax_info, null);

            TextView txtType = (TextView) view.findViewById(R.id.lblTaxType);
            txtType.setText(tax.TaxType +" ("+ tax.Tax +" %)");

            Double TaxPrice = Double.valueOf(twoDForm.format((BasePrice * Double.parseDouble(tax.Tax)) / 100));

            Total+=TaxPrice;

            TextView txtTax = (TextView) view.findViewById(R.id.lblTaxValue);
            txtTax.setText(TaxPrice + " Rs.");

            layoutInfo.addView(view);

        }

        txtTotal.setText( Double.valueOf(twoDForm.format(Total))+" Rs.");
        btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnOk){
            this.finish();
        }
    }
}
