package com.flowrithm.daangdiaries.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowrithm.daangdiaries.Model.Menu;
import com.flowrithm.daangdiaries.R;

import java.util.ArrayList;


/**
 * Created by dev on 26-08-2017.
 */

public class MenuAdapter extends BaseAdapter {

    Context context;
    ArrayList<Menu> menus;
    LayoutInflater inflater;

    public MenuAdapter(Context context, ArrayList<Menu> menus){
        this.context=context;
        this.menus=menus;
    }

    @Override
    public int getCount() {
        return this.menus.size();
    }

    @Override
    public Object getItem(int position) {
        return menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.item_menu,null);
            holder=new ViewHolder();
            holder.txtName=(TextView) convertView.findViewById(R.id.itemTxtMenu);
            holder.Image=(ImageView) convertView.findViewById(R.id.itemImgMenu);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        holder.txtName.setText(menus.get(position).MenuName);
        holder.Image.setImageResource(menus.get(position).MenuImage);
        return convertView;
    }

    public class ViewHolder{
        public TextView txtName;
        public ImageView Image;
    }

}
