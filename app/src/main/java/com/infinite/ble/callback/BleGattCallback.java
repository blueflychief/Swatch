
package com.infinite.ble.callback;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import com.infinite.ble.exception.BleException;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BleGattCallback extends BluetoothGattCallback {

    public abstract void onConnectOk(BluetoothGatt gatt, int status);

    public abstract void onDiscoverOk(BluetoothGattCharacteristic characteristic, int status);

    public abstract void onDiscoverFailed(String msg, int status);

    public abstract void onConnectFailed(BleException exception);

}