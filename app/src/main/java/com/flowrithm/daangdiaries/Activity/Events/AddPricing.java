package com.flowrithm.daangdiaries.Activity.Events;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.MEvent;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Tabs.PassPricing;
import com.flowrithm.daangdiaries.Tabs.TicketPricing;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddPricing extends AppCompatActivity {

    @Bind(R.id.viewpager)
    ViewPager viewpager;

    @Bind(R.id.tabs)
    TabLayout tabs;

    @Bind(R.id.txtEventName)
    TextView txtEventName;

    @Bind(R.id.txtDate)
    TextView txtDate;

    @Bind(R.id.txtVenue)
    TextView txtVenue;

    @Bind(R.id.txtContact)
    TextView txtContact;

    MEvent event;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pricing);
        ButterKnife.bind(this);
        event = (MEvent) getIntent().getSerializableExtra("Detail");
        if (event != null) {
            txtEventName.setText(event.Name);
            pref = Application.getSharedPreferenceInstance();
            txtContact.setText(event.ContactNumber);
            txtDate.setText(event.StartDate + " - " + event.EndDate);
            txtVenue.setText(event.Venue);
        }
        setupViewPager(viewpager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(this.getSupportFragmentManager());

        TicketPricing ticketPricing = new TicketPricing();

        PassPricing passPricing = new PassPricing();

        adapter.addFragment(passPricing, "Passes");
        passPricing.setData(event);

        adapter.addFragment(ticketPricing, "Ticket");
        ticketPricing.setData(event);

        viewPager.setAdapter(adapter);
        pref = Application.getSharedPreferenceInstance();
        viewPager.setOffscreenPageLimit(0);
        tabs.setupWithViewPager(viewPager);
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
