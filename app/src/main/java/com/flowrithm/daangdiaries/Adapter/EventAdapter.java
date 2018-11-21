package com.flowrithm.daangdiaries.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;

import java.util.ArrayList;

public class EventAdapter extends BaseAdapter {

    Context context;
    ArrayList<MEvent> events;
    LayoutInflater inflater;
    LinearLayout.LayoutParams params;

    public EventAdapter(Context context,ArrayList<MEvent> events){
        this.context=context;
        this.events=events;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,3,5,6);
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
            convertView=inflater.inflate(R.layout.item_event,null);
            holder=new ViewHolder();
            holder.txtEventName=(TextView) convertView.findViewById(R.id.txtEventName);
            holder.txtDate=(TextView)convertView.findViewById(R.id.txtDate);
            holder.txtVenue=(TextView)convertView.findViewById(R.id.txtVenue);
            holder.txtContact=(TextView)convertView.findViewById(R.id.txtContact);
            holder.layout=(LinearLayout)convertView.findViewById(R.id.layout);
            holder.layout.setLayoutParams(params);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        holder.txtEventName.setText(events.get(position).Name);
        holder.txtDate.setText(events.get(position).StartDate+" - "+events.get(position).EndDate);
        holder.txtContact.setText(events.get(position).ContactNumber);
        holder.txtVenue.setText(events.get(position).Venue);
        return convertView;
    }

    public class ViewHolder{
        TextView txtEventName,txtDate,txtVenue,txtContact;
        public LinearLayout layout;
    }
}
