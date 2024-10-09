package com.zzy.medicinewarehouse;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.zzy.medicinewarehouse.view.RulerWidget;

import android.view.Menu;
import android.view.MenuItem;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.db.Selector;
import org.xutils.db.annotation.Column;
import org.xutils.db.converter.ColumnConverter;
import org.xutils.db.sqlite.ColumnDbType;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.ColumnUtils;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private List<Medicine> dataList = null;
    private RecyclerViewAdapter adapter;

    private int sortType = 0;
    private boolean isDesc = false;

    private String searchStr = "";

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

        getSupportActionBar().setTitle("列表");

        dataList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvContent.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerViewAdapter(MainActivity.this, dataList);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(MainActivity.this, OutInActivity.class);
            intent.putExtra("ID", dataList.get(position).getId());
            startActivity(intent);
        });

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

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchStr = s.toString();
                binding.srlContent.setRefreshing(true);
//                binding.srlContent.canChildScrollUp();
                getData();
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
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getData() {
        try {
            DbManager db = x.getDb(BaseApplication.daoConfig);

            Selector<Medicine> selector = db.selector(Medicine.class);
            if (sortType == 0) {
                selector.orderBy("abbreviation");
//                selector.select("inventory", " <= ", "alarmInventory");
                selector.expr("inventory <= alarmInventory");
            }
            if (sortType == 1)
                selector.orderBy("inventory", isDesc);
            if (sortType == 2)
                selector.orderBy("abbreviation");
            if (!TextUtils.isEmpty(searchStr)) {
                selector.where("name", "like", "%" + searchStr + "%").or("abbreviation", "like", "%" + searchStr + "%");
            }

            List<Medicine> medicineList = selector.findAll();
            dataList.clear();
            if (medicineList != null) {
                dataList.addAll(medicineList);
            }
            adapter.notifyDataSetChanged();
            if (sortType == 2) {
                setRightSuoYin();
                binding.rulerWidget.setVisibility(View.VISIBLE);
            } else {
                binding.rulerWidget.setVisibility(View.GONE);
            }

            if (dataList.isEmpty() && sortType == 0) {
                sortType = 2;
                getData();
            }

        } catch (DbException e) {
            throw new RuntimeException(e);
        }
        binding.srlContent.setRefreshing(false);

    }

    public void setRightSuoYin() {
        Map<String, Integer> selector = new HashMap<>();
        List<String> rules = new ArrayList<>();

        String tempStr = "";
        for (int i = 0; i < dataList.size(); i++) {
            Medicine bean = dataList.get(i);
            String firstWord = bean.getAbbreviation().substring(0, 1);
            if (!selector.containsKey(firstWord)) {
                selector.put(firstWord, i);
                rules.add(firstWord);
            }
        }

        binding.rulerWidget.setData(rules.toArray(new String[0]));
        binding.rulerWidget.invalidate();
        binding.rulerWidget.setOnTouchingLetterChangedListener(new RulerWidget.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                Integer i = selector.get(s);
                binding.rvContent.scrollToPosition(i);
            }
        });
    }


}