package com.vpipl.mmtfranchisee.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.vpipl.mmtfranchisee.AppController;
import com.vpipl.mmtfranchisee.R;
import com.vpipl.mmtfranchisee.Register_User_Activity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by admin on 29-04-2017.
 */
public class AppUtils {

    public static ProgressDialog progressDialog;
    public static boolean showLogs = true;
    public static String mPINCodePattern = "^[1-9][0-9]{5}$";

    /*    <string name="serviceAPIURL">http://mmtbusiness.in/webservice/Service.asmx/</string>*/

    public static String serviceAPIURL = "http://mmtbusiness.in//Webservice/Service.asmx/";

    public static String serviceAPIURL() {
        return serviceAPIURL;
    }

    public static void hideKeyboardOnClick(Context con, View view) {
        try {
            InputMethodManager inputManager = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(con);
        }
    }





    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
        return false;
    }


    public static void showExceptionDialog(Context con) {
        try {
            AppUtils.dismissProgressDialog();
            AppUtils.alertDialog(con, "Sorry, There seems to be a problem. Try again later");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProgressDialog(Context conn) {
        try {
            if (progressDialog != null) {
                if (!progressDialog.isShowing()) {
                    if (!((Activity) conn).isFinishing()) {
                        progressDialog.show();
                    }
                }
            } else {

//                progressDialog = new SpotsDialog(conn, R.style.Custom);

                progressDialog = new ProgressDialog(conn);
                progressDialog.setMessage("Processing...");
//                progressDialog.setTitle("Processing...");
//                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

//                progressDialog = new ProgressDialog(conn);
//                progressDialog.setMessage("Please Wait...");
//                progressDialog.setTitle("Processing...");
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                progressDialog.setIndeterminate(true);
//                progressDialog.setCancelable(false);
//                progressDialog.setInverseBackgroundForced(false);

                progressDialog.show();

                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismissProgressDialog() {
        try {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void alertDialog(Context context, String message) {
        try {
            final Dialog dialog = createDialog(context, true);
            TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setText(message);

            dialog.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Dialog createDialog(Context context, boolean single) {
        final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        if (single)
            dialog.setContentView(R.layout.custom_dialog_one);
        else
            dialog.setContentView(R.layout.custom_dialog_two);

        return dialog;
    }

    public static boolean isValidMail(String email) {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void setActionbarTitle(ActionBar bar, Context con) {
        try {

            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setTitle(""+con.getResources().getString(R.string.app_name) );
            bar.setSubtitle("");

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(con);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
        return connected;
    }

    public static void showDialogSignOut(final Context con) {
        try {
            final Dialog dialog = AppUtils.createDialog(con, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml(con.getResources().getString(R.string.txt_signout_message)));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.button);
            txt_submit.setText("Yes");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();

                        AppController.getSpUserInfo().edit().clear().commit();
                        AppController.getSpIsLogin().edit().clear().commit();

                        AppController.getSpIsInstall().edit().putBoolean(SPUtils.IS_LOGIN, false).commit();

                        Intent intent = new Intent(con, Register_User_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("SendToHome",true);
                        con.startActivity(intent);
                        ((Activity) con).finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView txt_cancel = (TextView) dialog.findViewById(R.id.button2);
            txt_cancel.setText("No");
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
            AppUtils.showExceptionDialog(con);
        }
    }


    public static String getDateandTimeFromAPIDate(String date) {
        try {

            if (AppUtils.showLogs) Log.v("getFormatDate", "before date.." + date);
            Calendar cal = Calendar.getInstance();

            if (date.contains("/Date("))
                cal.setTimeInMillis(Long.parseLong(date.replace("/Date(", "").replace(")/", "")));
            else
                cal.setTimeInMillis(Long.parseLong(date.replace("/date(", "").replace(")/", "")));

            date = DateFormat.format("dd MMM yyyy", cal).toString();

            if (AppUtils.showLogs) Log.v("getFormatDate", "after date.." + date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String callWebServiceWithMultiParam(Context con, List<NameValuePair> postParameters, String methodName, String pageName) {
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();

            if (AppUtils.showLogs)
                Log.e(pageName, "Executing URL..." + con.getResources().getString(R.string.serviceAPIURL) + methodName);
            AppUtils.printQuery(pageName, postParameters);

            HttpPost request = new HttpPost
                    (con.getResources().getString(R.string.serviceAPIURL) + methodName);
            UrlEncodedFormEntity formEntity = null;
            try {
                formEntity = new UrlEncodedFormEntity(postParameters);
            } catch (Exception e) {
                e.printStackTrace();
            }

            request.setEntity(formEntity);
            HttpResponse response = null;
            try {
                response = client.execute(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            String line;
            String NL = System.getProperty("line.separator");
            try {
                while ((line = in.readLine()) != null) {
                    sb.append(line).append(NL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String result = null;

            try {
                if (AppUtils.showLogs) Log.e(pageName + "", "Response..... " + sb.toString());
                result = sb.toString();
//                result = result.replaceFirst("\"","");
//                result = result.replaceAll("\"$","");

                Log.d("Result", result);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void alertDialogWithFinish(final Context context, String message) {
        try {
            final Dialog dialog = createDialog(context, true);
            TextView dialog4all_txt = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            dialog4all_txt.setText(message);
            dialog.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ((Activity) context).finish();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(context);
        }
    }

    public static void printQuery(String pageName, List<NameValuePair> postParam) {
        try {
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < postParam.size(); i++) {
                query.append(" ").append(postParam.get(i).getName()).append(" : ").append(postParam.get(i).getValue());
            }

            if (AppUtils.showLogs) Log.e(pageName, "Executing Query..." + query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}