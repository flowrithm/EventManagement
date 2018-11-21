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
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EventListAdapter extends BaseAdapter {

    Context context;
    ArrayList<MEvent> events;
    LayoutInflater inflater;
    LinearLayout.LayoutParams params;
    public EventListAdapter(Activity context, ArrayList<MEvent> events){
        this.context=context;
        this.events=events;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Point point=Utils.GetScreenSize(context);
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, point.y/3);
        params.setMargins(0,3,0,3);
    }

    @Override
    public int getCount() {
        return this.events.size();
    }

    @Override
    public Object getItem(int position) {
        return this.events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_event_design,null);
            holder=new ViewHolder();
            holder.txtEventName=(TextView) convertView.findViewById(R.id.txtEventName);
            holder.txtStartDate=(TextView)convertView.findViewById(R.id.txtStartDate);
            holder.txtEndDate=(TextView)convertView.findViewById(R.id.txtEndDate);
            holder.txtVenue=(TextView)convertView.findViewById(R.id.txtVenue);
            holder.img=(ImageView)convertView.findViewById(R.id.imgEvent);
            holder.layoutEndDate=(LinearLayout)convertView.findViewById(R.id.layoutEndDate);
            holder.layout=(RelativeLayout) convertView.findViewById(R.id.layout);
            holder.layout.setLayoutParams(params);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        Picasso.with(context).load(WebApi.DOMAIN + events.get(position).Image).placeholder(R.drawable.icon_banner1).into(holder.img);
        holder.txtEventName.setText(events.get(position).Name);
        holder.txtStartDate.setText(events.get(position).StartDate);
        holder.txtEndDate.setText(events.get(position).EndDate);
        if(events.get(position).EndDate.equals("")){
            holder.layoutEndDate.setVisibility(View.GONE);
        }
        holder.txtVenue.setText(events.get(position).Venue);
        return convertView;
    }

    public class ViewHolder{
        TextView txtEventName,txtStartDate,txtEndDate,txtVenue;
        ImageView img;
        LinearLayout layoutEndDate;
        public RelativeLayout layout;
    }
}
