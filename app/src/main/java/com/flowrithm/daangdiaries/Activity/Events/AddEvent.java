package com.flowrithm.daangdiaries.Activity.Events;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
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
import com.flowrithm.daangdiaries.Model.MTax;
import com.flowrithm.daangdiaries.R;

import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.flowrithm.daangdiaries.Utils.VolleyMultipartRequest;
import com.flowrithm.daangdiaries.Utils.VolleySingleton;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfig;

import static com.flowrithm.daangdiaries.Api.WebApi.GET_EVENT_DETAIL;
import static com.flowrithm.daangdiaries.Api.WebApi.GET_TAXES;
import static com.flowrithm.daangdiaries.Utils.Config.CompanyId;

public class AddEvent extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etAddress)
    EditText etAddress;

    @Bind(R.id.etContactPersonName)
    EditText etContactPersonName;

    @Bind(R.id.etContactPersonNumber)
    EditText etContactPersonNumber;

    @Bind(R.id.etPassPrice)
    EditText etPassPrice;

    @Bind(R.id.etStartDate)
    TextView etStartDate;

    @Bind(R.id.etEndDate)
    TextView etEndDate;

    @Bind(R.id.etStartTime)
    TextView etStartTime;

    @Bind(R.id.etEndTime)
    TextView etEndTime;

    @Bind(R.id.stActive)
    Switch stActive;

    @Bind(R.id.btnSave)
    Button btnSave;

    @Bind(R.id.etDescription)
    EditText etDescription;

    @Bind(R.id.imgPhoto)
    ImageView imgPhoto;

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.imgContact)
    ImageView imgContact;

    @Bind(R.id.txtTax)
    TextView txtTax;

    String Image;
    Uri EventImage;
    MEvent event;

    boolean IsFileUpdated = false;
    int EventId = 0;
    SharedPreferences pref;
    ArrayList<MTax> taxes,tmpTaxes;
    ArrayList<String> arrayTaxes;
    boolean[] SelectedITems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);
        pref = Application.getSharedPreferenceInstance();
        event = (MEvent) getIntent().getSerializableExtra("Detail");
        if (event != null) {
            EventId = event.EventId;
            GetEventDetail();
        }
        imgBack.setOnClickListener(this);
        imgPhoto.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        etStartDate.setOnClickListener(this);
        etEndDate.setOnClickListener(this);
        etStartTime.setOnClickListener(this);
        imgContact.setOnClickListener(this);
        etEndTime.setOnClickListener(this);
        txtTax.setOnClickListener(this);
        EasyImage.configuration(this)
                .setImagesFolderName("My app images")
                .saveInAppExternalFilesDir()
                .saveInRootPicturesDirectory();
        GetTaxes();
    }

    public void GetEventDetail() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, GET_EVENT_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        etName.setText(json.getString("EventName"));
                        etStartDate.setText(Utils.ConvertDateFormate(Utils.getDateFromString(json.getString("StartDate"))));
                        etEndDate.setText(Utils.ConvertDateFormate(Utils.getDateFromString(json.getString("EndDate"))));
                        etStartTime.setText(Utils.ConvertTimeIn24(json.getString("StartTime")));
                        etEndTime.setText(Utils.ConvertTimeIn24(json.getString("EndTime")));
                        etAddress.setText(json.getString("Venue"));
                        etContactPersonName.setText(json.getString("ContactPerson"));
                        etDescription.setText(json.getString("Description"));
                        etContactPersonNumber.setText(json.getString("ContactNumber"));
                        stActive.setChecked(json.getInt("Status") == 0 ? false : true);
                        Picasso.with(AddEvent.this).load(WebApi.DOMAIN + json.getString("Image")).placeholder(R.drawable.icon_banner1).into(imgPhoto);
                        etPassPrice.setText(json.getInt("PassPrice") + "");
                        ArrayList<MTax> taxes=new ArrayList<>();
                        JSONArray array=json.getJSONArray("IncludedTaxes");
                        if(array.length()>0){
                            String Selected="";
                            for(int i=0;i<array.length();i++){
                                JSONObject obj=array.getJSONObject(i);
                                    Selected+=obj.getString("TaxName")+", ";
                            }
                            if(!Selected.equals("")) {
                                Selected = Selected.substring(0, Selected.length() - 2);
                            }
                            txtTax.setText(Selected);
                        }
                        txtTax.setEnabled(false);
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
                    Toast.makeText(AddEvent.this,
                            AddEvent.this.getString(R.string.error_network_timeout),
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

    public void GetTaxes() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, GET_TAXES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        taxes=new ArrayList<>();
                        arrayTaxes=new ArrayList<>();
                        tmpTaxes=new ArrayList<>();
                        JSONArray array=json.getJSONArray("Data");
                        SelectedITems=new boolean[array.length()];
                        for(int i=0;i<array.length();i++){
                            JSONObject obj=array.getJSONObject(i);
                            MTax tax=new MTax();
                            SelectedITems[i]=false;
                            tax.TaxId=obj.getString("TaxId");
                            tax.TaxType=obj.getString("TaxName");
                            arrayTaxes.add(obj.getString("TaxName"));
                            tax.Tax=obj.getString("Tax");
                            taxes.add(tax);
                            tmpTaxes.add(tax);
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
                    Toast.makeText(AddEvent.this,
                            AddEvent.this.getString(R.string.error_network_timeout),
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
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public boolean Validation() {
        boolean Success = true;
        if (!Utils.CheckEmptyValidation(this, etName)) {
            Success = false;
        }
        if (!Utils.CheckEmptyValidation(this, etAddress)) {
            Success = false;
        }
        if (!Utils.CheckEmptyValidation(this, etContactPersonName)) {
            Success = false;
        }
        if (!Utils.CheckEmptyValidation(this, etContactPersonNumber)) {
            Success = false;
        }
        if (etContactPersonNumber.getText().toString().length() != 10) {
            Success = false;
            etContactPersonNumber.setError("Enter Valid Mobile Number");
        }
        if (!Utils.CheckEmptyValidation(this, etPassPrice)) {
            Success = false;
        }
        if (etStartDate.getText().toString().equals("")) {
            Success = false;
            etStartDate.setError("Mandatory");
        } else {
            etStartDate.setError(null);
        }
        if (etEndDate.getText().toString().equals("")) {
            Success = false;
            etEndDate.setError("Mandatory");
        } else {
            etEndDate.setError(null);
        }
        if (etStartTime.getText().toString().equals("")) {
            Success = false;
            etStartTime.setError("Mandatory");
        } else {
            etStartTime.setError(null);
        }
        if (etEndTime.getText().toString().equals("")) {
            Success = false;
            etEndTime.setError("Mandatory");
        } else {
            etEndTime.setError(null);
        }
        return Success;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.etStartDate) {
            Utils.PickDateIntoTextView(this, etStartDate, etStartDate.getText().toString(), false);
        } else if (v.getId() == R.id.etEndDate) {
            Utils.PickDateIntoTextView(this, etEndDate, etEndDate.getText().toString(), false);
        } else if (v.getId() == R.id.etStartTime) {
            Utils.PickTimeInTextView(this, etStartTime);
        } else if (v.getId() == R.id.etEndTime) {
            Utils.PickTimeInTextView(this, etEndTime);
        } else if (v.getId() == R.id.btnSave) {
            if (Validation()) {
                AddEvent();
            }
        } else if (v.getId() == R.id.imgPhoto) {
            EasyImage.openChooserWithGallery(AddEvent.this, "Select Image", EasyImageConfig.REQ_PICK_PICTURE_FROM_GALLERY);
        } else if (v.getId() == R.id.imgBack) {
            this.finish();
        } else if (v.getId() == R.id.imgContact) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            startActivityForResult(intent, 102);
        }else if(v.getId()==R.id.txtTax){
            for(int i=0;i<taxes.size();i++){
                if(taxes.get(i).IsSelected){
                    SelectedITems[i]=true;
                    tmpTaxes.get(i).IsSelected=true;
                }else{
                    SelectedITems[i]=false;
                    tmpTaxes.get(i).IsSelected=false;
                }
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Taxes ");
            builder.setMultiChoiceItems(arrayTaxes.toArray(new String[arrayTaxes.size()]), SelectedITems,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected,
                                            boolean isChecked) {
                            if(tmpTaxes.get(indexSelected).IsSelected){
                                tmpTaxes.get(indexSelected).IsSelected=false;
                                SelectedITems[indexSelected]=false;
                            }else{
                                taxes.get(indexSelected).IsSelected=true;
                                SelectedITems[indexSelected]=true;
                            }
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String Selected="";
                            for(int i=0;i<tmpTaxes.size();i++){
                                if(tmpTaxes.get(i).IsSelected){
                                    Selected+=tmpTaxes.get(i).TaxType+",";
                                    taxes.get(i).IsSelected=true;
                                }else{
                                    taxes.get(i).IsSelected=false;
                                }
                            }
                            if(!Selected.equals("")) {
                                Selected = Selected.substring(0, Selected.length() - 1);
                            }
                            txtTax.setText(Selected);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            for(int i=0;i<tmpTaxes.size();i++){
                                tmpTaxes.get(i).IsSelected=false;
                            }
                            dialog.dismiss();
                        }
                    });

            Dialog dialog = builder.create();//AlertDialog dialog;
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                IsFileUpdated = true;
                EventImage = Uri.fromFile(new File(imageFile.getAbsolutePath()));
                imgPhoto.setImageURI(EventImage);
            }

        });
        if (requestCode == 102 && resultCode == RESULT_OK) {
            Uri contactData = data.getData();
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();

            String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

            number = number.replace(" ", "").replace("+91", "").replace("-", "");

            String Name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            etContactPersonName.setText(Name);
            etContactPersonNumber.setText(number);
        }
    }

    public void AddEvent() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebApi.POST_ADD_NEW_EVENT, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    Utils.ShowServiceDialog(AddEvent.this, result);
