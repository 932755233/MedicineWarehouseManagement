package com.zzy.medicinewarehouse.base;

import android.app.Application;

import org.xutils.BuildConfig;
import org.xutils.x;

import java.security.cert.X509Certificate;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
