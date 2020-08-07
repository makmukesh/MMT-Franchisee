package com.vpipl.mmtfranchisee;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.vpipl.mmtfranchisee.Utils.AppUtils;
import com.vpipl.mmtfranchisee.Utils.QueryUtils;
import com.vpipl.mmtfranchisee.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Register_User_Activity extends AppCompatActivity {

    private static final String TAG = "Register_User_Activity";

    Button button_login;
    Button button_franchisee_registration;
    private TextInputEditText edtxt_userid_member, edtxt_password_member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(" "+getResources().getString(R.string.app_name));
        getSupportActionBar().setSubtitle("");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        edtxt_userid_member = (TextInputEditText) findViewById(R.id.edtxt_userid_member);
        edtxt_password_member = (TextInputEditText) findViewById(R.id.edtxt_password_member);
        button_login = (Button) findViewById(R.id.button_login);
        button_franchisee_registration = (Button) findViewById(R.id.button_franchisee_registration);

        String useridmember = AppController.getSpIsInstall().getString(SPUtils.IS_INSTALL_MobileNo, "");
        edtxt_userid_member.setText(useridmember);

        edtxt_password_member.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    ValidateData();
                    return true;
                }
                return false;
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Register_User_Activity.this, view);
                ValidateData();
            }
        });
        button_franchisee_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtils.hideKeyboardOnClick(Register_User_Activity.this, view);
               startActivity(new Intent(Register_User_Activity.this , Franchisee_Register_Activity.class));
            }
        });
    }

    private void ValidateData() {
        edtxt_userid_member.setError(null);
        edtxt_password_member.setError(null);

        String userid = edtxt_userid_member.getText().toString();
        String password = edtxt_password_member.getText().toString();

        if (TextUtils.isEmpty(userid)) {
            edtxt_userid_member.setError(getResources().getString(R.string.error_required_mobile_number));
            edtxt_userid_member.requestFocus();
//        } else if ((userid.trim()).length() != 10) {
//            edtxt_userid_member.setError(getResources().getString(R.string.error_invalid_mobile_number));
//            edtxt_userid_member.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            edtxt_password_member.setError("Enter Password");
                edtxt_password_member.requestFocus();
            } else {
                if (AppUtils.isNetworkAvailable(Register_User_Activity.this))
                {
                    executeLoginRequest(userid, password);
                } else {
                    AppUtils.alertDialog(Register_User_Activity.this, getResources().getString(R.string.txt_networkAlert));
                }
            }
}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void executeLoginRequest(final String userId, final String passwd) {
        try {
            if (AppUtils.isNetworkAvailable(Register_User_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Register_User_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FranchiseeCode", userId));
                            postParameters.add(new BasicNameValuePair("Password", passwd));

                            response = AppUtils.callWebServiceWithMultiParam(Register_User_Activity.this, postParameters, QueryUtils.methodFranchiseeLogin, TAG);

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
                            if (jsonObject.getString("Status").equalsIgnoreCase("True"))
                            {
                                JSONArray jsonArrayData = jsonObject.getJSONArray("Data");
                                saveLoginUserInfo(jsonArrayData);

                            } else {
                                AppUtils.alertDialog(Register_User_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Register_User_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_User_Activity.this);
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
            AppUtils.showExceptionDialog(Register_User_Activity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveLoginUserInfo(final JSONArray jsonArray) {
        try {
            AppUtils.dismissProgressDialog();

                String memberuserid = edtxt_userid_member.getText().toString();
                AppController.getSpIsInstall().edit().putString(SPUtils.IS_INSTALL_MobileNo, memberuserid).commit();
                AppController.getSpIsInstall().edit().putBoolean(SPUtils.IS_INSTALL, true).commit();


            AppController.getSpUserInfo().edit().clear().commit();


                AppController.getSpUserInfo().edit()

                        .putString(SPUtils.USER_FCode, jsonArray.getJSONObject(0).getString("UserPartyCode"))
                        .putString(SPUtils.USER_NAME, jsonArray.getJSONObject(0).getString("PartyName"))
                        .putString(SPUtils.USER_State, jsonArray.getJSONObject(0).getString("STateName"))
                        .putString(SPUtils.USER_City, jsonArray.getJSONObject(0).getString("CityName"))
                        .putString(SPUtils.USER_Address, jsonArray.getJSONObject(0).getString("Address1"))
                        .putString(SPUtils.USER_Mobile, jsonArray.getJSONObject(0).getString("MobileNo"))
                        .putString(SPUtils.USER_Email, jsonArray.getJSONObject(0).getString("E_MailAdd"))
                        .putString(SPUtils.USER_SponsorIDNO, jsonArray.getJSONObject(0).getString("SponsorIDNO"))
                        .putString(SPUtils.USER_SponsorFormno, jsonArray.getJSONObject(0).getString("SponsorFormno"))
                        .commit();

                startSplash(new Intent(Register_User_Activity.this, HomeActivity.class));

                AppController.getSpIsLogin().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();
                AppController.getSpIsInstall().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

            AppController.getSpIsInstall().edit().putBoolean(SPUtils.IS_LOGIN, true).commit();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Register_User_Activity.this);
        }
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
}