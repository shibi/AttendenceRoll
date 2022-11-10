package com.example.attendenceroll.ui.scaner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.example.attendenceroll.Config;
import com.example.attendenceroll.Constants;
import com.example.attendenceroll.R;
import com.example.attendenceroll.api.ApiGenerator;
import com.example.attendenceroll.api.ApiService;
import com.example.attendenceroll.models.CheckInResponse;
import com.example.attendenceroll.models.LoggedEmployee;
import com.example.attendenceroll.ui.checkindetails.EmployeeDetailActivity;
import com.example.attendenceroll.ui.dashboard.DashboardActivity;
import com.example.attendenceroll.ui.login.LoginActivity;
import com.example.attendenceroll.utils.AppDialogs;
import com.example.attendenceroll.utils.DateTimeUtils;
import com.example.attendenceroll.utils.SharedPrefHelper;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ScannerActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private boolean isScannerInitialized;
    private AppDialogs appDialogs;
    private String branchId = "";
    private BottomSheetBehavior bottomSheetBehavior;
    private ConstraintLayout ctl_layout_bottomsheet;
    private AppCompatEditText et_employeeId;
    private AppCompatButton btn_checkin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        appDialogs = new AppDialogs(this);
        ctl_layout_bottomsheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(ctl_layout_bottomsheet);
        et_employeeId = findViewById(R.id.et_manual_employeeid);
        btn_checkin = findViewById(R.id.btn_manual_checkin);



        //get branch id passed through intent from previous screen.
        if(getIntent()!=null) {
            branchId = getIntent().getStringExtra(Constants.BRANCH_ID);
        }

        //force logout, if branch id is empty
        //its no use to continue without branch id
        if(branchId.isEmpty()){
            showForceLogoutForBranchIdMissing();
            return;
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            //initialize qr scanner
            initQRScanner();

        }else {

            isScannerInitialized = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA_PERMISSION);
            return;
        }


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                try{

                    switch (newState) {
                        case BottomSheetBehavior.STATE_EXPANDED:
                            et_employeeId.setText(Config.EMPLOYEE_SCAN_PREFIX);
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            break;
                        default:
                            break;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        //Manual entry employee id
        btn_checkin.setOnClickListener(view -> {
            onClickManualCheckIn();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isScannerInitialized) {
            mCodeScanner.startPreview();
        }
    }


    @Override
    protected void onPause() {

        if (isScannerInitialized) {
            mCodeScanner.releaseResources();
        }

        super.onPause();
    }

    /**
     * initialize QR scanner
     * */
    private void initQRScanner(){
        try {
            //set flag true to identify scanner initialized
            isScannerInitialized = true;
            //get initialize scanner view
            CodeScannerView scannerView = findViewById(R.id.scanner_view);
            mCodeScanner = new CodeScanner(this, scannerView);
            List<BarcodeFormat> scanFormats = new ArrayList<>();
            scanFormats.add(BarcodeFormat.QR_CODE);
            mCodeScanner.setFormats(scanFormats);
            mCodeScanner.setAutoFocusEnabled(true);
            mCodeScanner.setAutoFocusMode(AutoFocusMode.CONTINUOUS);
            mCodeScanner.setTouchFocusEnabled(true);


            //add decode callback
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //get scanned data
                                String scannedEmployeId = result.getText();

                                //show child as toast
                                showToast(result.getText());

                                //check whether scanned data is valid
                                if(!scannedEmployeId.isEmpty()) {

                                    if(verifyPrefix(scannedEmployeId)) {

                                        //request check in
                                        requestCheckInWithEmployee(scannedEmployeId, branchId);
                                        //requestCheckInWithEmployee(scannedEmployeId, "2");

                                    }else {

                                        showToast(getString(R.string.wrong_format_employee));

                                        //goto dashboard
                                        //gotoDashboard();
                                        onBackPressed();
                                    }
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To check in an employee
     * @param employeeId
     * @param branchId
     * */
    private void requestCheckInWithEmployee(String employeeId, String branchId){
        try {

            //show progress
            appDialogs.showProgressBar();

            //get the current date and time for checkin
            String today_date = DateTimeUtils.getCurrentDate();
            String time_now = DateTimeUtils.getCurrentTime();

            String[] emp_ids = employeeId.split(Config.EMPLOYEE_SCAN_PREFIX);
            String formatedEmployeeId = emp_ids[1];

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Config.ENCRYPTED_API_KEY);
            Call<CheckInResponse> call = apiService.checkInEmployee(formatedEmployeeId, branchId, today_date, time_now);
            ApiGenerator.enqueueApiCall(call, new ApiGenerator.APIResponseListener<CheckInResponse>(){

                @Override
                public void onSuccess(Response<CheckInResponse> response) {
                    try {

                        appDialogs.hideProgressbar();

                        CheckInResponse checkInResponse = response.body();
                        if(checkInResponse.getSuccess()) {

                            LoggedEmployee loggedEmployee = checkInResponse.getData();
                            String employeName = loggedEmployee.getEmployeeName();
                            String date = loggedEmployee.getEmployeeDate();
                            String time = loggedEmployee.getEmployeeTime();
                            String employeStatus = getEmployeeStatus(loggedEmployee.getEmployeeStatus());
                            String imageURL = loggedEmployee.getEmployeeImage();

                            gotoCheckInDetailActivity(employeName, employeStatus,imageURL, date, time);

                        }else {

                            LoggedEmployee loggedEmployee = checkInResponse.getData();

                            if(loggedEmployee!=null){
                                if(loggedEmployee.getBranchId()!=null) {
                                    if (loggedEmployee.getBranchId() > 0) {
                                        showForceLogoutConfirmation(employeeId, "" + loggedEmployee.getBranchId());
                                    }
                                }else {
                                    showToast(checkInResponse.getMessage());
                                }
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(ApiGenerator.API_ERROR api_error, String error) {
                    try {

                        appDialogs.hideProgressbar();
                        showToast("Error while accessing employee details");

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
     * to get employee status
     * @param status
     * */
    private String getEmployeeStatus(String status){
        return (status.equals(Constants.STATUS_ACTIVE))?getString(R.string.checkedIn):getString(R.string.checked_out);
    }


    /**
     * if tried to check-in from different
     * branch , it shows a logout confirmation
     * */
    private void showForceLogoutConfirmation(final String emp_id,final String branchId){
        try {

            //get the message
            String message = getString(R.string.checkin_different_branch);

            //show force checkout confirmation dialog
            appDialogs.showForceCheckOutDialog(message, new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    //send force logout request to webservice
                    requestForceCheckOut(emp_id,branchId);
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
     * on manual entry employee id
     * */
    private void onClickManualCheckIn(){
        try {
            //get employee id from field
            String str_employeId = et_employeeId.getText().toString();

            //check for id empty
            if(!str_employeId.isEmpty()){
                //check if prefix is correct
                if(verifyPrefix(str_employeId)){

                    //if ok, then proceed to check in
                    requestCheckInWithEmployee(str_employeId, branchId);
                }else {

                    //if prefix not same , then show invalid format message
                    showToast(getString(R.string.wrong_format_employee));

                    //goto dashboard
                    gotoDashboard();


                }
            }else {
                //show empty field error
                et_employeeId.setError(getString(R.string.enter_valid_employeeid));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To verify employee id prefix
     * */
    private boolean verifyPrefix(String employeeIdWithPrefix){
        try {
                //only employee id with length more
                // than 4 is considered valid
                if (employeeIdWithPrefix.length() > 4) {

                    //get the first 3 letters
                    String prefix = employeeIdWithPrefix.substring(0,3);

                    //check the prefix same as the one provided
                    //returns true if equals or false if not equal
                    return prefix.equals(Config.EMPLOYEE_SCAN_PREFIX);

                }else {
                    return false;
                }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    /**
     * to send force logout request to database
     * */
    private void requestForceCheckOut(String emp_id, String br_id){
        try{

            //CALL CHECK IN WITH THE GIVEN EMPLOYEE ID
            //AND BRANCH ID TO CHECKOUT.
            requestCheckInWithEmployee(emp_id,br_id);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * if branch Id missing, then logout
     * */
    private void showForceLogoutForBranchIdMissing(){
        try {

            appDialogs.showLogoutWithMessage(getString(R.string.force_logout_branchid_missing), new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    try {

                      logout();

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

    private void gotoCheckInDetailActivity(String _employeeName,String _checkStatus,String imageURL,String date,String time){

        Intent checkInIntent = new Intent(this, EmployeeDetailActivity.class);

        checkInIntent.putExtra(Constants.EMPLOYEE_NAME,_employeeName);
        checkInIntent.putExtra(Constants.CHECK_STATUS,_checkStatus);
        checkInIntent.putExtra(Constants.EMPLOYEE_IMAGE,imageURL);
        checkInIntent.putExtra(Constants.DATE,date);
        checkInIntent.putExtra(Constants.TIME,time);

        startActivity(checkInIntent);
        finish();

    }


    private void gotoDashboard(){

        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        startActivity(dashboardIntent);
        finish();
    }

    private void showToast(String msg){
        Toast.makeText(ScannerActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    private void logout(){
        try{

            SharedPrefHelper.getInstance(ScannerActivity.this).logoutClear();

            Intent loginInIntent = new Intent(this, LoginActivity.class);
            loginInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginInIntent);
            finish();

        }catch (Exception e) {

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //check request code
        if(requestCode == Constants.REQUEST_CAMERA_PERMISSION){
            //check permission granted
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //initialize scanner
                initQRScanner();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //release scanner object for garbage collection
        if(isScannerInitialized){
            if(mCodeScanner!=null){
                mCodeScanner = null;
            }
        }
    }
}
