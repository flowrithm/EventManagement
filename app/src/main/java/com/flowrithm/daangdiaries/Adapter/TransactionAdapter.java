package com.flowrithm.daangdiaries.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.Model.MTransaction;
import com.flowrithm.daangdiaries.R;

import java.util.ArrayList;

public class TransactionAdapter extends BaseAdapter {

    Context context;
    ArrayList<MTransaction> transactions;
    LayoutInflater inflater;
    LinearLayout.LayoutParams params;

    public TransactionAdapter(Context context, ArrayList<MTransaction> transactions){
        this.context=context;
        this.transactions=transactions;
        this.inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,3,5,6);
    }

    @Override
    public int getCount() {
        return this.transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return this.transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_transaction_layout,null);
            holder=new ViewHolder();
            holder.txtEventName=(TextView) convertView.findViewById(R.id.txtEventName);
            holder.txtAmount=(TextView)convertView.findViewById(R.id.txtAmount);
            holder.txtStatus=(TextView)convertView.findViewById(R.id.txtStatus);
            holder.txtDate=(TextView)convertView.findViewById(R.id.txtDate);
            holder.layout=(LinearLayout)convertView.findViewById(R.id.layout);
            holder.layout.setLayoutParams(params);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }

        if(transactions.get(position).Status.contains("CANCEL_TRANSACTION")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.red));
        }else{
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.colorDarkGreen));
        }
        holder.txtEventName.setText(transactions.get(position).EventName);
        holder.txtAmount.setText(transactions.get(position).TxnAmount);
        holder.txtDate.setText(transactions.get(position).TxnDateTime);
        holder.txtStatus.setText(transactions.get(position).CustomStatus);
        return convertView;
    }

    public class ViewHolder{
        TextView txtEventName,txtAmount,txtStatus,txtDate;
        public LinearLayout layout;
    }
}
