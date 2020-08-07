package com.vpipl.mmtfranchisee;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.vpipl.mmtfranchisee.Adapters.Pending_Request_List_Adapter;
import com.vpipl.mmtfranchisee.Utils.AppUtils;
import com.vpipl.mmtfranchisee.Utils.QueryUtils;
import com.vpipl.mmtfranchisee.Utils.SPUtils;
import com.vpipl.mmtfranchisee.modal.RequestList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Bill_request_detail_Activity extends AppCompatActivity {

    ListView listView;
    private TextView layout_noData;
    private Pending_Request_List_Adapter adapter;

    public List<RequestList> CurrentPendingRequestList = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
               onBackPressed();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_request_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        AppUtils.setActionbarTitle(getSupportActionBar(), this);

        layout_noData = (TextView) findViewById(R.id.layout_noData);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new Pending_Request_List_Adapter(this, CurrentPendingRequestList);
        listView.setAdapter(adapter);


        if (AppUtils.isNetworkAvailable(this)) {

            executeToGetPendingListRequest();

        } else {
            AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));
        }

    }

    private void executeToGetPendingListRequest() {
        try {
            if (AppUtils.isNetworkAvailable(this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Bill_request_detail_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FranchiseeCode", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FCode, "")));
                            response = AppUtils.callWebServiceWithMultiParam(Bill_request_detail_Activity.this, postParameters, QueryUtils.methodCurrentPendingRequests, "Bill_request_detail_Activity");
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
                                JSONArray jsonArrayOrderList = jsonObject.getJSONArray("Data");

                                if (jsonArrayOrderList.length() > 0) {
                                    saveOrderList(jsonArrayOrderList);
                                } else {
                                    noDataFound();
                                }
                            } else {
                                noDataFound();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Bill_request_detail_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_request_detail_Activity.this);
        }
    }

    private void noDataFound() {
        try {
            layout_noData.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveOrderList(final JSONArray jsonArrayOrderList) {
        try {
            CurrentPendingRequestList.clear();

            for (int i = 0; i < jsonArrayOrderList.length(); i++)
            {
                JSONObject jsonObjectOrders = jsonArrayOrderList.getJSONObject(i);

                String RequestId = "" + jsonObjectOrders.getString("AID");
//                String BillNo = "" + jsonObjectOrders.getString("BillNo");
                String BillDate = "" + AppUtils.getDateandTimeFromAPIDate(jsonObjectOrders.getString("BillDate"));
                String CustomerName = "" + jsonObjectOrders.getString("Name");
                String CustomerIdno = "" + jsonObjectOrders.getString("IDNo");
                String CustomerFormNo = "" + jsonObjectOrders.getString("FormNo");
                String BillAmount = "" + jsonObjectOrders.getString("Amount");
                String CommissionAmount = "" + jsonObjectOrders.getString("CommissionAmount");
                String Status = "" + jsonObjectOrders.getString("Status");
                String ApproveDate = "" + jsonObjectOrders.getString("IsApproveDate");



                RequestList list = new RequestList(RequestId,"",BillDate,  CustomerName,CustomerIdno,  CustomerFormNo, BillAmount, CommissionAmount, Status, ApproveDate);

                CurrentPendingRequestList.add(list);
            }

            setData();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_request_detail_Activity.this);
        }
    }


    private void setData() {
        try {

            layout_noData.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            adapter.notifyDataSetChanged();

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
