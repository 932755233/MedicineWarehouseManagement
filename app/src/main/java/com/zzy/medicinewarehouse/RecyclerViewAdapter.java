package com.zzy.medicinewarehouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zzy.medicinewarehouse.bean.Medicine;
import com.zzy.medicinewarehouse.databinding.ItemMedicineBinding;
import com.zzy.medicinewarehouse.utils.UnitUtil;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Medicine> dataList;
    private Context context;


    public RecyclerViewAdapter(Context context, List<Medicine> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine bean = dataList.get(position);
        if (bean != null) {
            holder.tv_name.setText(bean.getName());

            if (bean.getInventory() > bean.getAlarmInventory()) {
                holder.tv_inventory.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            } else {
                holder.tv_inventory.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
            }

            holder.tv_inventory.setText(UnitUtil.getUnitStr(bean.getInventory(), 0) + "公斤");

            holder.tv_abbreviation.setText(bean.getAbbreviation());
            holder.tv_alarmInventory.setText(UnitUtil.getUnitStr(bean.getAlarmInventory(), 0) + "公斤");
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_inventory;
        TextView tv_abbreviation;
        TextView tv_alarmInventory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_inventory = itemView.findViewById(R.id.tv_inventory);
            tv_abbreviation = itemView.findViewById(R.id.tv_abbreviation);
            tv_alarmInventory = itemView.findViewById(R.id.tv_alarmInventory);
        }
    }
}
