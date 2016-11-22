package com.infinite;

import android.app.Application;

import ble.swatch.tools.KLog;
import ble.swatch.tools.ToastUtils;

/**
 * Created by lsq on 11/22/2016.
 */

public class MyApplication extends Application {
    public static MyApplication sMyApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sMyApplication = this;
        KLog.init(true, "SwatchBle###");
        ToastUtils.init(this);
    }

    public static MyApplication getApp() {
        return sMyApplication;
    }
}
