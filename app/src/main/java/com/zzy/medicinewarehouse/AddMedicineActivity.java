package com.zzy.medicinewarehouse;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.zzy.medicinewarehouse.base.BaseApplication;
import com.zzy.medicinewarehouse.bean.AccessRecord;
import com.zzy.medicinewarehouse.bean.Medicine;
import com.zzy.medicinewarehouse.databinding.AddMedicineBinding;
import com.zzy.medicinewarehouse.utils.DateTimeUtil;
import com.zzy.medicinewarehouse.utils.UnitUtil;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class AddMedicineActivity extends AppCompatActivity {

    private String[] unitDatas = {"公斤", "斤", "克"};

    private AddMedicineBinding binding;

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
                                        binding.spnUnit.getSelectedItem().toString();
                                        inventoryTemp = bean.getInventory();

                                        String inventorStr = getUnitStr(inventoryTemp);

                                        binding.tvThisInventory.setText("当前余量：" + inventorStr);

                                        binding.tvInventoryTxt.setText("增加余量：");
                                        binding.etInventory.setText("");

                                        binding.etAlarmInventory.setText(UnitUtil.getUnitStr(bean.getAlarmInventory(), binding.spnAlarmUnit.getSelectedItemPosition()));

                                        idTemp = bean.getId();
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

        ArrayAdapter unitAdapter = new ArrayAdapter(AddMedicineActivity.this, android.R.layout.simple_list_item_1, unitDatas);
        binding.spnUnit.setAdapter(unitAdapter);

        binding.spnUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String unitStr = getUnitStr(inventoryTemp);
                binding.tvThisInventory.setText("当前余量：" + unitStr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spnAlarmUnit.setAdapter(unitAdapter);

        binding.oneGongJin.setOnClickListener(v -> {
            setInventoryAndAlarm("1");
        });
        binding.twoGongJin.setOnClickListener(v -> {
            setInventoryAndAlarm("2");
        });
        binding.fiveGongJin.setOnClickListener(v -> {
            setInventoryAndAlarm("5");
        });
        binding.tenGongJin.setOnClickListener(v -> {
            setInventoryAndAlarm("10");
        });

        binding.butAdd.setOnClickListener(v -> {
            try {
                String tempTip = "新增成功";
                Medicine medicine = new Medicine();

                if (idTemp != -1) {
                    medicine.setId(idTemp);
                    tempTip = "保存成功";
                } else {
                    medicine.setCreateDate(DateTimeUtil.getyyyyMMddHHmmss());
                }
                medicine.setName(binding.etName.getText().toString().trim());
                medicine.setAbbreviation(binding.etAbbreviation.getText().toString().trim());

                medicine.setInventory(inventoryTemp + UnitUtil.getNumberOfUnit(
                        Long.parseLong(
                                TextUtils.isEmpty(binding.etInventory.getText().toString().trim()) ? "0" : binding.etInventory.getText().toString().trim()
                        ), binding.spnUnit.getSelectedItemPosition()
                ));

                medicine.setAlarmInventory(UnitUtil.getNumberOfUnit(
                        Long.parseLong(
                                TextUtils.isEmpty(binding.etAlarmInventory.getText().toString().trim()) ? "0" : binding.etAlarmInventory.getText().toString().trim()
                        ), binding.spnAlarmUnit.getSelectedItemPosition()
                ));


                DbManager db = x.getDb(BaseApplication.daoConfig);
                if (idTemp != -1) {
                    db.update(medicine);
                } else {
                    db.saveBindingId(medicine);
                }

                AccessRecord accessRecord = new AccessRecord();
                accessRecord.setCreateDate(DateTimeUtil.getyyyyMMddHHmmss());
                accessRecord.setBefore(inventoryTemp);
                accessRecord.setVariable(UnitUtil.getNumberOfUnit(
                        Long.parseLong(
                                TextUtils.isEmpty(binding.etInventory.getText().toString().trim()) ? "0" : binding.etInventory.getText().toString().trim()
                        ), binding.spnUnit.getSelectedItemPosition()));
                accessRecord.setAfter(inventoryTemp + UnitUtil.getNumberOfUnit(
                        Long.parseLong(
                                TextUtils.isEmpty(binding.etInventory.getText().toString().trim()) ? "0" : binding.etInventory.getText().toString().trim()
                        ), binding.spnUnit.getSelectedItemPosition()
                ));
                accessRecord.setMedicineName(medicine.getName());
                accessRecord.setMedicineId(medicine.getId());
                accessRecord.setType(1);
                db.save(accessRecord);


                boolean checked = binding.cbIsContinuous.isChecked();
                if (checked) {
                    binding.etName.setText("");
                    binding.etAbbreviation.setText("");
//                    binding.etInventory.setText("");
//                    binding.etAlarmInventory.setText("");
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
        });
    }

    public void setInventoryAndAlarm(String inventory) {
        if (binding.etInventory.hasFocus()) {
            binding.etInventory.setText(inventory);
            binding.etInventory.setSelection(inventory.length());
        } else if (binding.etAlarmInventory.hasFocus()) {
            binding.etAlarmInventory.setText(inventory);
            binding.etAlarmInventory.setSelection(inventory.length());
        }
    }

    public String getUnitStr(int inventory) {
        DecimalFormat df = new DecimalFormat("0.###");
        String inventorStr = "";
        BigDecimal bigDecimal = new BigDecimal(inventory);
        if (binding.spnUnit.getSelectedItemPosition() == 0) {
            BigDecimal tempBigDecimal = new BigDecimal("1000");
            BigDecimal result = bigDecimal.divide(tempBigDecimal, 3, BigDecimal.ROUND_HALF_UP);
            inventorStr = df.format(result) + "公斤";
        } else if (binding.spnUnit.getSelectedItemPosition() == 1) {
            BigDecimal tempBigDecimal = new BigDecimal("500");
            BigDecimal result = bigDecimal.divide(tempBigDecimal, 3, BigDecimal.ROUND_HALF_UP);
            inventorStr = df.format(result) + "斤";
        } else if (binding.spnUnit.getSelectedItemPosition() == 2) {
            inventorStr = df.format(inventory) + "克";
        }
        return inventorStr;
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
