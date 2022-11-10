package com.example.attendenceroll.ui.apploading;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.attendenceroll.Config;
import com.example.attendenceroll.R;
import com.example.attendenceroll.api.ApiGenerator;
import com.example.attendenceroll.api.ApiService;
import com.example.attendenceroll.models.LoginDataDTO;
import com.example.attendenceroll.models.LoginResponseDTO;
import com.example.attendenceroll.ui.dashboard.DashboardActivity;
import com.example.attendenceroll.ui.login.LoginActivity;
import com.example.attendenceroll.utils.SharedPrefHelper;

import retrofit2.Call;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        /*Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSavedLogin();
            }
        }, Config.SPLASH_TIME_OUT);*/

        checkSavedLogin();

    }

    private void gotoLoginScreen(){

        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void checkSavedLogin(){
        try {

            SharedPrefHelper sp_helper = SharedPrefHelper.getInstance(this);
            String loginCredentials = sp_helper.getSavedLogin();

            if(!loginCredentials.equals(":")){

                String[] credentials = loginCredentials.split(":");
                String email = credentials[0];
                String password = credentials[1];
                requestBranchLogin(email, password);
            }else {
                gotoLoginScreen();
            }

        }catch (Exception e){
            e.printStackTrace();
            gotoLoginScreen();
        }
    }

    private void requestBranchLogin(String email,String password){
        try {

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Config.ENCRYPTED_API_KEY);
            Call<LoginResponseDTO> call = apiService.branchLogin(email , password);

            ApiGenerator.enqueueApiCall(call, new ApiGenerator.APIResponseListener<LoginResponseDTO>(){

                @Override
                public void onSuccess(Response<LoginResponseDTO> response) {
                    try {

                        LoginResponseDTO loginResponse = response.body();

                        if(loginResponse.getStatus()) {

                            LoginDataDTO loginData = loginResponse.getData();
                            if (loginData != null) {
                                gotoDashboard();
                            }else {
                                gotoLoginScreen();
                            }

                        }else {

                            gotoLoginScreen();
                        }

                    }catch (Exception e){
                        gotoLoginScreen();
                    }
                }

                @Override
                public void onFailure(ApiGenerator.API_ERROR api_error, String error) {
                    try {

                        Toast.makeText(SplashActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        gotoLoginScreen();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gotoDashboard(){
        Intent dashboardIntent = new Intent(SplashActivity.this, DashboardActivity.class);
        startActivity(dashboardIntent);
        finish();
    }
}