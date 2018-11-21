package com.flowrithm.daangdiaries.Activity.Blogs;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Model.MBlogs;
import com.flowrithm.daangdiaries.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BlogDetail extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.imgBack)
    ImageView imgBack;

    @Bind(R.id.txtHeading)
    TextView txtHeading;

    @Bind(R.id.txtContent)
    TextView txtContent;

    @Bind(R.id.img)
    ImageView img;

    @Bind(R.id.iconEdit)
    ImageView iconEdit;

    @Bind(R.id.adView)
    AdView adView;

    MBlogs blogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);
        ButterKnife.bind(this);
        imgBack.setOnClickListener(this);
        iconEdit.setOnClickListener(this);
        blogs = (MBlogs) getIntent().getSerializableExtra("Detail");
        if (blogs != null) {
            txtHeading.setText(blogs.Heading);
            txtContent.setText(blogs.Content);
            Picasso.with(this).load(WebApi.DOMAIN + blogs.Image).placeholder(R.drawable.icon_banner1).error(R.drawable.icon_banner1).into(img);
        }

        MobileAds.initialize(this, getResources().getString(R.string.AppIdAds));
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("3ACD51E3E5C1B5E51D476F1B02598BA6")
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.e("Error","Ad Loaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("Error",errorCode+"");
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
                Log.e("Error","Ad Close");
            }
        });
        adView.loadAd(adRequest);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgBack) {
            this.finish();
        }else if(v.getId()==R.id.iconEdit){
            Intent intent=new Intent(this,AddBlog.class);
            intent.putExtra("Detail",blogs);
            startActivityForResult(intent,101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==101 && resultCode==RESULT_OK){
            this.setResult(RESULT_OK);
            this.finish();
        }
    }
}
