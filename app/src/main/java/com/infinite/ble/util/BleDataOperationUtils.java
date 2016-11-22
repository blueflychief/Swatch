package com.infinite.ble.util;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import com.infinite.ble.Constants;

import ble.swatch.tools.KLog;


/**
 * Ble数据的操作工具类
 * Created by lsq on 11/22/2016.
 */

public class BleDataOperationUtils {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean writeRXCharacteristic(BluetoothGatt gatt, byte[] value) {
        if (gatt == null) {
            KLog.i("-----gatt is null!!!");
            return false;
        }
        BluetoothGattService service = gatt.getService(Constants.RX_SERVICE_UUID);
        if (service == null) {
            KLog.i("-----service is null!!!");
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(Constants.RX_CHAR_UUID);
        if (characteristic == null) {
            KLog.i("-----characteristic is null!!!");
            return false;
        }
        characteristic.setValue(value);
        boolean status = gatt.writeCharacteristic(characteristic);
        KLog.d("writeRXCharacteristic——status=" + status);
        return status;
    }
}
