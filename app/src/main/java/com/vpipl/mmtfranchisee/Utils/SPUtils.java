package com.vpipl.mmtfranchisee.Utils;

import android.content.Context;

/**
 * Created by admin on 01-05-2017.
 */
public class SPUtils {

    public static String USER_FCode="sp_user_id";


    public static String USER_NAME = "NAME";
    public static String USER_Email = "Email";
    public static String USER_Mobile = "Mobile";
    public static String USER_Address = "Address";
    public static String USER_Pincode = "Pincode";
    public static String USER_City = "City";
    public static String USER_State = "State";
    public static String USER_SponsorIDNO = "SponsorIDNO";
    public static String USER_SponsorFormno = "SponsorFormno";

    public static String USER_INFO = "sp_userinfo";

    public static String IS_LOGIN = "sp_isLogin";
    public static String IS_INSTALL = "sp_isInstall";
    public static String IS_INSTALL_MobileNo = "sp_isInstall_MobileNo";

    public static String IS_INSTALL_DeviceModel = "sp_isInstall_DeviceModel";
    public static String IS_INSTALL_DeviceName = "sp_isInstall_DeviceName";
    public static String IS_FIRSTRUN = "sp_isFirstRUN";

    static Context context;
    private static SPUtils instance = new SPUtils();

    public SPUtils getInstance(Context con) {
        context = con;
        return instance;
    }
}