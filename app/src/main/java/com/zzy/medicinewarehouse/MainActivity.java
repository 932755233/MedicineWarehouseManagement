package com.zzy.medicinewarehouse;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zzy.medicinewarehouse.base.BaseApplication;
import com.zzy.medicinewarehouse.bean.Medicine;
import com.zzy.medicinewarehouse.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private List<Medicine> dataList = null;
    private RecyclerViewAdapter adapter;

    private int sortType = 0;
    private boolean isDesc = false;

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setTitle("首页");

        dataList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvContent.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerViewAdapter(MainActivity.this, dataList);

        binding.rvContent.setAdapter(adapter);

        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAnchorView(R.id.fab)
//                        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, AddMedicineActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.normal_sort) {
            if (sortType != 0) {
                isDesc = false;
            } else {
                isDesc = !isDesc;
            }
            sortType = 0;
            getData();
            return true;
        }
        if (id == R.id.date_sort) {
            if (sortType != 1) {
                isDesc = false;
            } else {
                isDesc = !isDesc;
            }
            sortType = 1;
            getData();
            return true;
        }
        if (id == R.id.name_sort) {
            if (sortType != 2) {
                isDesc = false;
            } else {
                isDesc = !isDesc;
            }
            sortType = 2;
            getData();
            return true;
        }
        if (id == R.id.save_get_list) {
            startActivity(new Intent(MainActivity.this, AccessRecordListActivity.class));
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getData() {
        try {
            DbManager db = x.getDb(BaseApplication.daoConfig);

            Selector<Medicine> selector = db.selector(Medicine.class);
            if (sortType == 0)
                selector.orderBy("id", isDesc);
            if (sortType == 1)
                selector.orderBy("inventory", isDesc);
            if (sortType == 2)
                selector.orderBy("abbreviation", isDesc);

            List<Medicine> medicineList = selector.findAll();

            if (medicineList != null) {
                dataList.clear();
                dataList.addAll(medicineList);
                adapter.notifyDataSetChanged();
            }

        } catch (DbException e) {
            throw new RuntimeException(e);
        }
        binding.srlContent.setRefreshing(false);

    }
}