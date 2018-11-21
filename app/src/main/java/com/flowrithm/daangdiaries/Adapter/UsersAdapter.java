package com.flowrithm.daangdiaries.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flowrithm.daangdiaries.Model.RUser;
import com.flowrithm.daangdiaries.R;

import io.realm.RealmResults;

public class UsersAdapter extends BaseAdapter {

    Context context;
    RealmResults<RUser> users;
    LayoutInflater inflater;
    LinearLayout.LayoutParams params;

    public UsersAdapter(Context context, RealmResults<RUser> users){
        this.context=context;
        this.users=users;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5,3,5,6);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.item_users,null);
            holder.txtName=(TextView)convertView.findViewById(R.id.txtName);
            holder.txtContact=(TextView)convertView.findViewById(R.id.txtContact);
            holder.txtCity=(TextView)convertView.findViewById(R.id.txtCity);
            holder.layout=(LinearLayout)convertView.findViewById(R.id.layout);
            holder.txtRole=(TextView)convertView.findViewById(R.id.txtRole);
            holder.layout.setLayoutParams(params);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        RUser user=users.get(position);
        holder.txtName.setText(user.getName());
        holder.txtCity.setText(user.getCity());
        holder.txtContact.setText(user.getContactNo());
        holder.txtRole.setText(user.getRole());
        return convertView;
    }

    public class ViewHolder{
        public TextView txtName,txtContact,txtCity,txtRole;
        public LinearLayout layout;
    }

}
