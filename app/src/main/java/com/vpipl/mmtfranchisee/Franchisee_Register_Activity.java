package com.vpipl.mmtfranchisee;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vpipl.mmtfranchisee.Utils.AppUtils;
import com.vpipl.mmtfranchisee.Utils.QueryUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Franchisee_Register_Activity extends AppCompatActivity {

    private static final String TAG = "Register_User_Activity";

    Button button_franchisee_submit;
    private TextInputEditText edtxt_sponsor_id, edtxt_fran_name, edtxt_fran_mobileno, edtxt_fran_pincode, edtxt_fran_address, edtxt_fran_contact_person;
    private String str_name, str_mobileno, str_pincode, str_address, str_contact_person, StateCode, state_name, citycode, city_name;
    String sponsor_id, sponsor_form_no, sponsor_name;
    TextInputLayout til_sposnor_id;
    TextView txt_sponsor_name;

    Spinner fran_state, fran_city;
    // -----------  spinner data country ----------
    ArrayList<String> statlist = new ArrayList<String>();
    private JSONArray getStatename;

    ArrayList<String> citylist = new ArrayList<String>();
    private JSONArray getcityname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_franchisee);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(" " + getResources().getString(R.string.app_name));
        getSupportActionBar().setSubtitle("");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edtxt_fran_name = (TextInputEditText) findViewById(R.id.edtxt_fran_name);
        edtxt_fran_mobileno = (TextInputEditText) findViewById(R.id.edtxt_fran_mobileno);
        edtxt_fran_pincode = (TextInputEditText) findViewById(R.id.edtxt_fran_pincode);
        edtxt_fran_address = (TextInputEditText) findViewById(R.id.edtxt_fran_address);
        edtxt_fran_contact_person = (TextInputEditText) findViewById(R.id.edtxt_fran_contact_person);
        fran_state = (Spinner) findViewById(R.id.fran_state);
        fran_city = (Spinner) findViewById(R.id.fran_city);
        edtxt_sponsor_id = (TextInputEditText) findViewById(R.id.edtxt_sponsor_id);
        til_sposnor_id = findViewById(R.id.til_sposnor_id);
        txt_sponsor_name = findViewById(R.id.txt_sponsor_name);

        edtxt_sponsor_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String id = edtxt_sponsor_id.getText().toString();
                if (id.length() > 9) {
                    executetoCheckSponsorName(edtxt_sponsor_id.getText().toString());
                } else {
                    sponsor_id = "" ;
                    txt_sponsor_name.setVisibility(View.GONE);
                }
            }
        });
        button_franchisee_submit = (Button) findViewById(R.id.button_franchisee_submit);

        button_franchisee_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Franchisee_Register_Activity.this, view);
                ValidateData();
            }
        });

        Getstate();

        fran_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StateCode = getStateCode(position);

                Log.e("StateCode", StateCode);
                ((TextView) view).setTextColor(Color.BLACK);
                citylist.clear();
                Getcity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fran_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citycode = getcityCode(position);

                Log.e("citycode", citycode);
                ((TextView) view).setTextColor(Color.BLACK);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void ValidateData() {
        edtxt_fran_name.setError(null);
        edtxt_fran_mobileno.setError(null);
        edtxt_fran_pincode.setError(null);
        edtxt_fran_address.setError(null);
        edtxt_fran_contact_person.setError(null);

        str_name = edtxt_fran_name.getText().toString();
        str_mobileno = edtxt_fran_mobileno.getText().toString();
        str_pincode = edtxt_fran_pincode.getText().toString();
        str_address = edtxt_fran_address.getText().toString();
        str_contact_person = edtxt_fran_contact_person.getText().toString();

        if ((sponsor_id.trim()).length() != 10) {
            edtxt_sponsor_id.setError("Invalid Sponsor Id ");
            edtxt_sponsor_id.requestFocus();
        } else if (TextUtils.isEmpty(str_name)) {
            edtxt_fran_name.setError(getResources().getString(R.string.error_required_name));
            edtxt_fran_name.requestFocus();
        } else if (TextUtils.isEmpty(str_mobileno)) {
            edtxt_fran_mobileno.setError(getResources().getString(R.string.error_required_mobile));
            edtxt_fran_mobileno.requestFocus();
        } else if ((str_mobileno.trim()).length() != 10) {
            edtxt_fran_mobileno.setError(getResources().getString(R.string.error_invalid_mobile));
            edtxt_fran_mobileno.requestFocus();
        } else if (TextUtils.isEmpty(str_pincode)) {
            edtxt_fran_pincode.setError(getResources().getString(R.string.error_required_pincode));
            edtxt_fran_pincode.requestFocus();
        } else if ((str_pincode.trim()).length() != 6) {
            edtxt_fran_pincode.setError(getResources().getString(R.string.error_invalid_pincode));
            edtxt_fran_pincode.requestFocus();
        } else if (TextUtils.isEmpty(str_address)) {
            edtxt_fran_address.setError(getResources().getString(R.string.error_required_address));
            edtxt_fran_address.requestFocus();
        } else if (StateCode.equalsIgnoreCase("0")) {
            AppUtils.alertDialog(Franchisee_Register_Activity.this, getResources().getString(R.string.error_required_state));
        } else if (citycode.equalsIgnoreCase("0")) {
            AppUtils.alertDialog(Franchisee_Register_Activity.this, getResources().getString(R.string.error_required_city));
        } else if (TextUtils.isEmpty(str_contact_person)) {
            edtxt_fran_contact_person.setError(getResources().getString(R.string.error_required_contact_person));
            edtxt_fran_contact_person.requestFocus();
        } else {
            if (AppUtils.isNetworkAvailable(Franchisee_Register_Activity.this)) {
                executeLoginRequest();
            } else {
                AppUtils.alertDialog(Franchisee_Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeLoginRequest() {
        try {
            if (AppUtils.isNetworkAvailable(Franchisee_Register_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Franchisee_Register_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            /*FranchiseeName:StateID:CityID:CityName:PinCode:Address:MobileNo:ContactPerson:*/
                            //     private String str_name, str_mobileno, str_pincode, str_address, str_contact_person, StateCode, state_name, citycode, city_name;
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FranchiseeName", "" + str_name));
                            postParameters.add(new BasicNameValuePair("StateID", "" + StateCode));
                            postParameters.add(new BasicNameValuePair("CityID", "" + citycode));
                            postParameters.add(new BasicNameValuePair("CityName", "" + city_name));
                            postParameters.add(new BasicNameValuePair("PinCode", "" + str_pincode));
                            postParameters.add(new BasicNameValuePair("Address", "" + str_address));
                            postParameters.add(new BasicNameValuePair("MobileNo", "" + str_mobileno));
                            postParameters.add(new BasicNameValuePair("ContactPerson", "" + str_contact_person));
                            postParameters.add(new BasicNameValuePair("SponsorFormno", "" + sponsor_form_no));
                            postParameters.add(new BasicNameValuePair("SponsorIDNO", "" + sponsor_id));

                            response = AppUtils.callWebServiceWithMultiParam(Franchisee_Register_Activity.this, postParameters, QueryUtils.methodFranchisee_Registration_New, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            AppUtils.dismissProgressDialog();

                            JSONObject jsonObject = new JSONObject(resultData);
                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                AppUtils.alertDialogWithFinish(Franchisee_Register_Activity.this, jsonObject.getString("Message"));
                            } else {
                                AppUtils.alertDialog(Franchisee_Register_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Franchisee_Register_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Franchisee_Register_Activity.this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Franchisee_Register_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void startSplash(final Intent intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MovetoNext(Intent intent) {
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////// ----------------------------- select state ----------
    private void Getstate() {
        StringRequest stringRequest = new StringRequest(AppUtils.serviceAPIURL() + "Master_FillState",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            String Status = j.getString("Status");
                            String Message = j.getString("Message");
                            Log.e("Message", Message);
                            if (Status.matches("True")) {
                                getStatename = j.getJSONArray("Data");
                                getStatename(getStatename);
                            } else {
                             /*   Snackbar snackbar = Snackbar.make(findViewById(R.id.register_parentLayout), Message, Snackbar.LENGTH_LONG);
                                View snackbarView = snackbar.getView();
                                snackbar.setActionTextColor(Color.RED);
                                View sbView = snackbar.getView();
                                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                textView.setTextColor(Color.BLACK);
                                snackbarView.setBackgroundColor(Color.parseColor("#ffd900"));
                                snackbar.show();*/
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getStatename(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {

                JSONObject json = j.getJSONObject(i);
                Log.e("val", json.toString());
                statlist.add(json.getString("State"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Franchisee_Register_Activity.this, android.R.layout.simple_spinner_dropdown_item, statlist);
        fran_state.setAdapter(adapter);

    }

    private String getStateCode(int position) {
        String name = "";
        try {
            JSONObject json = getStatename.getJSONObject(position);
            name = json.getString("STATECODE");
            state_name = json.getString("State");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    // -----------------  select city --------------------
    private void Getcity() {
        StringRequest stringRequest = new StringRequest(AppUtils.serviceAPIURL() + "Franchisee_CityList?StateID=" + StateCode,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            String Status = j.getString("Status");
                            String Message = j.getString("Message");
                            Log.e("Message", Message);
                            if (Status.matches("True")) {
                                getcityname = j.getJSONArray("Data");
                                getcityname(getcityname);
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getcityname(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {

                JSONObject json = j.getJSONObject(i);
                Log.e("val", json.toString());
                citylist.add(json.getString("CityName"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Franchisee_Register_Activity.this, android.R.layout.simple_spinner_dropdown_item, citylist);
        fran_city.setAdapter(adapter1);

    }

    private String getcityCode(int position) {
        String name = "";
        try {
            JSONObject json = getcityname.getJSONObject(position);
            name = json.getString("CityCode");
            city_name = json.getString("CityName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    /*Check Sponsor */
    public void executetoCheckSponsorName(final String sponsorid) {
        try {
            if (AppUtils.isNetworkAvailable(Franchisee_Register_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("SponsorID", "" + sponsorid));
                            response = AppUtils.callWebServiceWithMultiParam(Franchisee_Register_Activity.this, postParameters, QueryUtils.methodCheckSponsor, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Franchisee_Register_Activity.this);
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {

                            JSONArray jsonArrayData = new JSONArray(resultData);
                            JSONObject Jobject = jsonArrayData.getJSONObject(0);

                            setSponsorName(Jobject, sponsorid);


                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Franchisee_Register_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                AppUtils.alertDialog(Franchisee_Register_Activity.this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Franchisee_Register_Activity.this);
        }
    }

    public void setSponsorName(JSONObject jobject, String str_sponsorid) {
        try {
            if (jobject.getString("Status").equalsIgnoreCase("True")) {
                sponsor_form_no = jobject.getString("FormNo");
                sponsor_name = jobject.getString("MemName");
                txt_sponsor_name.setText(sponsor_name);
                txt_sponsor_name.setVisibility(View.VISIBLE);
                sponsor_id = str_sponsorid;
                button_franchisee_submit.setVisibility(View.VISIBLE);

            } else {
                txt_sponsor_name.setText(jobject.getString("Message"));
                txt_sponsor_name.setVisibility(View.VISIBLE);
                edtxt_sponsor_id.requestFocus();
                sponsor_id = "";
                button_franchisee_submit.setVisibility(View.GONE);
            }
        } catch (Exception ignored) {

        }
    }
}