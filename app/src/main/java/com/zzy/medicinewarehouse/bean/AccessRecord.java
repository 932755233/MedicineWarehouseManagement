package com.zzy.medicinewarehouse.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "accessrecord")
public class AccessRecord {

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "medicineId")
    private int medicineId;
    @Column(name = "medicineName")
    private String medicineName;
    @Column(name = "before")
    private int before;
    @Column(name = "add")
    private int add;
    @Column(name = "after")
    private int after;
    @Column(name = "type")
    private int type;//1：存入。2：取出
    @Column(name = "createDate")
    private String createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getBefore() {
        return before;
    }

    public void setBefore(int before) {
        this.before = before;
    }

    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }

    public int getAfter() {
        return after;
    }

    public void setAfter(int after) {
        this.after = after;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
