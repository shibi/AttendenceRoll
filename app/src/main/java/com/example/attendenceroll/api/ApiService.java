package com.example.attendenceroll.api;

import com.example.attendenceroll.models.CheckInResponse;
import com.example.attendenceroll.models.EmployeeListDTO;
import com.example.attendenceroll.models.ForceLogoutResponse;
import com.example.attendenceroll.models.LoginResponseDTO;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {


    @FormUrlEncoded
    @POST("login")
    Call<LoginResponseDTO> branchLogin(@Field("email") String email,@Field("password") String password);

    @FormUrlEncoded
    @POST("employee/check")
    Call<CheckInResponse> checkInEmployee(@Field("employee_id") String employeeId,@Field("branch_id") String branchId,@Field("date") String date,@Field("time") String time);


    @FormUrlEncoded
    @POST("employee/list")
    Call<EmployeeListDTO> getEmployeeList(@Field("branch_id") String branchId);

}
