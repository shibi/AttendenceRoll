package com.example.attendenceroll.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.example.attendenceroll.Constants;
import com.example.attendenceroll.R;
import com.example.attendenceroll.models.EmployeeListData;
import com.example.attendenceroll.models.LoggedEmployee;
import com.example.attendenceroll.models.LoggedEmployees;
import com.example.attendenceroll.ui.checkindetails.EmployeeDetailActivity;
import com.example.attendenceroll.utils.listutils.DataBoundListAdapter;
import com.example.attendenceroll.utils.listutils.DataBoundViewHolder;

import java.util.List;

public class ActiveEmployeAdapter extends DataBoundListAdapter<EmployeeListData, ActiveEmployeAdapter.DataViewHolder> {

    private OnEmployeeListClick listener;
    private Context mContext;
    public ActiveEmployeAdapter(Context context,OnEmployeeListClick listener){
        this.listener = listener;
        this.mContext = context;
    }

    @Override
    public int createLayoutView() {
        return R.layout.view_logged_employee;
    }

    @Override
    public DataViewHolder wrapViewHolder(View view) {
        return new DataViewHolder(view);
    }

    @Override
    public void onBind(@NonNull DataViewHolder holder, int position, EmployeeListData employee) {
        try{

            holder.tv_emp_name.setText(employee.getEmployeeName());

            Resources res = mContext.getResources();
            String status;
            int color;

            if(employee.getEmployeeStatus().equals(Constants.STATUS_ACTIVE)){
                status = res.getString(R.string.checkedIn);
                color = R.color.eucalyptus_green;
            }else {
                status = res.getString(R.string.checked_out);
                color = R.color.red_color;
            }


            holder.tv_emp_status.setText(status);
            holder.tv_emp_status.setTextColor(res.getColor(color));

            holder.tv_time.setText(employee.getEmployeeTime());
            holder.tv_date.setText(employee.getEmployeeDate());

            //list item click listener
            holder.itemView.setOnClickListener(view -> {
                try{

                    if(listener!=null)listener.onClickEmployee(employee);

                }catch (Exception e){
                    e.printStackTrace();
                }
            });


            Glide.with(holder.iv_profile_image.getContext())
                    .load(employee.getEmployeeImage())
                    .fitCenter()
                    .placeholder(R.drawable.ic_employee2)
                    .into(holder.iv_profile_image);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected boolean areItemsTheSame(EmployeeListData oldItem, EmployeeListData newItem) {
        return false;
    }

    @Override
    protected boolean areContentsTheSame(EmployeeListData oldItem, EmployeeListData newItem) {
        return false;
    }

    @Override
    protected void dispatched() {

    }

    public static class DataViewHolder extends DataBoundViewHolder{
        private AppCompatTextView tv_emp_name, tv_emp_status,tv_date,tv_time;
        private ImageView iv_profile_image;
        public DataViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_emp_name = itemView.findViewById(R.id.tv_name);
            tv_emp_status = itemView.findViewById(R.id.tv_status);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time= itemView.findViewById(R.id.tv_time);
            iv_profile_image = itemView.findViewById(R.id.iv_profile_list);
        }
    }

    public interface OnEmployeeListClick{
        void onClickEmployee(EmployeeListData employee);
    }
}
