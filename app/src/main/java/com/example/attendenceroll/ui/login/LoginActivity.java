package com.example.attendenceroll.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.attendenceroll.Config;
import com.example.attendenceroll.R;
import com.example.attendenceroll.api.ApiGenerator;
import com.example.attendenceroll.api.ApiService;
import com.example.attendenceroll.models.LoginDataDTO;
import com.example.attendenceroll.models.LoginResponseDTO;
import com.example.attendenceroll.ui.dashboard.DashboardActivity;
import com.example.attendenceroll.utils.AppDialogs;
import com.example.attendenceroll.utils.SharedPrefHelper;
import com.example.attendenceroll.utils.Utility;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private AppCompatButton btn_login;
    private AppCompatEditText et_username,et_password;
    private CoordinatorLayout cl_rootLayout;
    private AppDialogs appDialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appDialogs = new AppDialogs(this);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        cl_rootLayout = findViewById(R.id.cl_login_root);

        Utility.setHideShowPassword(et_password,R.drawable.ic_lock);

        btn_login.setOnClickListener(view -> {
            try{

                //on click
                onLoginClick();


               /* Handler handler = new Handler(Looper.getMainLooper());

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(isProgress){
                            appDialogs.hideProgressbar();
                        }else {
                            appDialogs.showProgressBar();
                        }
                        isProgress = !isProgress;

                        handler.postDelayed(this::run, 1000);
                    }
                }, 1000);*/

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //showSnack("Branch Inactive");
    }


    private void onLoginClick(){


        if(validate()){

            appDialogs.showProgressBar();

            final String email = et_username.getText().toString();
            final String password = et_password.getText().toString();

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Config.ENCRYPTED_API_KEY);
            Call<LoginResponseDTO> call = apiService.branchLogin(email , password);

            ApiGenerator.enqueueApiCall(call, new ApiGenerator.APIResponseListener<LoginResponseDTO>(){

                @Override
                public void onSuccess(Response<LoginResponseDTO> response) {
                    try {

                        appDialogs.hideProgressbar();

                        LoginResponseDTO loginResponse = response.body();

                        if(loginResponse.getStatus()) {

                            LoginDataDTO loginData = loginResponse.getData();
                            if (loginData != null) {
                                SharedPrefHelper sp_helper = SharedPrefHelper.getInstance(LoginActivity.this);
                                sp_helper.saveBranchCredentials(email, password, loginData.getId().toString(), loginData.getName());
                                gotoDashboard();
                            }

                        }else {

                            showSnack(loginResponse.getMessage());

                        }

                    }catch (Exception e){
                        
                    }
                }

                @Override
                public void onFailure(ApiGenerator.API_ERROR api_error, String error) {
                    try {

                        appDialogs.hideProgressbar();
                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }else {

        }
    }

    private void gotoDashboard(){
        Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(dashboardIntent);
        finish();
    }

    private void showSnack(String msg){
        try{

            Snackbar snackbar = Snackbar.make(cl_rootLayout,msg,Snackbar.LENGTH_SHORT);
            snackbar.show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean validate(){

        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        if(username.isEmpty()){
            showErrorField(et_username,getString(R.string.username_empty));
            return false;
        }

        if(password.isEmpty()){
            showErrorField(et_password,getString(R.string.password_empty));
            return false;
        }

        return true;
    }

    private void showErrorField(AppCompatEditText field,String message){
        field.setError(message);
        field.requestFocus();
    }
}