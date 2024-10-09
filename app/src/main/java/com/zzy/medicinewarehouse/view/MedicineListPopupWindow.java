package com.zzy.medicinewarehouse.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzy.medicinewarehouse.R;
import com.zzy.medicinewarehouse.bean.Medicine;

import java.util.List;
import java.util.zip.Inflater;

public class MedicineListPopupWindow extends PopupWindow {

    private Context mContext;
    private View outerView;
    private OnItemClickListener listener;
    private MedicineListPopupWindowAdapter adapter;

    public interface OnItemClickListener {
        void onItemClick(Medicine bean);
    }

    public MedicineListPopupWindow(Context context, List<Medicine> data, OnItemClickListener listener) {
        this.mContext = context;
        this.listener = listener;

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        outerView = LayoutInflater.from(context).inflate(R.layout.pw_medicine_list, null);

        initViews(data);
        setContentView(outerView);
    }

    public void initViews(List<Medicine> data) {
        RecyclerView recyclerView = outerView.findViewById(R.id.rv_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new MedicineListPopupWindowAdapter(this.mContext, data);
        adapter.setOnItemClickListener(position -> {
            listener.onItemClick(data.get(position));
            this.dismiss();
        });
        recyclerView.setAdapter(adapter);
    }

    public void showPop(View view) {
//        changeWindowAlpha(0.7f, mContext);
        showAsDropDown(view, 0, 0, Gravity.BOTTOM);
    }

    public void notifyDataSetChanged(List<Medicine> data) {
        adapter.notifyDataSetChanged(data);
    }

    /**
     * 改变当前window的透明度
     *
     * @param alpha
     * @param context
     */
    private void changeWindowAlpha(float alpha, Context context) {
        WindowManager.LayoutParams params = ((Activity) context).getWindow().getAttributes();
        params.alpha = alpha;
        ((Activity) context).getWindow().setAttributes(params);
    }
}
