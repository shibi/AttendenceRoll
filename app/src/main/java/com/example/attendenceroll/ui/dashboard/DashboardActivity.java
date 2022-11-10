package com.example.attendenceroll.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.attendenceroll.Constants;
import com.example.attendenceroll.R;
import com.example.attendenceroll.ui.active_users.ActiveUserActivity;
import com.example.attendenceroll.ui.login.LoginActivity;
import com.example.attendenceroll.ui.scaner.ScannerActivity;
import com.example.attendenceroll.utils.AppDialogs;
import com.example.attendenceroll.utils.SharedPrefHelper;

public class DashboardActivity extends AppCompatActivity {

    private LinearLayout ll_scan;
    private AppCompatButton btn_logout,btn_report;
    private AppDialogs appDialogs;
    private String branchId;
    private ImageView iv_branch_profile;
    private String branchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashborad);

        //initialize app dialog box
        appDialogs = new AppDialogs(this);

        branchId = SharedPrefHelper.getInstance(this).getBranchId();
        branchName = SharedPrefHelper.getInstance(this).getBranchName();
        if(branchId.isEmpty()){
            showForceLogoutForBranchIdMissing();
            return;
        }

        ll_scan = findViewById(R.id.ll_scan);
        btn_logout = findViewById(R.id.btn_logout);
        btn_report = findViewById(R.id.btn_report);
        iv_branch_profile = findViewById(R.id.iv_branch_profile);



        ll_scan.setOnClickListener(view -> gotoScannerActivity());

        btn_report.setOnClickListener(view -> gotoActiveUsersActivity());

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //show logout confirmation dialog
                showLogoutConfirmation();

            }
        });

        iv_branch_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showPopupWindow(iv_branch_profile);
                appDialogs.showBranchProfile(branchName,branchId);
            }
        });

    }

    /**
     * show logout confirmation dialog box to user
     *
     * */
    private void showLogoutConfirmation(){
        try {

            appDialogs.showLogoutConfirmationDialog(new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    //logout
                    logoutCall();
                }

                @Override
                public void onClickNegetive(String id) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * to redirect to scanner screen
     * */
    private void gotoScannerActivity(){

        Intent scannerIntent = new Intent(this, ScannerActivity.class);
        scannerIntent.putExtra(Constants.BRANCH_ID, branchId);
        startActivity(scannerIntent);
    }

    private void showForceLogoutForBranchIdMissing(){
        try {

            appDialogs.showLogoutWithMessage(getString(R.string.force_logout_branchid_missing), new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    try {

                        logoutCall();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onClickNegetive(String id) {
                    try {

                        finish();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * clear login credential
     * and goto login screen
     * */
    private void logoutCall(){
        try {

            SharedPrefHelper spHelper = SharedPrefHelper.getInstance(this);

            //clear login and branch details
            spHelper.logoutClear();

            //goto login activity
            gotoLoginActivity();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * to redirect to scanner screen
     * */
    private void gotoLoginActivity(){
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    /**
     *  to goto active users list
     * */
    private void gotoActiveUsersActivity(){
        Intent activeUsers = new Intent(this, ActiveUserActivity.class);
        startActivity(activeUsers);
    }


    //PopupWindow display method
    /*public void showPopupWindow(final View view) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_branch_profile, null);

        //Specify the length and width through constants
        int width = 360;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.TOP|Gravity.END, 0, 0);

        //Initialize the elements of our window, install the handler
        AppCompatTextView tv_branchName = popupView.findViewById(R.id.tv_branchName);
        tv_branchName.setText(branchName);

        AppCompatTextView tv_branchCode = popupView.findViewById(R.id.tv_branch_code);
        tv_branchCode.setText("branch code : "+branchId);

        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }*/

}