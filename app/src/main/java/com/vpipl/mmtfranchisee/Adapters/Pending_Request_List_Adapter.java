package com.vpipl.mmtfranchisee.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.vpipl.mmtfranchisee.AppController;
import com.vpipl.mmtfranchisee.R;
import com.vpipl.mmtfranchisee.Utils.AppUtils;
import com.vpipl.mmtfranchisee.Utils.QueryUtils;
import com.vpipl.mmtfranchisee.Utils.SPUtils;
import com.vpipl.mmtfranchisee.modal.RequestList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC14 on 11-Apr-16.
 */
public class Pending_Request_List_Adapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater = null;

    private List<RequestList> tableLists;

    public Pending_Request_List_Adapter(Context context, List<RequestList> tableLists) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tableLists = tableLists;
    }

    @Override
    public int getCount() {
        return tableLists.size();
    }

    @Override
    public Object getItem(int position) {
        return tableLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            final Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.current_order_list_adapter, parent, false);
                holder = new Holder();

                holder.order_number = (TextView) convertView.findViewById(R.id.order_number);
                holder.bill_number = (TextView) convertView.findViewById(R.id.bill_number);
                holder.bill_date = (TextView) convertView.findViewById(R.id.bill_date);
                holder.customer_name = (TextView) convertView.findViewById(R.id.customer_name);
                holder.order_amount = (TextView) convertView.findViewById(R.id.order_amount);

                holder.deliver = (Button) convertView.findViewById(R.id.deliver);
                holder.deliver2 = (Button) convertView.findViewById(R.id.deliver2);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }


            holder.customer_name.setText("Name : " + tableLists.get(position).getCustomerName().trim());
            holder.order_number.setText("Request Number : " + tableLists.get(position).getRequestId().trim());
            holder.order_amount.setText("Amount : " + tableLists.get(position).getBillAmount().trim());
//            holder.bill_number.setText("Bill Number : " + tableLists.get(position).getBillNo().trim());
            holder.bill_date.setText("Request Date : " + tableLists.get(position).getBillDate().trim());

            holder.deliver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executeToGetPendingListRequest(tableLists.get(position).getRequestId(), "C", position);
                }
            });

            holder.deliver2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executeToGetPendingListRequest(tableLists.get(position).getRequestId(), "R",  position);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
        return convertView;
    }

    private static class Holder {
        TextView order_number, bill_number, bill_date, customer_name, order_amount;
        Button deliver, deliver2;
    }


    private void executeToGetPendingListRequest(final String AID,final String Status,final int Position) {
        try {
            if (AppUtils.isNetworkAvailable(context)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(context);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("FranchiseeCode", "" + AppController.getSpUserInfo().getString(SPUtils.USER_FCode, "")));
                            postParameters.add(new BasicNameValuePair("AID", AID));
                            postParameters.add(new BasicNameValuePair("Status", Status));
                            postParameters.add(new BasicNameValuePair("VendorSponsor", "" + AppController.getSpUserInfo().getString(SPUtils.USER_SponsorFormno, "")));

                            response = AppUtils.callWebServiceWithMultiParam(context, postParameters, QueryUtils.methodToGetApprove_Reject_PurchaseBillNew, "Bill_request_detail_Activity");
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
                                tableLists.remove(Position);
                                notifyDataSetChanged();
                                AppUtils.alertDialogWithFinish(context,jsonObject.getString("Message") );
                            }
                            else {
                                AppUtils.alertDialog(context,jsonObject.getString("Message") );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(context);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
    }





}