package com.zzy.medicinewarehouse;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zzy.medicinewarehouse.base.BaseApplication;
import com.zzy.medicinewarehouse.base.Constants;
import com.zzy.medicinewarehouse.databinding.LayoutSettingBinding;
import com.zzy.medicinewarehouse.utils.UnitUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

public class SettingActivity extends AppCompatActivity {

    private LayoutSettingBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LayoutSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("设置");

        ArrayAdapter unitAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Constants.unitDatas);
        binding.spnAlarmUnit.setAdapter(unitAdapter);

        binding.butSetAllAlarm.setOnClickListener(v -> {

            if (TextUtils.isEmpty(binding.etAlarmInventory.getText().toString().trim())) {
                Toast.makeText(SettingActivity.this, "请输入数量！", Toast.LENGTH_LONG).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                                .setIcon(R.drawable.ic_launcher_background)
                    .setTitle("提示")
                    .setMessage("是否要同时设置提醒数量？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String sql = "update medicine set alarmInventory = " + UnitUtil.getNumberOfUnit(binding.etAlarmInventory.getText().toString().trim(), binding.spnAlarmUnit.getSelectedItemPosition());
                                DbManager db = x.getDb(BaseApplication.daoConfig);
                                db.execNonQuery(sql);
                                Toast.makeText(SettingActivity.this, "设置完成", Toast.LENGTH_LONG).show();
                            } catch (DbException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        binding.butClearAllData.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                                .setIcon(R.drawable.ic_launcher_background)
                    .setTitle("警告")
                    .setMessage("是否要清理数据？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                DbManager db = x.getDb(BaseApplication.daoConfig);
                                db.dropDb();
                                Toast.makeText(SettingActivity.this, "清理完成", Toast.LENGTH_LONG).show();
                            } catch (DbException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}