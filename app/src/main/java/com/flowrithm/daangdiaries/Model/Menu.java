package com.flowrithm.daangdiaries.Model;

import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.R;

import java.util.ArrayList;

public class Menu {

    public Menu(int Image,String Name,String Tag){
        this.MenuImage=Image;
        this.MenuName=Name;
        this.Tag=Tag;
    }

    public int MenuImage;
    public String MenuName;
    public String Tag;

    public static ArrayList<Menu> parseMenu(String Role){
        ArrayList<Menu> menus=new ArrayList<>();
        menus.add(new Menu(R.mipmap.icon_dashboard,"Dashboard","Dashboard"));
        menus.add(new Menu(R.mipmap.icon_user,"Profile","Profile"));
        menus.add(new Menu(R.mipmap.icon_event,"Events","Events"));
        menus.add(new Menu(R.mipmap.icon_ticket,"My Passes","MyPass"));
        menus.add(new Menu(R.mipmap.icon_transaction,"My Orders","MyOrders"));
        menus.add(new Menu(R.mipmap.icon_blogs,"Blogs","Blogs"));
//        if(Application.getSharedPreferenceInstance().getInt("ScanningRights",0)==1 || Role.equals("Admin")) {
//            menus.add(new Menu(R.mipmap.icon_dashboard, "Scan Tickets", "ScanTickets"));
//        }
        if(Application.getSharedPreferenceInstance().getInt("UserCreationRights",0)==1 || Role.equals("Admin")) {
            menus.add(new Menu(R.mipmap.icon_role, "User Management", "UserManagement"));
            menus.add(new Menu(R.mipmap.icon_event_management, "Event Management", "EventManagement"));
        }
        menus.add(new Menu(R.mipmap.icon_about_us,"About Us","AboutUs"));
        menus.add(new Menu(R.mipmap.icon_contact_us,"Powered By","PoweredBy"));
        menus.add(new Menu(R.mipmap.icon_logout,"Logout","Logout"));
        return menus;
    }

}
