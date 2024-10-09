package com.zzy.medicinewarehouse.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zzy.medicinewarehouse.R;
import com.zzy.medicinewarehouse.RecyclerViewAdapter;
import com.zzy.medicinewarehouse.bean.Medicine;
import com.zzy.medicinewarehouse.databinding.ItemMedicinePwBinding;
import com.zzy.medicinewarehouse.utils.UnitUtil;

import java.util.List;

public class MedicineListPopupWindowAdapter extends RecyclerView.Adapter<MedicineListPopupWindowAdapter.ViewHolder> {

    private Context context;
    private List<Medicine> beanList;

    public MedicineListPopupWindowAdapter(Context context, List<Medicine> beanList) {
        this.context = context;
        this.beanList = beanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine_pw, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine bean = beanList.get(position);
        if (bean != null) {
            holder.tv_name.setText(bean.getName());
            holder.tv_abbreviation.setText(bean.getAbbreviation());
            holder.tv_inventory.setText(UnitUtil.getUnitStr(bean.getInventory(), 0) + "公斤");
            holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return beanList.size();
    }

    public void notifyDataSetChanged(List<Medicine> beanList) {
        this.beanList = beanList;
        notifyDataSetChanged();
    }

    private OnItemClickListener listener;

    //添加接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    //创建构造方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_abbreviation;
        private TextView tv_inventory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_abbreviation = itemView.findViewById(R.id.tv_abbreviation);
            tv_inventory = itemView.findViewById(R.id.tv_inventory);
        }
    }
}
