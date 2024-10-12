package com.zzy.medicinewarehouse;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.zzy.medicinewarehouse.base.BaseApplication;
import com.zzy.medicinewarehouse.base.Constants;
import com.zzy.medicinewarehouse.bean.AccessRecord;
import com.zzy.medicinewarehouse.bean.Medicine;
import com.zzy.medicinewarehouse.databinding.LayoutSettingBinding;
import com.zzy.medicinewarehouse.utils.UnitUtil;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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

        binding.butExportAllData.setOnClickListener(v -> {
            try {
                DbManager db = x.getDb(BaseApplication.daoConfig);
                List<Medicine> medicineList = db.selector(Medicine.class).findAll();
                List<AccessRecord> accessRecordList = db.selector(AccessRecord.class).findAll();


                Workbook workbook = new HSSFWorkbook();

                Sheet medicine = workbook.createSheet("药品");
                Row row = medicine.createRow(0);
                row.createCell(0).setCellValue("序号");
                row.createCell(1).setCellValue("药品名称");
                row.createCell(2).setCellValue("简写");
                row.createCell(3).setCellValue("库存(公斤)");
                row.createCell(4).setCellValue("提醒库存(公斤)");
                row.createCell(5).setCellValue("创建时间");

                for (int i = 0; i < medicineList.size(); i++) {
                    Medicine bean = medicineList.get(i);
                    Row rowTemp = medicine.createRow(i + 1);
                    rowTemp.createCell(0).setCellValue(bean.getId());
                    rowTemp.createCell(1).setCellValue(bean.getName());
                    rowTemp.createCell(2).setCellValue(bean.getAbbreviation());
                    rowTemp.createCell(3).setCellValue(UnitUtil.getUnitStr(bean.getInventory(), 0));
                    rowTemp.createCell(4).setCellValue(UnitUtil.getUnitStr(bean.getAlarmInventory(), 0));
                    rowTemp.createCell(5).setCellValue(bean.getCreateDate());
                }

                Sheet accessRecord = workbook.createSheet("存取记录");
                Row accessRecordRow = accessRecord.createRow(0);
                accessRecordRow.createCell(0).setCellValue("序号");
                accessRecordRow.createCell(1).setCellValue("药品序号");
                accessRecordRow.createCell(2).setCellValue("药品名称");
                accessRecordRow.createCell(3).setCellValue("存入或取出");
                accessRecordRow.createCell(4).setCellValue("之前余量(公斤)");
                accessRecordRow.createCell(5).setCellValue("变动数量(公斤)");
                accessRecordRow.createCell(6).setCellValue("最终库存(公斤)");
                accessRecordRow.createCell(7).setCellValue("操作时间");

                for (int i = 0; i < medicineList.size(); i++) {
                    AccessRecord bean = accessRecordList.get(i);
                    Row rowTemp = accessRecord.createRow(i + 1);
                    rowTemp.createCell(0).setCellValue(bean.getId());
                    rowTemp.createCell(1).setCellValue(bean.getMedicineId());
                    rowTemp.createCell(2).setCellValue(bean.getMedicineName());
                    rowTemp.createCell(3).setCellValue(bean.getType() == 1 ? "存入" : "取出");
                    rowTemp.createCell(4).setCellValue(UnitUtil.getUnitStr(bean.getBefore(), 0));
                    rowTemp.createCell(5).setCellValue(UnitUtil.getUnitStr(bean.getVariable(), 0));
                    rowTemp.createCell(6).setCellValue(UnitUtil.getUnitStr(bean.getAfter(), 0));
                    rowTemp.createCell(7).setCellValue(bean.getCreateDate());
                }


                XXPermissions.with(this)
                        .permission(Permission.WRITE_EXTERNAL_STORAGE)
                        .request(new OnPermissionCallback() {
                            @Override
                            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                if (allGranted) {
                                    try {
                                        File file = new File(getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), "樊氏堂.xlsx");
                                        LogUtil.i("zzy---" + file.getAbsolutePath());
                                        binding.tvPath.setText("Excel文件保存路径：" + file.getAbsolutePath());
                                        FileOutputStream out = new FileOutputStream(file);
                                        workbook.write(out);
                                        out.close();
                                        workbook.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(SettingActivity.this, "必须要权限", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            } catch (DbException e) {
                throw new RuntimeException(e);
            }
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