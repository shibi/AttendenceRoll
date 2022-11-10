package com.example.attendenceroll.ui.checkindetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.attendenceroll.Constants;
import com.example.attendenceroll.R;
import com.example.attendenceroll.ui.dashboard.DashboardActivity;
import com.example.attendenceroll.ui.login.LoginActivity;

public class EmployeeDetailActivity extends AppCompatActivity {

    private AppCompatTextView tv_name, tv_date,tv_time,tv_status;
    private ImageView iv_profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_detail);

        tv_name = findViewById(R.id.tv_employeeName);
        tv_status = findViewById(R.id.tv_employeeStatus);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        iv_profile_image = findViewById(R.id.iv_profileImage);


        String employeeName;
        String employeeStatus;
        String date;
        String time;
        String imageUrl;

        if(getIntent()!=null){

            Intent intent = getIntent();

            employeeName = intent.getStringExtra(Constants.EMPLOYEE_NAME);
            employeeStatus = intent.getStringExtra(Constants.CHECK_STATUS);
            date = intent.getStringExtra(Constants.DATE);
            time = intent.getStringExtra(Constants.TIME);
            imageUrl = intent.getStringExtra(Constants.EMPLOYEE_IMAGE);

            tv_name.setText(employeeName);
            tv_status.setText(employeeStatus);
            tv_date.setText(date);
            tv_time.setText(time);

            if(!imageUrl.isEmpty()){

                Glide.with(iv_profile_image.getContext())
                        .load(imageUrl)
                        .fitCenter()
                        .placeholder(R.drawable.ic_employee2)
                        .into(iv_profile_image);
            }

        }

        AppCompatButton btn_dashboard = findViewById(R.id.btn_redirect);
        btn_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoDashboard();
            }
        });
    }

    private void gotoDashboard(){
        onBackPressed();
    }

}