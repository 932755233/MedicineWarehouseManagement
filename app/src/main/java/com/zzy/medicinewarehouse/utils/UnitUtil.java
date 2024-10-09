package com.zzy.medicinewarehouse.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UnitUtil {

    public static String getUnitStr(int inventory, int type) {
        DecimalFormat df = new DecimalFormat("0.###");
        String inventorStr = "";
        BigDecimal bigDecimal = new BigDecimal(inventory);
        if (type == 0) {
            BigDecimal tempBigDecimal = new BigDecimal("1000");
            BigDecimal result = bigDecimal.divide(tempBigDecimal, 3, RoundingMode.HALF_UP);
            inventorStr = df.format(result);
        } else if (type == 1) {
            BigDecimal tempBigDecimal = new BigDecimal("500");
            BigDecimal result = bigDecimal.divide(tempBigDecimal, 3, RoundingMode.HALF_UP);
            inventorStr = df.format(result);
        } else if (type == 2) {
            inventorStr = df.format(inventory);
        }
        return inventorStr;
    }

    public static int getNumberOfUnit(String number, int type) {
        number = TextUtils.isEmpty(number) ? "0" : number;
        BigDecimal bigDecimal = new BigDecimal(number);
        int resultInt = 0;
        if (type == 0) {
            BigDecimal tempBigDecimal = new BigDecimal("1000");
            BigDecimal result = bigDecimal.multiply(tempBigDecimal);
            resultInt = result.intValue();
        } else if (type == 1) {
            BigDecimal tempBigDecimal = new BigDecimal("500");
            BigDecimal result = bigDecimal.multiply(tempBigDecimal);
            resultInt = result.intValue();
        }
        return resultInt;
    }
}
