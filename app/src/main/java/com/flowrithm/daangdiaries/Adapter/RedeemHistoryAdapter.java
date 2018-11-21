package com.flowrithm.daangdiaries.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flowrithm.daangdiaries.Model.MReedem;
import com.flowrithm.daangdiaries.R;

import java.util.ArrayList;

public class RedeemHistoryAdapter extends BaseAdapter {

    Context context;
    ArrayList<MReedem> reedems;
    LayoutInflater inflater;
    LinearLayout.LayoutParams params;

    public RedeemHistoryAdapter(Context context, ArrayList<MReedem> reedems){
        this.context=context;
        this.reedems=reedems;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,3,5,6);
    }

    @Override
    public int getCount() {
        return this.reedems.size();
    }

    @Override
    public Object getItem(int position) {
        return this.reedems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_redeem_history,null);
            holder=new ViewHolder();
            holder.txtEventName=(TextView) convertView.findViewById(R.id.txtEventName);
            holder.txtDate=(TextView)convertView.findViewById(R.id.txtDate);
            holder.txtType=(TextView)convertView.findViewById(R.id.txtType);
            holder.txtQty=(TextView)convertView.findViewById(R.id.txtQty);
            holder.layout=(LinearLayout)convertView.findViewById(R.id.layout);
            holder.layout.setLayoutParams(params);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        holder.txtEventName.setText(reedems.get(position).EventName);
        holder.txtDate.setText(reedems.get(position).DateTime);
        holder.txtType.setText(reedems.get(position).Type);
        holder.txtQty.setText(reedems.get(position).Qty);
        return convertView;
    }

    public class ViewHolder{
        TextView txtType,txtEventName,txtDate,txtQty;
        public LinearLayout layout;
    }
}
