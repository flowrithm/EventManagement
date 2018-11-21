package com.flowrithm.daangdiaries.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flowrithm.daangdiaries.Api.WebApi;
import com.flowrithm.daangdiaries.Model.MBlogs;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BlogsAdapter extends BaseAdapter {

    Context context;
    ArrayList<MBlogs> blogs;
    LayoutInflater inflater;
    LinearLayout.LayoutParams params;
    public BlogsAdapter(Activity context, ArrayList<MBlogs> blogs){
        this.context=context;
        this.blogs=blogs;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Point point=Utils.GetScreenSize(context);
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, point.y/3);
        params.setMargins(0,3,0,3);
    }

    @Override
    public int getCount() {
        return this.blogs.size();
    }

    @Override
    public Object getItem(int position) {
        return this.blogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_blog_layout,null);
            holder=new ViewHolder();
            holder.txtEventName=(TextView)convertView.findViewById(R.id.txtEventName);
            holder.txtContent=(TextView)convertView.findViewById(R.id.txtContent);
            holder.img=(ImageView)convertView.findViewById(R.id.img);
            holder.layout=(RelativeLayout) convertView.findViewById(R.id.layout);
            holder.layout.setLayoutParams(params);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        if(!blogs.get(position).Image.equals("")) {
            Picasso.with(context).load(WebApi.DOMAIN + blogs.get(position).Image).placeholder(R.drawable.icon_banner1).into(holder.img);
        }
        holder.txtEventName.setText(blogs.get(position).Heading);
        holder.txtContent.setText(blogs.get(position).Content);
        return convertView;
    }

    public class ViewHolder{
        TextView txtContent,txtEventName;
        ImageView img;
        RelativeLayout layout;
    }
}
