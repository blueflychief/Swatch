package com.infinite.ble.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Build;

import java.util.List;

import ble.swatch.tools.KLog;

/**
 * Created by lsq on 11/22/2016.
 */

public class BleUtils {
    /**
     * 开启蓝牙
     */
    public static void enableBluetooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void printService(BluetoothGatt gatt) {
        List<BluetoothGattService> services = gatt.getServices();
        for (BluetoothGattService service : services) {  //遍历service
            KLog.i("--------######find_service:" + service.getUuid());
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristics) {//遍历Characteristic
                KLog.i("--------$$$$characteristic:" + characteristic.getUuid());
                for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    KLog.i("--------$$$$descriptor:" + descriptor.getUuid());
                }
                KLog.i("--------$$$$characteristic_getProperties:" + characteristic.getProperties() + "--" + BleUtils.parseProperty(characteristic.getProperties()));
            }
        }
    }

    /**
     * BluetoothGattCharacteristic的property类型
     *
     * @param property
     * @return
     */
    public static String parseProperty(int property) {
        String desc = null;
        switch (property) {
            case BluetoothGattCharacteristic.PROPERTY_BROADCAST:
                desc = "可广播";
                break;
            case BluetoothGattCharacteristic.PROPERTY_READ:
                desc = "可读";
                break;
            case BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE:
                desc = "可写但不回应";
                break;
            case BluetoothGattCharacteristic.PROPERTY_WRITE:
                desc = "可写";
                break;
            case BluetoothGattCharacteristic.PROPERTY_NOTIFY:
                desc = "可通知";
                break;
            case BluetoothGattCharacteristic.PROPERTY_INDICATE:
                desc = "指示";
                break;
            case BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE:
                desc = "可写带签名";
                break;
            case BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS:
                desc = "有扩展的属性";
                break;
        }
        return desc;
    }
}
