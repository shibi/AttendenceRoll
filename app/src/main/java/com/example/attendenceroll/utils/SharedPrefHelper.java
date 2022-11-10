package com.example.attendenceroll.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.attendenceroll.Config;


public class SharedPrefHelper {

    private static SharedPrefHelper instance;
    private static final String SP_LOGIN_USERNAME = "username";
    private static final String SP_LOGIN_PASSWORD = "password";
    private static Context mContext;
    private static final String SP_USER_ID = "userID";

    private static final String SP_BRANCH_ID = "branchId";
    private static final String SP_BRANCH_NAME = "branchName";

    private SharedPrefHelper(Context context) {
        this.mContext = context;
    }

    public static SharedPrefHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefHelper(context);
        }
        return instance;
    }

    public void saveLogin(String username, String password) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SP_LOGIN_USERNAME, username);
            editor.putString(SP_LOGIN_PASSWORD, password);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSavedLogin() {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            String username = sharedPreferences.getString(SP_LOGIN_USERNAME, "");
            String password = sharedPreferences.getString(SP_LOGIN_PASSWORD, "");

            return username + ":" + password;

        } catch (Exception e) {
            e.printStackTrace();
            return ":";
        }
    }

    public void saveBranchId(String branchId) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SP_BRANCH_ID, branchId);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBranchId() {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            String branchId = sharedPreferences.getString(SP_BRANCH_ID, "");

            return branchId;

        } catch (Exception e) {
            e.printStackTrace();
            return ":";
        }
    }

    public void saveBranchName(String branchName) {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SP_BRANCH_NAME, branchName);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String getBranchName() {
        try {

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            String branchId = sharedPreferences.getString(SP_BRANCH_NAME, "");

            return branchId;

        } catch (Exception e) {
            e.printStackTrace();
            return ":";
        }
    }

    public void saveBranchCredentials(String email,String password,String branchId,String branchName){
        try{

            saveLogin(email,password);
            saveBranchId(branchId);
            saveBranchName(branchName);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void logoutClear(){
        try
        {
            saveLogin("","");
            saveBranchId("");
            saveBranchName("");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
