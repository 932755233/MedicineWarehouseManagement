package com.zzy.medicinewarehouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
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
import com.zzy.medicinewarehouse.base.Constants;
import com.zzy.medicinewarehouse.bean.AccessRecord;
import com.zzy.medicinewarehouse.bean.Medicine;
import com.zzy.medicinewarehouse.databinding.OutInBinding;
import com.zzy.medicinewarehouse.utils.DateTimeUtil;
import com.zzy.medicinewarehouse.utils.UnitUtil;
import com.zzy.medicinewarehouse.view.MedicineListPopupWindow;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class OutInActivity extends AppCompatActivity {

    private OutInBinding binding;

    private int id;

    private Medicine bean;

    private int inventoryTemp = 0;

    private TextWatcher NameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getDateOfName(s.toString());
        }
    };

    private TextWatcher AbbreviationTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getDateOfAbbreviation(s.toString());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OutInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("存取");

        binding.tvAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddMedicineActivity.class));
        });

        binding.tvDelete.setOnClickListener(v -> {

            if (bean == null || bean.getId() == 0) {
                Toast.makeText(OutInActivity.this, "请检查是否已选择药品!", Toast.LENGTH_LONG).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                                .setIcon(R.drawable.ic_launcher_background)
                    .setTitle("警告")
                    .setMessage("是否要删除：" + bean.getName() + "？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                DbManager db = x.getDb(BaseApplication.daoConfig);
                                db.deleteById(Medicine.class, bean.getId());
                                finish();
                                Toast.makeText(OutInActivity.this, "删除完成!", Toast.LENGTH_LONG).show();
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

        ArrayAdapter unitAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Constants.unitDatas);
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

        binding.butSave.setOnClickListener(v -> {
            if (bean.getId() == 0) {
                Toast.makeText(this, "请选择药品！！！", Toast.LENGTH_LONG).show();
                return;
            }

            String name = binding.etName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "药品名称不能为空！！！", Toast.LENGTH_LONG).show();
                return;
            }
            bean.setName(name);

            String abbreviation = binding.etAbbreviation.getText().toString().trim();
            if (TextUtils.isEmpty(abbreviation)) {
                Toast.makeText(this, "药品简写不能为空！！！", Toast.LENGTH_LONG).show();
                return;
            }
            bean.setAbbreviation(abbreviation);

            int resultTemp = 0;
            int typeTemp = 2;

            int inven = UnitUtil.getNumberOfUnit(binding.etInventory.getText().toString().trim(), binding.spnUnit.getSelectedItemPosition());

            if (binding.rbOut.isChecked()) {
                if (inven > inventoryTemp) {
                    Toast.makeText(this, "取出数量不能大于当前余量！！！", Toast.LENGTH_LONG).show();
                    return;
                }
                resultTemp = inventoryTemp - inven;
                typeTemp = 2;
            } else {
                resultTemp = inventoryTemp + inven;
                typeTemp = 1;
            }

            bean.setInventory(resultTemp);
            bean.setAlarmInventory(UnitUtil.getNumberOfUnit(binding.etAlarmInventory.getText().toString().trim(), binding.spnAlarmUnit.getSelectedItemPosition()));

            try {
                DbManager db = x.getDb(BaseApplication.daoConfig);
                db.update(bean);

                AccessRecord accessRecord = new AccessRecord();
                accessRecord.setCreateDate(DateTimeUtil.getyyyyMMddHHmmss());
                accessRecord.setBefore(inventoryTemp);
                accessRecord.setVariable(inven);
                accessRecord.setAfter(resultTemp);
                accessRecord.setMedicineName(bean.getName());
                accessRecord.setMedicineId(bean.getId());
                accessRecord.setType(typeTemp);

                db.save(accessRecord);

                if (binding.cbIsContinuous.isChecked()) {
                    bean = new Medicine();
//                    binding.etName.setText("");
//                    binding.etAbbreviation.setText("");
//                    binding.tvThisInventory.setText("当前余量：0公斤");
//                    inventoryTemp = 0;
                    setCompData();
                    binding.etAbbreviation.requestFocus();
                } else {
                    finish();
                }

            } catch (DbException e) {
                throw new RuntimeException(e);
            }
        });

        Intent intent = getIntent();

        id = intent.getIntExtra("ID", -1);

        getData();

        binding.rbOut.setChecked(true);
        binding.etInventory.requestFocus();


    }

    public void setCompData() {
        binding.etName.removeTextChangedListener(NameTextWatcher);
        binding.etAbbreviation.removeTextChangedListener(AbbreviationTextWatcher);

        binding.etName.setText(bean.getName());
        binding.etAbbreviation.setText(bean.getAbbreviation());
        inventoryTemp = bean.getInventory();
        binding.tvThisInventory.setText("当前数量:" + UnitUtil.getUnitStr(inventoryTemp, 0));
        binding.etAlarmInventory.setText(UnitUtil.getUnitStr(bean.getAlarmInventory(), 0));

        binding.etInventory.requestFocus();

        binding.etName.addTextChangedListener(NameTextWatcher);
        binding.etAbbreviation.addTextChangedListener(AbbreviationTextWatcher);
    }

    public void getDateOfAbbreviation(String abbreviation) {
        try {
            DbManager db = x.getDb(BaseApplication.daoConfig);

            List<Medicine> medicineList = db.selector(Medicine.class).where("abbreviation", "like", abbreviation + "%").findAll();

            MedicineListPopupWindow popupWindow = new MedicineListPopupWindow(this, medicineList, bean1 -> {
                this.bean = bean1;
                setCompData();
            });

            popupWindow.showPop(binding.etAbbreviation);
        } catch (DbException e) {
            throw new RuntimeException(e);
        }
    }

    public void getDateOfName(String name) {
        try {
            DbManager db = x.getDb(BaseApplication.daoConfig);

            List<Medicine> medicineList = db.selector(Medicine.class).where("name", "like", name + "%").findAll();

            MedicineListPopupWindow popupWindow = new MedicineListPopupWindow(this, medicineList, bean1 -> {
                this.bean = bean1;
                setCompData();
            });

            popupWindow.showPop(binding.etName);


//            View view = LayoutInflater.from(this).inflate(R.layout.pw_medicine_list, null);
//            RecyclerView recyclerView = view.findViewById(R.id.rv_content);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
//            MedicineListPopupWindowAdapter adapter = new MedicineListPopupWindowAdapter(this, medicineList);
////        adapter.setOnItemClickListener(position -> );
//            recyclerView.setAdapter(adapter);
//
//            PopupWindow popupWindow1 = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            popupWindow1.setOutsideTouchable(true);
//            popupWindow1.showAsDropDown(binding.etName);

        } catch (DbException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData() {
        try {
            DbManager db = x.getDb(BaseApplication.daoConfig);

            bean = db.selector(Medicine.class).where("id", "=", id).findFirst();

            setCompData();

        } catch (DbException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

}
