package com.flowrithm.daangdiaries.Activity.Users;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
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
import com.flowrithm.daangdiaries.Api.WebApi;
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
import io.realm.Realm;

import static com.flowrithm.daangdiaries.Api.WebApi.ADD_USER;
import static com.flowrithm.daangdiaries.Api.WebApi.UPDATE_USERS;

public class AddUser extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etContact)
    EditText etContact;

    @Bind(R.id.etAddress)
    EditText etAddress;

    @Bind(R.id.etCity)
    EditText etCity;

    @Bind(R.id.stScanningRights)
    Switch stScanningRights;

    @Bind(R.id.stUserCreation)
    Switch stUserCreation;

    @Bind(R.id.stActive)
    Switch stActive;

    @Bind(R.id.btnSave)
    Button btnSave;

    @Bind(R.id.txtRole)
    TextView txtRole;

    @Bind(R.id.imgContact)
    ImageView imgContact;

    @Bind(R.id.etEmail)
    EditText etEmail;

    Realm realm;
    SharedPreferences pref;
    int UserId = 0;

    ArrayList<String> roles;
    HashMap<String, Integer> hashRoles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        pref = Application.getSharedPreferenceInstance();
        ButterKnife.bind(this);
        realm = Application.getRealmInstance();
        UserId = getIntent().getIntExtra("UserId", 0);
        if (UserId != 0) {
            RUser user = realm.where(RUser.class).equalTo("UId", UserId).findFirst();
            etName.setText(user.getName());
            etAddress.setText(user.getAddress());
            etCity.setText(user.getCity());
            etEmail.setText(user.getEmail());
            etContact.setText(user.getContactNo());
            imgContact.setVisibility(View.GONE);
            txtRole.setText(user.getRole());
            setChecked(stActive, user.getIsActive());
            setChecked(stScanningRights, user.getScanningRights());
            setChecked(stUserCreation, user.getUserCreativeRights());
            etContact.setEnabled(false);
        }
        GetRoles();
        btnSave.setOnClickListener(this);
        imgContact.setOnClickListener(this);
        txtRole.setOnClickListener(this);
    }

    public void GetRoles() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, WebApi.GET_ROLES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("success") == 1) {
                        roles = new ArrayList<>();
                        hashRoles = new HashMap<>();
                        JSONArray array = json.getJSONArray("Data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            hashRoles.put(obj.getString("RoleName"), obj.getInt("RoleId"));
                            roles.add(obj.getString("RoleName"));
                        }
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
                    Toast.makeText(AddUser.this,
                            AddUser.this.getString(R.string.error_network_timeout),
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
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request);
    }

    void setChecked(Switch sw, int status) {
        if (status == 0) {
            sw.setChecked(false);
        } else {
            sw.setChecked(true);
        }
    }

    public void UpdateUser() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_USERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Utils.ShowServiceDialog(AddUser.this, json);
                    if (json.getInt("success") == 1) {
                        AddUser.this.setResult(RESULT_OK);
                        AddUser.this.finish();
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
                    Toast.makeText(AddUser.this,
                            AddUser.this.getString(R.string.error_network_timeout),
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
                map.put("UserId", UserId + "");
                map.put("Name", etName.getText().toString());
                map.put("Address", etAddress.getText().toString());
                map.put("City", etCity.getText().toString());
                map.put("ContactNo", etContact.getText().toString());
                map.put("RoleId", hashRoles.get(txtRole.getText().toString()) + "");
                map.put("InsBy", pref.getString("UserId", ""));
                map.put("CompanyId", Config.CompanyId);
                map.put("ScanningPermission", stScanningRights.isChecked() + "");
                map.put("UserCreation", stUserCreation.isChecked() + "");
                map.put("IsActive", stActive.isChecked() + "");
                map.put("IsPayment", false + "");
                map.put("Email",etEmail.getText().toString());
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request, "AddUser");
    }

    public void AddUser() {
        final KProgressHUD progress = Utils.ShowDialog(this);
        StringRequest request = new StringRequest(Request.Method.POST, ADD_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    Utils.ShowServiceDialog(AddUser.this, json);
                    if (json.getBoolean("success")) {
                        AddUser.this.setResult(RESULT_OK);
                        AddUser.this.finish();
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
                    Toast.makeText(AddUser.this,
                            AddUser.this.getString(R.string.error_network_timeout),
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
                map.put("Name", etName.getText().toString());
                map.put("Address", etAddress.getText().toString());
                map.put("City", etCity.getText().toString());
                map.put("ContactNo", etContact.getText().toString());
                map.put("RoleId", hashRoles.get(txtRole.getText().toString()) + "");
                map.put("InsBy", pref.getString("UserId", ""));
                map.put("CompanyId", Config.CompanyId);
                map.put("ScanningPermission", stScanningRights.isChecked() + "");
                map.put("UserCreation", stUserCreation.isChecked() + "");
                map.put("IsActive", stActive.isChecked() + "");
                map.put("Email",etEmail.getText().toString());
                return map;
            }
        };
        Application.getInstance().addToRequestQueue(request, "AddUser");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSave) {
            if (UserId == 0) {
                AddUser();
            } else {
                UpdateUser();
            }
        } else if (v.getId() == R.id.txtRole) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MaterialThemeDialog);
            builder.setTitle("Select Role");
            builder.setSingleChoiceItems(roles.toArray(new String[roles.size()]), roles.indexOf(txtRole.getText().toString()), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    txtRole.setText(roles.get(which));
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (v.getId() == R.id.imgContact) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            startActivityForResult(intent, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            Uri contactData = data.getData();
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();

            String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

            number = number.replace(" ", "").replace("+91", "");

            String Name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            etName.setText(Name);
            etContact.setText(number);
        }
    }
}
