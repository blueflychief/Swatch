
package com.infinite.ble.callback;

import android.bluetooth.BluetoothGattCallback;

import com.infinite.ble.exception.BleException;


public abstract class BleCallback {
    private BluetoothGattCallback bluetoothGattCallback;

    public BleCallback setBluetoothGattCallback(BluetoothGattCallback bluetoothGattCallback) {
        this.bluetoothGattCallback = bluetoothGattCallback;
        return this;
    }

    public BluetoothGattCallback getBluetoothGattCallback() {
        return bluetoothGattCallback;
    }

    public void onInitiatedSuccess() {
    }

    public abstract void onFailure(BleException exception);
}