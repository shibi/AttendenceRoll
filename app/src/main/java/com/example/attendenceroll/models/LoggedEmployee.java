
package com.example.attendenceroll.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class LoggedEmployee {

    @SerializedName("employee_name")
    @Expose
    private String employeeName;
    @SerializedName("employee_status")
    @Expose
    private String employeeStatus;

    @SerializedName("employee_time")
    @Expose
    private String employeeTime;

    @SerializedName("employee_date")
    @Expose
    private String employeeDate;
    @SerializedName("employee_image")
    @Expose
    private String employeeImage;

    @SerializedName("error")
    @Expose
    private String error;

    @SerializedName("branch_id")
    @Expose
    private Integer branchId;


    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(String employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public String getEmployeeTime() {
        return employeeTime;
    }

    public void setEmployeeTime(String employeeTime) {
        this.employeeTime = employeeTime;
    }

    public String getEmployeeDate() {
        return employeeDate;
    }

    public void setEmployeeDate(String employeeDate) {
        this.employeeDate = employeeDate;
    }

    public String getEmployeeImage() {
        return employeeImage;
    }

    public void setEmployeeImage(String employeeImage) {
        this.employeeImage = employeeImage;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }
}
