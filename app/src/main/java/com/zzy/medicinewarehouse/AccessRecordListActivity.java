package com.zzy.medicinewarehouse;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zzy.medicinewarehouse.base.BaseApplication;
import com.zzy.medicinewarehouse.bean.AccessRecord;
import com.zzy.medicinewarehouse.databinding.AccessRecordListBinding;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class AccessRecordListActivity extends AppCompatActivity {

    private AccessRecordListBinding binding;

    private AccessRecordListAdapter adapter;

    private List<AccessRecord> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AccessRecordListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("存取记录");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvContent.setLayoutManager(linearLayoutManager);
        dataList = new ArrayList<>();
        getData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getData() {
        try {
            DbManager db = x.getDb(BaseApplication.daoConfig);

            List<AccessRecord> accessRecords = db.selector(AccessRecord.class).orderBy("createDate", true).findAll();
            if (accessRecords != null) {
                dataList.clear();
                dataList.addAll(accessRecords);
                adapter = new AccessRecordListAdapter(dataList, this);
                binding.rvContent.setAdapter(adapter);
            }


        } catch (DbException e) {
            throw new RuntimeException(e);
        }
    }
}
