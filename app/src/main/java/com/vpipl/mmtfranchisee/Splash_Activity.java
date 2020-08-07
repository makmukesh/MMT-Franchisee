package com.vpipl.mmtfranchisee;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.mmtfranchisee.Utils.AppUtils;
import com.vpipl.mmtfranchisee.Utils.QueryUtils;
import com.vpipl.mmtfranchisee.Utils.SPUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;

import static android.view.View.GONE;

public class Splash_Activity extends AppCompatActivity {

    private final String TAG = "Splash_Activity";

    String[] PermissionGroup = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.GET_TASKS};

    String version;
    String currentVersion, latestVersion;

    String mobilenumber = "";
    private int versionCode;

    public final static int REQUEST_CODE = 110;

    ProgressDialog progressDialog;
    DownloadManager downloadManager;
    long downloadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        try {
            PackageManager manager = getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            version = info.versionName;
            versionCode = info.versionCode;


            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            String DeviceModel = manufacturer + " " + model;

            AppController.getSpIsInstall()
                    .edit().putString(SPUtils.IS_INSTALL_DeviceModel, "" + DeviceModel)
                    .putString(SPUtils.IS_INSTALL_DeviceName, "" + DeviceModel)
                    .commit();

            mobilenumber = AppController.getSpIsInstall().getString(SPUtils.IS_INSTALL_MobileNo, "");

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);

            } else if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//moveNextScreen();
               executesGetAppStatusRequest();

            } else {
                ActivityCompat.requestPermissions(this, PermissionGroup, 84);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {

                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                           executesGetAppStatusRequest();
                    } else {
                        ActivityCompat.requestPermissions(this, PermissionGroup, 84);
                    }

                } else {
                    System.exit(0);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 84) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                executesGetAppStatusRequest();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SYSTEM_ALERT_WINDOW)
                        ) {
                    showDialogOK(
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            ActivityCompat.requestPermissions(Splash_Activity.this, PermissionGroup, 84);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            });
                } else {
                    AppUtils.alertDialogWithFinish(this, "Go to settings and Enable these permissions");
                }
            }
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("These Permissions are mandatory to use App.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

     public void executesToGetVersion() {
        try {
            if (AppUtils.isNetworkAvailable(Splash_Activity.this)) {
                new AsyncTask<Void, Void, String>() {

                    @Override
                    protected String doInBackground(Void... params) {
                        String response = null;
                        try {

                            PackageInfo pInfo = Splash_Activity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                            int versionCode = pInfo.versionCode;

                            List<NameValuePair> postParameters = new ArrayList<>();
                            postParameters.add(new BasicNameValuePair("Versioninfo", "" + versionCode));
                            response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodToGetVersion, TAG);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return response;
                    }

                    @Override
                    protected void onPostExecute(String resultData) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultData);
                            JSONArray jsonArray = jsonObject.getJSONArray("Data");
                            JSONObject object = jsonArray.getJSONObject(0);

                            if (object.getString("Status").equalsIgnoreCase("False")) {
                                showUpdateDialog(object.getString("Msg"), object.getString("AppDownloadURL"));
                            } else {
                                moveNextScreen();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppUtils.showExceptionDialog(Splash_Activity.this);
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    public void showUpdateDialog(String Msg, final String DownloadLink) {
        try {
            final Dialog dialog = AppUtils.createDialog(Splash_Activity.this, true);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Msg);

            Button txt_submit = (Button) dialog.findViewById(R.id.button);
            txt_submit.setText("Update Now");
            txt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        dialog.dismiss();
                        finish();

                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    private void moveNextScreen() {
        try {
            if (!AppController.getSpIsInstall().getBoolean(SPUtils.IS_LOGIN, false))
            {
                startSplash(new Intent(Splash_Activity.this, Register_User_Activity.class));
            } else {
                startSplash(new Intent(Splash_Activity.this, HomeActivity.class));
            }

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }

    private void startSplash(final Intent intent1) {
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    finish();
                }
            };
            new Handler().postDelayed(runnable, 2000);

        } catch (Exception e) {
            e.printStackTrace();
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }
    /*App Update */ 
    public void executesGetAppStatusRequest() {
    try {
        if (AppUtils.isNetworkAvailable(Splash_Activity.this)) {
            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... params) {
                    String response = null;
                    try {
                        List<NameValuePair> postParameters = new ArrayList<>();
                        response = AppUtils.callWebServiceWithMultiParam(Splash_Activity.this, postParameters, QueryUtils.methodToCheckFranchiseeAppRunningStatus, TAG);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return response;
                }

                @Override
                protected void onPostExecute(String resultData) {
                    try {

                        JSONObject jsonObject = new JSONObject(resultData);
                        JSONArray jsonArrayData = jsonObject.getJSONArray("Data");

                        if (jsonArrayData.getJSONObject(0).getString("Status").equalsIgnoreCase("False")) {
                            showUpdateDialog(jsonArrayData.getJSONObject(0).getString("Msg"));
                        } else {
                            getCurrentVersionnew();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AppUtils.showExceptionDialog(Splash_Activity.this);
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    } catch (Exception e) {
        e.printStackTrace();
        AppUtils.showExceptionDialog(Splash_Activity.this);
    }
}
    public void showUpdateDialog(String Msg) {
        try {
            final Dialog dialog = AppUtils.createDialog(Splash_Activity.this, false);
            dialog.setCancelable(false);

            TextView txt_DialogTitle = (TextView) dialog.findViewById(R.id.txt_DialogTitle);
            txt_DialogTitle.setText(Html.fromHtml(Msg));

            TextView txt_submit = (TextView) dialog.findViewById(R.id.button);
            txt_submit.setText("OK");
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
            txt_cancel.setVisibility(GONE);
            txt_cancel.setTextColor(getResources().getColor(R.color.color__bg_orange));
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
            AppUtils.showExceptionDialog(Splash_Activity.this);
        }
    }
    private void getCurrentVersionnew(){
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo =  pm.getPackageInfo(this.getPackageName(),0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        new GetLatestVersionnew().execute();

    }
    private class GetLatestVersionnew extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
//It retrieves the latest version by scraping the content of current version from play store at runtime
                Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName()).get();
                latestVersion = doc.getElementsByClass("htlgb").get(6).text();

            }catch (Exception e){
                //   Toast.makeText(Splash_Activity.this, "App update related issue .Please try again !" , Toast.LENGTH_SHORT).show();
                //    AppUtils.alertDialogWithFinish(Splash_Activity.this , "App update related issue .Please try again !");
                Log.e("latestversionerr" , e.getMessage());
                e.printStackTrace();

            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(latestVersion!=null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)){
                    if(!isFinishing()){ //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                        showUpdateDialog();
                    }
                }
                else {
                    moveNextScreen();
                }
            }
            else
                //   background.start();
                super.onPostExecute(jsonObject);
        }
    }
    private void showUpdateDialog(){
        final Dialog dialog = new Dialog(Splash_Activity.this, R.style.ThemeDialogCustom);
        //   dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_update);

        TextView dialog4all_txt = dialog.findViewById(R.id.tvDescription);
        Button btnNone = dialog.findViewById(R.id.btnNone);
        ImageView iv_update_image = dialog.findViewById(R.id.iv_update_image);
        dialog4all_txt.setText("An Update is available,Please Update App from Play Store.");
        GifDrawable gifDrawable = null;
        try {
            gifDrawable = new GifDrawable( getAssets(), "update.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gifDrawable != null) {
            gifDrawable.addAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationCompleted(int loopNumber) {
                    Log.d("splashscreen", "Gif animation completed");
                }
            });
            iv_update_image.setImageDrawable(gifDrawable);
        }

        btnNone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();

    }
}