package com.zzy.medicinewarehouse.bean;

import androidx.annotation.NonNull;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.time.LocalDateTime;
import java.util.Date;


@Table(name = "medicine")
public class Medicine {

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "abbreviation")
    private String abbreviation;
    @Column(name = "inventory")
    private int inventory;
    @Column(name = "alarmInventory")
    private int alarmInventory;
    @Column(name = "createDate")
    private String createDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public int getAlarmInventory() {
        return alarmInventory;
    }

    public void setAlarmInventory(int alarmInventory) {
        this.alarmInventory = alarmInventory;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", inventory=" + inventory +
                ", alarmInventory=" + alarmInventory +
                ", createDate=" + createDate +
                '}';
    }
}
