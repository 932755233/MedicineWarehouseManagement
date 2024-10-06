package com.zzy.medicinewarehouse.base;

import android.app.Application;

import org.xutils.BuildConfig;
import org.xutils.DbManager;
import org.xutils.x;

import java.security.cert.X509Certificate;

public class BaseApplication extends Application {

    public static DbManager.DaoConfig daoConfig;


    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);


        daoConfig = new DbManager.DaoConfig()
                .setDbName("medicinewarehouse.db")
                // 不设置dbDir时, 默认存储在app的私有目录.
//                .setDbDir(new File("/sdcard")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
                .setDbVersion(1)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                        // or
                        // db.dropDb();
                    }
                });
    }
}
