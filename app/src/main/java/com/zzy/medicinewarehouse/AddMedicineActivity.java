package com.zzy.medicinewarehouse;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zzy.medicinewarehouse.base.BaseApplication;
import com.zzy.medicinewarehouse.bean.Medicine;
import com.zzy.medicinewarehouse.databinding.AddMedicineBinding;
import com.zzy.medicinewarehouse.utils.DateTimeUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

public class AddMedicineActivity extends AppCompatActivity {

    AddMedicineBinding binding;

    private int idTemp = -1;
    private int inventoryTemp = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AddMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("新增");

        binding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    DbManager db = x.getDb(BaseApplication.daoConfig);

                    Medicine bean = db.selector(Medicine.class).where("name", "=", s.toString()).findFirst();
                    if (bean != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddMedicineActivity.this)
//                                .setIcon(R.drawable.ic_launcher_background)
                                .setTitle("提示")
                                .setMessage("已有相同名称的药品，是否回显数据")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        binding.llThisInventory.setVisibility(View.VISIBLE);
                                        binding.etAbbreviation.setText(bean.getAbbreviation());
                                        binding.tvThisInventory.setText("当前余量：" + bean.getInventory());
                                        binding.tvInventoryTxt.setText("增加余量：");
                                        binding.etInventory.setText("");
                                        binding.etAlarmInventory.setText(String.valueOf(bean.getAlarmInventory()));
                                        idTemp = bean.getId();
                                        inventoryTemp = bean.getInventory();
                                        binding.butAdd.setText("保存");
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        Toast.makeText(AddMedicineActivity.this, "点击了取消", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();


                    } else {
//                        Toast.makeText(AddMedicineActivity.this, "没找到", Toast.LENGTH_LONG).show();
                    }


                } catch (DbException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        binding.butAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String tempTip = "新增成功";
                    Medicine medicine = new Medicine();
                    if (idTemp != -1) {
                        medicine.setId(idTemp);
                        tempTip = "保存成功";
                    }
                    medicine.setName(binding.etName.getText().toString().trim());
                    medicine.setAbbreviation(binding.etAbbreviation.getText().toString().trim());

                    medicine.setInventory(inventoryTemp + Integer.parseInt(binding.etInventory.getText().toString().trim()));

                    medicine.setAlarmInventory(Integer.parseInt(binding.etAlarmInventory.getText().toString().trim()));
                    medicine.setCreateDate(DateTimeUtil.getyyyyMMddHHmmss());

                    DbManager db = x.getDb(BaseApplication.daoConfig);
                    db.saveOrUpdate(medicine);

                    boolean checked = binding.cbIsContinuous.isChecked();
                    if (checked) {
                        binding.etName.setText("");
                        binding.etAbbreviation.setText("");
                        binding.etInventory.setText("");
                        binding.etAlarmInventory.setText("");
                        binding.llThisInventory.setVisibility(View.GONE);
                        binding.etName.requestFocus();
                        binding.tvInventoryTxt.setText("输入余量：");
                        idTemp = -1;
                        inventoryTemp = 0;

                    } else {
                        finish();
                    }
                    Toast.makeText(AddMedicineActivity.this, tempTip, Toast.LENGTH_SHORT).show();

                } catch (DbException e) {
                    throw new RuntimeException(e);
                }
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
