package com.flowrithm.daangdiaries.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.flowrithm.daangdiaries.Activity.Users.AddUser;
import com.flowrithm.daangdiaries.Adapter.UsersAdapter;
import com.flowrithm.daangdiaries.Application;
import com.flowrithm.daangdiaries.Model.RUser;
import com.flowrithm.daangdiaries.R;
import com.flowrithm.daangdiaries.Utils.Config;
import com.flowrithm.daangdiaries.Utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.app.Activity.RESULT_OK;
import static com.flowrithm.daangdiaries.Api.WebApi.GET_USERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class Users extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, TextWatcher, RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.btnAdd)
    FloatingActionButton btnAdd;

    @Bind(R.id.lstUsers)
    ListView lstUsers;

    @Bind(R.id.layoutFilterMain)
    LinearLayout layoutFilterMain;

    @Bind(R.id.layoutFilter)
    LinearLayout layoutFilter;

    @Bind(R.id.rdAgent)
    RadioButton rdAgent;

    @Bind(R.id.rdVisitor)
    RadioButton rdVisitor;

    @Bind(R.id.rdBoth)
    RadioButton rdBoth;

    @Bind(R.id.etSearch)
    AutoCompleteTextView etSearch;

    @Bind(R.id.rgRole)
    RadioGroup rgRole;

    @Bind(R.id.imgStatus)
    ImageView imgStatus;

    ArrayList<String> suggestions;

    RealmResults<RUser> users;
    SharedPreferences pref;
    Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this, view);
        realm = Application.getRealmInstance();
        btnAdd.setOnClickListener(this);
        layoutFilter.setVisibility(View.GONE);
        pref = Application.getSharedPreferenceInstance();
        lstUsers.setOnItemClickListener(this);
        layoutFilterMain.setOnClickListener(this);
        etSearch.addTextChangedListener(this);
        rgRole.setOnCheckedChangeListener(this);

        GetUsers();
        return view;
    }

    public void BindRecords() {
        users = realm.where(RUser.class).findAll();
        UsersAdapter adapter = new UsersAdapter(Users.this.getContext(), users);
        lstUsers.setAdapter(adapter);
    }

    public void GetUsers() {
        final KProgressHUD progress = Utils.ShowDialog(this.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, GET_USERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Utils.ShowServiceDialog(Users.this.getContext(), json);
                    if (json.getInt("success") == 1) {
                        JSONArray array = json.getJSONArray("Data");
                        suggestions=new ArrayList<>();
                        realm.beginTransaction();
                        realm.clear(RUser.class);
                        realm.commitTransaction();
                        realm.beginTransaction();
                        for (int i = 0; i < array.length(); i++) {
                            RUser user = realm.createObject(RUser.class);
                            JSONObject obj = array.getJSONObject(i);
                            user.setUId(obj.getInt("ID"));
                            user.setName(obj.getString("Name"));
                            user.setAddress(obj.getString("Address"));
                            user.setCity(obj.getString("City"));
                            user.setContactNo(obj.getString("ContactNo"));
                            user.setEmail(obj.getString("Email"));
                            user.setRole(obj.getString("RoleName"));
                            user.setIsActive(obj.getInt("IsActive"));
                            user.setScanningRights(obj.getInt("ScanningRights"));
                            user.setUserCreativeRights(obj.getInt("UserCreationRights"));
                            suggestions.add(obj.getString("Name") + " - "+ obj.getString("ContactNo"));
                        }
                        realm.commitTransaction();
                        BindRecords();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Users.this.getContext(),android.R.layout.simple_list_item_1,suggestions);
                        etSearch.setAdapter(adapter);

                    }
                } catch (Exception ex) {
                    Crashlytics.logException(ex.getCause());
                }
                progress.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(Users.this.getContext(),
                            Users.this.getContext().getString(R.string.error_network_timeout),
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                } else if (error instanceof ServerError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                } else if (error instanceof NetworkError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                } else if (error instanceof ParseError) {
                    //TODO
                    Crashlytics.log(error.getMessage());
                }
                progress.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("UserId", pref.getString("UserId", ""));
                map.put("CompanyId", Config.CompanyId);
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request, "AddUser");
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAdd) {
            Intent intent = new Intent(this.getContext(), AddUser.class);
            startActivityForResult(intent, 101);
        }else if(v.getId()==R.id.layoutFilterMain){
            if(layoutFilter.getVisibility()==View.VISIBLE){
                layoutFilter.setVisibility(View.GONE);
                imgStatus.setImageResource(R.mipmap.icon_arrow_down);
            }else{
                layoutFilter.setVisibility(View.VISIBLE);
                imgStatus.setImageResource(R.mipmap.icon_arrow_up);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            GetUsers();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this.getContext(), AddUser.class);
        intent.putExtra("UserId", users.get(position).getUId());
        startActivityForResult(intent, 101);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(rdBoth.isChecked()){
            users = realm.where(RUser.class).contains("Name",etSearch.getText().toString(),Case.SENSITIVE).or().equalTo("ContactNo",etSearch.getText().toString()).findAllSorted("Name", Sort.ASCENDING);
            UsersAdapter adapter = new UsersAdapter(Users.this.getContext(), users);
            lstUsers.setAdapter(adapter);
        }else if(rdAgent.isChecked()){
            users = realm.where(RUser.class).equalTo("Role",rdAgent.getText().toString()).contains("Name",etSearch.getText().toString(), Case.SENSITIVE).or().equalTo("Role",rdAgent.getText().toString()).contains("ContactNo",etSearch.getText().toString(), Case.SENSITIVE).findAllSorted("Name", Sort.ASCENDING);
            UsersAdapter adapter = new UsersAdapter(Users.this.getContext(), users);
            lstUsers.setAdapter(adapter);
        }else if(rdVisitor.isChecked()){
            users = realm.where(RUser.class).equalTo("Role",rdVisitor.getText().toString()).contains("Name",etSearch.getText().toString(),Case.SENSITIVE).or().equalTo("Role",rdVisitor.getText().toString()).contains("ContactNo",etSearch.getText().toString(),Case.SENSITIVE).findAllSorted("Name", Sort.ASCENDING);
            UsersAdapter adapter = new UsersAdapter(Users.this.getContext(), users);
            lstUsers.setAdapter(adapter);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId==R.id.rdAgent){
            users = realm.where(RUser.class).equalTo("Role",rdAgent.getText().toString()).findAll();
            UsersAdapter adapter = new UsersAdapter(Users.this.getContext(), users);
            lstUsers.setAdapter(adapter);
        }else if(checkedId==R.id.rdVisitor){
            users = realm.where(RUser.class).equalTo("Role",rdVisitor.getText().toString()).findAll();
            UsersAdapter adapter = new UsersAdapter(Users.this.getContext(), users);
            lstUsers.setAdapter(adapter);
        }else if(checkedId==R.id.rdBoth){
            BindRecords();
        }
    }
}
