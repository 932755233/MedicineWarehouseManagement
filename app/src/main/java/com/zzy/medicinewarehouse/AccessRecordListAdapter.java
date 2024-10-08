package com.zzy.medicinewarehouse;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zzy.medicinewarehouse.bean.AccessRecord;
import com.zzy.medicinewarehouse.utils.UnitUtil;

import java.util.List;
import java.util.zip.Inflater;

public class AccessRecordListAdapter extends RecyclerView.Adapter<AccessRecordListAdapter.ViewHolder> {

    private List<AccessRecord> dataList;
    private Context context;

    public AccessRecordListAdapter(List<AccessRecord> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_access_record, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccessRecord bean = dataList.get(position);
        if (bean != null) {
            holder.tv_name.setText(bean.getMedicineName());
            holder.tv_type.setText(bean.getType() == 1 ? "存入" : "取出");
            GradientDrawable gradientDrawable = (GradientDrawable) holder.tv_type.getBackground();
            if (bean.getType() == 1) {
                holder.tv_type.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                gradientDrawable.setStroke(5, ContextCompat.getColor(context, android.R.color.holo_green_dark));
            } else {
                holder.tv_type.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
                gradientDrawable.setStroke(5, ContextCompat.getColor(context, android.R.color.holo_red_dark));
            }
            holder.tv_before.setText(UnitUtil.getUnitStr(bean.getBefore(), 0));
            holder.tv_variable.setText(UnitUtil.getUnitStr(bean.getVariable(), 0));
            holder.tv_after.setText(UnitUtil.getUnitStr(bean.getAfter(), 0));
            holder.tv_date.setText("时间：" + bean.getCreateDate());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_type;
        TextView tv_before;
        TextView tv_variable;
        TextView tv_after;
        TextView tv_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_before = itemView.findViewById(R.id.tv_before);
            tv_variable = itemView.findViewById(R.id.tv_variable);
            tv_after = itemView.findViewById(R.id.tv_after);
            tv_date = itemView.findViewById(R.id.tv_date);
        }
    }


}
