package com.example.attendenceroll.ui.active_users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.attendenceroll.Config;
import com.example.attendenceroll.Constants;
import com.example.attendenceroll.R;
import com.example.attendenceroll.adapter.ActiveEmployeAdapter;
import com.example.attendenceroll.api.ApiGenerator;
import com.example.attendenceroll.api.ApiService;
import com.example.attendenceroll.models.EmployeeListDTO;
import com.example.attendenceroll.models.EmployeeListData;
import com.example.attendenceroll.models.LoggedEmployee;
import com.example.attendenceroll.models.LoggedEmployees;
import com.example.attendenceroll.ui.checkindetails.EmployeeDetailActivity;
import com.example.attendenceroll.utils.AppDialogs;
import com.example.attendenceroll.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ActiveUserActivity extends AppCompatActivity {

    private List<EmployeeListData> loggedEmployeesList;
    private RecyclerView rv_logged;
    private ActiveEmployeAdapter activeEmployeAdapter;
    private AppDialogs appDialogs;
    private FrameLayout fl_empty_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_user);

        appDialogs = new AppDialogs(this);

        rv_logged = findViewById(R.id.rv_loggedEmployees);
        fl_empty_view = findViewById(R.id.fl_empty);

        setUpList();


        String branchId = SharedPrefHelper.getInstance(this).getBranchId();
        requestLoggedDetailsInThisBranch(branchId);

        showEmptyView(true);
    }

    private void setUpList(){
        activeEmployeAdapter = new ActiveEmployeAdapter(ActiveUserActivity.this, new ActiveEmployeAdapter.OnEmployeeListClick() {
            @Override
            public void onClickEmployee(EmployeeListData employee) {
                gotoEmployeeDetailsActivity(employee);
            }
        });

        rv_logged.setLayoutManager(new LinearLayoutManager(this));
        rv_logged.setAdapter(activeEmployeAdapter);
    }

    /**
     * get logged employee details from net
     * */
    private void requestLoggedDetailsInThisBranch(String branchId){
        try{

            appDialogs.showProgressBar();

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Config.ENCRYPTED_API_KEY);
            Call<EmployeeListDTO> call = apiService.getEmployeeList(branchId);
            ApiGenerator.enqueueApiCall(call, new ApiGenerator.APIResponseListener<EmployeeListDTO>() {
                @Override
                public void onSuccess(Response<EmployeeListDTO> response) {
                    try{
                        appDialogs.hideProgressbar();
                        EmployeeListDTO employeeListDTO = response.body();
                        if(employeeListDTO!=null){
                            if(employeeListDTO.getSuccess()){
                                List<EmployeeListData> employeeListData = employeeListDTO.getData();
                                if(employeeListData!=null && employeeListData.size()>0){

                                    activeEmployeAdapter.replace(employeeListData);

                                    //remove empty view
                                    showEmptyView(false);
                                }else {
                                    showEmptyView(true);
                                }
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(ApiGenerator.API_ERROR api_error, String error) {
                    try{
                        appDialogs.hideProgressbar();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gotoEmployeeDetailsActivity(EmployeeListData employee){

        Intent detailsIntent = new Intent(this, EmployeeDetailActivity.class);
        String status = (employee.getEmployeeStatus().equals(Constants.STATUS_ACTIVE))?getString(R.string.checkedIn):getString(R.string.checked_out);
        detailsIntent.putExtra(Constants.EMPLOYEE_NAME,employee.getEmployeeName());
        detailsIntent.putExtra(Constants.CHECK_STATUS,status);
        detailsIntent.putExtra(Constants.EMPLOYEE_IMAGE,employee.getEmployeeImage());
        detailsIntent.putExtra(Constants.DATE,employee.getEmployeeDate());
        detailsIntent.putExtra(Constants.TIME,employee.getEmployeeTime());

        startActivity(detailsIntent);
    }

    private void showEmptyView(boolean show){
        fl_empty_view.setVisibility((show)? View.VISIBLE:View.GONE);
    }

    private void populateDummyData(){

        loggedEmployeesList = new ArrayList<>();

        EmployeeListData employees;
        for (int i=0;i< 10;i++){
            employees = new EmployeeListData();
            employees.setEmployeeName("Employee "+(i+1));
            loggedEmployeesList.add(employees);
        }

        activeEmployeAdapter.replace(loggedEmployeesList);

    }
}