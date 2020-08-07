package com.vpipl.mmtfranchisee;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vpipl.mmtfranchisee.Utils.AppUtils;
import com.vpipl.mmtfranchisee.Utils.QueryUtils;
import com.vpipl.mmtfranchisee.Utils.SPUtils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Bill_request_detail_report_Activity extends AppCompatActivity {

    String TAG = "Bill_request_detail_report_Activity";

    Button btn_proceed, btn_load_more, button_load_less;
    TextView text_pg_number;

    DatePickerDialog datePickerDialog;

    private Calendar myCalendar;
    private SimpleDateFormat sdf;
    String whichdate = "";

    TextInputEditText txt_from_joining, txt_to_joining, txt_package_Name;
    RadioGroup rg_view_detail_for;

    int StartedRow = 0;
    int PageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_request_detail_report);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.setActionbarTitle(getSupportActionBar(), this);

            txt_from_joining = (TextInputEditText) findViewById(R.id.txt_from_joining);
            txt_to_joining = (TextInputEditText) findViewById(R.id.txt_to_joining);
            txt_package_Name = (TextInputEditText) findViewById(R.id.txt_package_Name);
            rg_view_detail_for = (RadioGroup) findViewById(R.id.rg_view_detail_for);

            myCalendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("dd MMM yyyy");

            txt_from_joining.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_from_joining";
                    showdatePicker();
                }
            });

            txt_to_joining.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    whichdate = "txt_to_joining";
                    showdatePicker();
                }
            });

            text_pg_number = (TextView) findViewById(R.id.text_pg_number);
            btn_proceed = (Button) findViewById(R.id.btn_proceed);
            btn_load_more = (Button) findViewById(R.id.btn_load_more);
            button_load_less = (Button) findViewById(R.id.button_load_less);

            btn_load_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PageIndex = PageIndex + 1;
                    StartedRow = StartedRow + 25;
                    createLevelDetailFullViewRequest();
                }
            });

            button_load_less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (PageIndex > 1 && StartedRow > 25) {
                        PageIndex = PageIndex - 1;
                        StartedRow = StartedRow - 25;
                        createLevelDetailFullViewRequest();
                    }
                }
            });

            btn_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PageIndex = 1;
                    createLevelDetailFullViewRequest();
                }
            });

            if (AppUtils.isNetworkAvailable(this)) {
                createLevelDetailFullViewRequest();
            } else {
                AppUtils.alertDialogWithFinish(this, getResources().getString(R.string.txt_networkAlert));
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(this);
        }
    }

    private void createLevelDetailFullViewRequest() {

        findViewById(R.id.ll_showData).setVisibility(View.GONE);

        int selectedIdTwo = rg_view_detail_for.getCheckedRadioButtonId();
        RadioButton radioButtonTwo = (RadioButton) findViewById(selectedIdTwo);
        String view_detail_side = radioButtonTwo.getText().toString();

        String side = "0";

        if (view_detail_side.equalsIgnoreCase("Approved"))
            side = "1";
        else if (view_detail_side.equalsIgnoreCase("Rejected"))
            side = "2";

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("FranchiseeCode", AppController.getSpUserInfo().getString(SPUtils.USER_FCode, "")));
//        postParameters.add(new BasicNameValuePair("LegNo", "" + side));
//        postParameters.add(new BasicNameValuePair("StartedRow", "" + StartedRow));
        executeLevelDetailFullViewRequest(postParameters);
    }

    private void executeLevelDetailFullViewRequest(final List postparameters) {
        try {
            if (AppUtils.isNetworkAvailable(Bill_request_detail_report_Activity.this)) {
                new AsyncTask<Void, Void, String>() {
                    protected void onPreExecute() {
                        AppUtils.showProgressDialog(Bill_request_detail_report_Activity.this);
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = "";
                        try {
                            response = AppUtils.callWebServiceWithMultiParam(Bill_request_detail_report_Activity.this, postparameters, QueryUtils.methodRequestDetailReport, TAG);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        AppUtils.dismissProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                            if (jsonObject.getString("Status").equalsIgnoreCase("True")) {

                                WriteValues(jsonArrayData);

                            } else {
                                AppUtils.alertDialog(Bill_request_detail_report_Activity.this, jsonObject.getString("Message"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Bill_request_detail_report_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Bill_request_detail_report_Activity.this);
        }
    }

    public void WriteValues(JSONArray jsonArrayDownLineDetail) {
        findViewById(R.id.ll_showData).setVisibility(View.VISIBLE);

        try {
            float sp = 8;
            int px = (int) (sp * getResources().getDisplayMetrics().scaledDensity);
            int px_right = (int) (15 * getResources().getDisplayMetrics().scaledDensity);

            if (jsonArrayDownLineDetail.length() > 0) {
                text_pg_number.setText("" + PageIndex);

                if (PageIndex <= 1)
                    button_load_less.setVisibility(View.GONE);
                else
                    button_load_less.setVisibility(View.VISIBLE);

                TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
                ll.removeAllViews();

                TableRow row1 = new TableRow(this);
                TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                row1.setLayoutParams(lp1);
                row1.setBackgroundColor(Color.parseColor("#EE5252"));


                TextView A1 = new TextView(this);
//                TextView B1 = new TextView(this);
                TextView C1 = new TextView(this);
                TextView D1 = new TextView(this);
                TextView E1 = new TextView(this);
                TextView F1 = new TextView(this);
                TextView G1 = new TextView(this);

                A1.setText("Request No");
//                B1.setText("Bill No");
                C1.setText("Request Date");
                D1.setText("Customer Name");
                E1.setText("Amount");
                F1.setText("Commission");
                G1.setText("Status");

                A1.setPadding(px, px, px, px);
//                B1.setPadding(px, px, px, px);
                C1.setPadding(px, px, px, px);
                D1.setPadding(px, px, px, px);
                E1.setPadding(px, px, px, px);
                G1.setPadding(px, px, px, px);
                F1.setPadding(px, px, px_right, px);

                A1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//                B1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                C1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                D1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                E1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                F1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                G1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                A1.setGravity(Gravity.CENTER);
//                B1.setGravity(Gravity.CENTER);
                C1.setGravity(Gravity.CENTER);
                D1.setGravity(Gravity.CENTER);
                E1.setGravity(Gravity.CENTER);
                F1.setGravity(Gravity.CENTER);
                G1.setGravity(Gravity.CENTER);

                A1.setTextColor(Color.WHITE);
//                B1.setTextColor(Color.BLACK);
                C1.setTextColor(Color.WHITE);
                D1.setTextColor(Color.WHITE);
                E1.setTextColor(Color.WHITE);
                F1.setTextColor(Color.WHITE);
                G1.setTextColor(Color.WHITE);



                row1.addView(A1);
//                row1.addView(B1);
                row1.addView(C1);
                row1.addView(D1);
                row1.addView(E1);
                row1.addView(F1);
                row1.addView(G1);

                View view = new View(this);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                view.setBackgroundColor(Color.parseColor("#999999"));

                ll.addView(row1);
                ll.addView(view);

                for (int i = 0; i < jsonArrayDownLineDetail.length(); i++) {
                    try {
                        JSONObject jobject = jsonArrayDownLineDetail.getJSONObject(i);

                        String RequestNo = jobject.getString("AID");
//                        String BillNo = jobject.getString("BillNo");
                        String CustomerName= WordUtils.capitalizeFully(jobject.getString("Name"));
                        String BillDate  = AppUtils.getDateandTimeFromAPIDate(jobject.getString("BillDate"));
                        String BillAmount = jobject.getString("Amount");
                        String Commission = jobject.getString("CommissionAmount");
                        String Status = jobject.getString("Status");

                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0,px,0,px);
                        row.setLayoutParams(lp);
                        row.setPadding(0,px,0,px);

                        if (i % 2 == 0)
                            row.setBackgroundColor(Color.parseColor("#eeeeee"));
                        else
                            row.setBackgroundColor(Color.WHITE);

                        TextView A = new TextView(this);
//                        TextView B = new TextView(this);
                        TextView C = new TextView(this);
                        TextView D = new TextView(this);
                        TextView E = new TextView(this);
                        TextView F = new TextView(this);
                        TextView G = new Button(this);

                        A.setText(RequestNo);
//                        B.setText(BillNo);
                        C.setText(BillDate);
                        D.setText(CustomerName);
                        E.setText(BillAmount);
                        F.setText(Commission);

                        G.setText(Status);
                        G.setClickable(false);
                        G.setMinHeight(0);
                        G.setTextColor(Color.WHITE);

                        if (G.getText().toString().equalsIgnoreCase("Approved"))
                            G.setBackground(getResources().getDrawable(R.drawable.round_rectangle_orange));
//                            G.setBackgroundColor(Color.parseColor("#68BA56"));
                        else if (G.getText().toString().equalsIgnoreCase("Pending"))
//                            G.setBackgroundColor(Color.parseColor("#FFAC4A"));
                            G.setBackground(getResources().getDrawable(R.drawable.round_rectangle_yellow));
                        else
                            G.setBackground(getResources().getDrawable(R.drawable.round_rectangle_red));


                        A.setGravity(Gravity.CENTER);
//                        B.setGravity(Gravity.CENTER);
                        C.setGravity(Gravity.CENTER);
                        D.setGravity(Gravity.CENTER);
                        E.setGravity(Gravity.CENTER);
                        F.setGravity(Gravity.CENTER);
                        G.setGravity(Gravity.CENTER);

                        A.setPadding(px, px, px, px);
//                        B.setPadding(px, px, px, px);
                        C.setPadding(px, px, px, px);
                        D.setPadding(px, px, px, px);
                        E.setPadding(px, px, px, px);
                        F.setPadding(px, px, px, px);
                        G.setPadding((px), (px), (px), (px));

                        A.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
//                        B.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        C.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        D.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        E.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        F.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        G.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

                        row.addView(A);
//                        row.addView(B);
                        row.addView(C);
                        row.addView(D);
                        row.addView(E);
                        row.addView(F);
                        row.addView(G);

                        View view_one = new View(this);
                        view_one.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                        view_one.setBackgroundColor(Color.parseColor("#dddddd"));

                        ll.addView(row);
                        ll.addView(view_one);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        }
        return super.onOptionsItemSelected(item);
    }


    private void showdatePicker() {
        datePickerDialog = new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }


    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (whichdate.equalsIgnoreCase("txt_from_joining")) {
                txt_from_joining.setText(sdf.format(myCalendar.getTime()));
                txt_to_joining.setText(sdf.format(myCalendar.getTime()));
            } else if (whichdate.equalsIgnoreCase("txt_to_joining"))
                txt_to_joining.setText(sdf.format(myCalendar.getTime()));

        }
    };

}
