package com.vpipl.mmtfranchisee;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.vpipl.mmtfranchisee.Adapters.ExpandableListAdapter;
import com.vpipl.mmtfranchisee.Utils.AppUtils;
import com.vpipl.mmtfranchisee.Utils.QueryUtils;
import com.vpipl.mmtfranchisee.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public DrawerLayout drawer;
    public NavigationView navigationView;
    public ActionBarDrawerToggle drawerToggle;
    TextView txt_welcome_name;
    private ArrayList<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ExpandableListView expListView;
    private int lastExpandedPosition = -1;

    private TextView txt_f_code;
    private TextView txt_joining_Date;
    private TextView txt_mobile;
    private TextView txt_welcome_name_header;

    TextView txt_total_commission;

    private TextView txt_pending_report;
    private TextView txt_approved_requests;
    private TextView txt_rejected_requests;
    private TextView txt_total_requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {
            AppUtils.setActionbarTitle(getSupportActionBar(), HomeActivity.this);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);


            txt_f_code = (TextView) findViewById(R.id.txt_f_code);
            txt_joining_Date = (TextView) findViewById(R.id.txt_joining_Date);
            txt_mobile = (TextView) findViewById(R.id.txt_mobile);
            txt_welcome_name_header = (TextView) findViewById(R.id.txt_welcome_name_header);

            txt_total_commission = (TextView) findViewById(R.id.txt_total_commission);
            txt_pending_report = (TextView) findViewById(R.id.txt_pending_report);
            txt_approved_requests = (TextView) findViewById(R.id.txt_approved_requests);
            txt_rejected_requests = (TextView) findViewById(R.id.txt_rejected_requests);
            txt_total_requests = (TextView) findViewById(R.id.txt_total_requests);


            txt_f_code.setText(AppController.getSpUserInfo().getString(SPUtils.USER_FCode, ""));
            txt_mobile.setText(AppController.getSpUserInfo().getString(SPUtils.USER_Mobile, ""));
            txt_welcome_name_header.setText("Welcome " + AppController.getSpUserInfo().getString(SPUtils.USER_NAME, ""));

            View navHeaderView = navigationView.getHeaderView(0);
            txt_welcome_name = (TextView) navHeaderView.findViewById(R.id.txt_welcome_name);
            txt_welcome_name.setText("Welcome " + AppController.getSpUserInfo().getString(SPUtils.USER_NAME, ""));

            expListView = (ExpandableListView) findViewById(R.id.left_drawer);

            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(drawerToggle);
            drawerToggle.syncState();

            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();

            enableExpandableList();

            if (AppUtils.isNetworkAvailable(this))
                executeDashboardDetails();
            else
                AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeDashboardDetails() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String response = "";
                try {
                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("FranchiseeCode", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FCode, "")));
                    response = AppUtils.callWebServiceWithMultiParam(HomeActivity.this, postParameters, QueryUtils.methodToGetFranchiseeForDashboardTotal, "Inbox_Activity");
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
                        JSONArray jsonArrayOld = jsonObject.getJSONArray("Data");
                        getTipsResults(jsonArrayOld);
                    } else {
                        AppUtils.alertDialog(HomeActivity.this, jsonObject.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getTipsResults(JSONArray jsonArrayNewTips) {
        try {
            JSONObject jsonObject = jsonArrayNewTips.getJSONObject(0);

            txt_total_commission.setText("" + jsonObject.getString("CommissionAmount"));
            if (txt_total_commission.getText().toString().equalsIgnoreCase("null"))
                txt_total_commission.setText("0");
            txt_pending_report.setText("" + jsonObject.getString("Total Pending"));
            txt_approved_requests.setText("" + jsonObject.getString("Total Approve"));
            txt_rejected_requests.setText("" + jsonObject.getString("Total Rejected"));
            txt_total_requests.setText("" + jsonObject.getString("Total Request"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showExitDialog() {
        try {
            final Dialog dialog = AppUtils.createDialog(HomeActivity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml("Are you sure ? Do you want to Exit ?"));

            Button txt_submit = (Button) dialog.findViewById(R.id.button);
            txt_submit.setText("Exit");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.button2);
            txt_cancel.setText("Cancel");
            txt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(navigationView)) {
            drawer.closeDrawer(navigationView);
        } else
            showExitDialog();
    }

    private void enableExpandableList() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        List<String> Empty = new ArrayList<>();

        listDataHeader.add("Bill Request List");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        listDataHeader.add("Bill Request Detail Report");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        listDataHeader.add("Generate QR Code");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        listDataHeader.add("Logout");
        listDataChild.put(listDataHeader.get(listDataHeader.size() - 1), Empty);

        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                String GroupTitle = listDataHeader.get(groupPosition);

                if (GroupTitle.trim().equalsIgnoreCase("Bill Request List")) {
                    startActivity(new Intent(HomeActivity.this, Bill_request_detail_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Bill Request Detail Report")) {
                    startActivity(new Intent(HomeActivity.this, Bill_request_detail_report_Activity.class));
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Generate QR Code")) {
                 //   Intent intent = new Intent(HomeActivity.this, QrGenerate.class);
                    Intent intent = new Intent(HomeActivity.this, com.vpipl.mmtfranchisee.GeneratorActivity.class);
                    startActivity(intent);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                } else if (GroupTitle.trim().equalsIgnoreCase("Logout")) {
                    AppUtils.showDialogSignOut(HomeActivity.this);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }

        });

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {


                String ChildItemTitle = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
//               if (ChildItemTitle.trim().equalsIgnoreCase("Bill Request List")) {
//                    startActivity(new Intent(HomeActivity.this, Bill_request_detail_Activity.class));
//                } else if (ChildItemTitle.trim().equalsIgnoreCase("Bill Request Detail Report")) {
//                    startActivity(new Intent(HomeActivity.this, Bill_request_detail_report_Activity.class));
//                }

                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                if (drawer.isDrawerOpen(navigationView)) {
                    drawer.closeDrawer(navigationView);
                } else {
                    drawer.openDrawer(navigationView);
                }
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        executeDashboardDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();
        executeDashboardDetails();
    }
}