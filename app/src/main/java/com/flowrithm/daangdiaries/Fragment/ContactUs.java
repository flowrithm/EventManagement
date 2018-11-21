package com.flowrithm.daangdiaries.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Utils;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUs extends Fragment implements View.OnClickListener {


    @Bind(R.id.txtAddress)
    TextView txtAddress;

    @Bind(R.id.txtMail)
    TextView txtMail;

    @Bind(R.id.txtContact)
    TextView txtContact;

    @Bind(R.id.txtWeb)
    TextView txtWeb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        ButterKnife.bind(this, view);
        txtAddress.setOnClickListener(this);
        txtMail.setOnClickListener(this);
        txtContact.setOnClickListener(this);
        txtWeb.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txtAddress){
            Utils.OpenGoogleMap(this.getContext(),txtAddress.getText().toString());
        }else if(v.getId()==R.id.txtMail){
            Utils.OpenMail(this.getContext(),txtMail.getText().toString());
        }else if(v.getId()==R.id.txtContact){
            Utils.OpenDialerScreen(this.getContext(),txtContact.getText().toString());
        }else if(v.getId()==R.id.txtWeb){
            Utils.OpenBrowser(this.getContext(),"http://"+txtWeb.getText().toString());
        }
    }
}
