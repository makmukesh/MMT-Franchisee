package com.vpipl.mmtfranchisee;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.vpipl.mmtfranchisee.Utils.SPUtils;
import com.vpipl.mmtfranchisee.modal.RequestList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 01-05-2017.
 */
public class AppController extends Application {

    public static ArrayList<HashMap<String, String>> stateList = new ArrayList<>();

    public static List<RequestList> CurrentPendingRequestList = new ArrayList<>();

    public static Uri URI;

    private static AppController mInstance;
    private static SharedPreferences sp_userinfo;
    private static SharedPreferences sp_isInstall;
    private static SharedPreferences sp_isLogin;

    /**
     * used to get instance globally of SharedPreferences
     */
    public static synchronized SharedPreferences getSpUserInfo() {
        return sp_userinfo;
    }

    public static synchronized SharedPreferences getSpIsLogin() {
        return sp_isLogin;
    }

    public static synchronized SharedPreferences getSpIsInstall() {
        return sp_isInstall;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mInstance = this;
            /* to call initialize SharedPreferences  */
            initSharedPreferences();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * used to initialize instance globally of SharedPreferences
     */
    private void initSharedPreferences() {
        try {
            sp_userinfo = getApplicationContext().getSharedPreferences(SPUtils.USER_INFO, Context.MODE_PRIVATE);
            sp_isInstall = getApplicationContext().getSharedPreferences(SPUtils.IS_INSTALL, Context.MODE_PRIVATE);
            sp_isLogin = getApplicationContext().getSharedPreferences(SPUtils.IS_LOGIN, Context.MODE_PRIVATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}