//                    Toast.makeText(Profile.this, result.getString("Message"), Toast.LENGTH_SHORT).show();
                    if (result.getInt("success") == 1) {
                        AddEvent.this.setResult(RESULT_OK);
                        AddEvent.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddEvent.this, R.string.Error_Network_Not_Reachable, Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("EventId", EventId + "");
                map.put("IsFileUpdated", IsFileUpdated + "");
                map.put("EventName", etName.getText().toString());
                String StartDate = Utils.ConvertDateFormateReverse(Utils.getDateFromStringFull(etStartDate.getText().toString()));
                map.put("StartDate", StartDate);
                String EndDate = Utils.ConvertDateFormateReverse(Utils.getDateFromStringFull(etEndDate.getText().toString()));
                map.put("EndDate", EndDate);
                String StartTime = Utils.ConvertTimeIn24(etStartTime.getText().toString());
                map.put("StartTime", StartTime);
                String EndTime = Utils.ConvertTimeIn24(etEndTime.getText().toString());
                map.put("EndTime", EndTime);

                String TaxIds="";
                for(MTax tax:taxes) {
                    if(tax.IsSelected){
                        TaxIds+=tax.TaxId+",";
                    }
                }
                TaxIds=TaxIds.substring(0,TaxIds.length()-1);
                map.put("TaxId",TaxIds);
                map.put("Description", etDescription.getText().toString());
                map.put("Venue", etAddress.getText().toString());
                map.put("ContactPersonName", etContactPersonName.getText().toString());
                map.put("ContactNumber", etContactPersonNumber.getText().toString());
                map.put("PassPrice", etPassPrice.getText().toString());
                map.put("IsActive", stActive.isChecked() + "");
                map.put("InsBy", pref.getString("UserId", ""));
                map.put("CompanyId", CompanyId);
                return map;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, VolleyMultipartRequest.DataPart> imgData = new HashMap<>();
                if (EventImage != null && IsFileUpdated) {
                    byte[] Imagedata = convertImageToByte(EventImage);
                    imgData.put("image", new DataPart(etName.getText().toString() + ".jpg", Imagedata, "image/jpeg"));
                }
                return imgData;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }

    public byte[] convertImageToByte(Uri uri) {
        byte[] data = null;
        try {
            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

}
