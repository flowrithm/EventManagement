package com.flowrithm.daangdiaries.Activity.Blogs;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.flowrithm.daangdiaries.Model.MBlogs;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.flowrithm.daangdiaries.Utils.VolleyMultipartRequest;
import com.flowrithm.daangdiaries.Utils.VolleySingleton;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfig;

import static com.flowrithm.daangdiaries.Utils.Config.CompanyId;

public class AddBlog extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.etHeading)
    EditText etHeading;

    @Bind(R.id.etDescription)
    EditText etDescription;

    @Bind(R.id.imgPhoto)
    ImageView imgPhoto;

    @Bind(R.id.btnSave)
    Button btnSave;

    @Bind(R.id.btnDelete)
    Button btnDelete;

    Uri EventImage;
    int BlogId=0;
    boolean IsFileUpdated = false;
    SharedPreferences pref;

    MBlogs blogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);
        ButterKnife.bind(this);
        blogs=(MBlogs)getIntent().getSerializableExtra("Detail");
        if(blogs!=null){
            BlogId=blogs.BlogId;
            etHeading.setText(blogs.Heading);
            etDescription.setText(blogs.Content);
            Picasso.with(this).load(WebApi.DOMAIN+blogs.Image).placeholder(R.drawable.icon_banner1).error(R.drawable.icon_banner1).into(imgPhoto);
        }
        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        imgPhoto.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        pref=Application.getSharedPreferenceInstance();
        EasyImage.configuration(this)
                .setImagesFolderName("My app images")
                .saveInAppExternalFilesDir()
                .saveInRootPicturesDirectory();
    }

    public void AddBlog() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, WebApi.POST_ADD_BLOG, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    Utils.ShowServiceDialog(AddBlog.this, result);
//                    Toast.makeText(Profile.this, result.getString("Message"), Toast.LENGTH_SHORT).show();
                    if (result.getInt("success") == 1) {
                        AddBlog.this.setResult(RESULT_OK);
                        AddBlog.this.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddBlog.this, R.string.Error_Network_Not_Reachable, Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("BlogId", BlogId + "");
                map.put("IsFileUpdated", IsFileUpdated + "");
                map.put("Heading", etHeading.getText().toString());
                map.put("Content", etDescription.getText().toString());
                map.put("InsBy", pref.getString("UserId", ""));
                map.put("CompanyId", CompanyId);
                return map;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, VolleyMultipartRequest.DataPart> imgData = new HashMap<>();
                if (EventImage != null && IsFileUpdated) {
                    byte[] Imagedata = convertImageToByte(EventImage);
                    imgData.put("image", new DataPart(etHeading.getText().toString() + ".jpg", Imagedata, "image/jpeg"));
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

    public void DeleteBlog(){
        final KProgressHUD progress=Utils.ShowDialog(this);
        StringRequest request=new StringRequest(Request.Method.POST, WebApi.POST_DELETE_BLOG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json=new JSONObject(response);
                    Utils.ShowServiceDialog(AddBlog.this,json);
                    if(json.getInt("success")==1){
                        AddBlog.this.setResult(RESULT_OK);
                        AddBlog.this.finish();
                    }
                }catch (Exception ex){
                    Crashlytics.logException(ex);
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(AddBlog.this,
                            AddBlog.this.getString(R.string.error_network_timeout),
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map=new HashMap<>();
                map.put("BlogId",BlogId+"");
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    public boolean Validation(){
        boolean Success=true;
        if(!Utils.CheckEmptyValidation(this,etHeading)){
            Success=false;
        }
        if(!Utils.CheckEmptyValidation(this,etDescription)){
            Success=false;
        }
        if(EventImage==null){
            Success=false;
            Toast.makeText(this,"Please Add Image",Toast.LENGTH_SHORT).show();
        }
        return Success;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnSave){
            if(Validation()) {
                AddBlog();
            }
        }else if(v.getId()==R.id.btnDelete){
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to delete?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            DeleteBlog();
                        }})
                    .setNegativeButton("No", null).show();
        }else if(v.getId()==R.id.imgPhoto){
            EasyImage.openChooserWithGallery(AddBlog.this, "Select Image", EasyImageConfig.REQ_PICK_PICTURE_FROM_GALLERY);
        }else if(v.getId()==R.id.imgBack){
            this.finish();
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
