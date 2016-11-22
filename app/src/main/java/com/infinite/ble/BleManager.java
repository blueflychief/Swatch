package com.infinite.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;

import com.infinite.ble.callback.BleCharacterCallback;
import com.infinite.ble.callback.BleGattCallback;
import com.infinite.ble.callback.ListScanCallback;

import java.util.UUID;

/**
 * Created by lsq on 11/22/2016.
 */

public class BleManager {
    private BleBluetooth mBleBluetooth;
    private Context mContext;

    public BleManager() {
    }

    private static final class BleManagerHolder {
        static final BleManager INSTANCE = new BleManager();
    }

    public static BleManager getInstance() {
        return BleManagerHolder.INSTANCE;
    }

    public BleManager init(Context context) {
        getInstance();
        mContext = context.getApplicationContext();
        if (mBleBluetooth == null) {
            mBleBluetooth = new BleBluetooth(context);
        }
        mBleBluetooth.enableBluetoothIfDisabled((Activity) context, 1);
        return this;
    }


    public boolean scanAllDevice(ListScanCallback callback) {
        return mBleBluetooth.startLeScan(callback);
    }

    /**
     * 连接设备
     */
    public void connect(BluetoothDevice device, boolean autoConnect, BleGattCallback callback) {
        mBleBluetooth.connect(device, autoConnect, callback);
    }


    /**
     * 读取数据
     *
     * @param serviceUuid
     * @param charUuid
     * @param characterCallback
     * @return
     */
    public boolean readData(UUID serviceUuid, UUID charUuid, BleCharacterCallback characterCallback) {
        if (mBleBluetooth == null) {
            throw new NullPointerException("mBleBluetooth is null!!!");
        }
        return mBleBluetooth.newBleConnector()
                .withUUIDString(serviceUuid, charUuid, null)
                .readCharacteristic(characterCallback, charUuid);
    }

    /**
     * 写入数据
     *
     * @param data
     * @param serviceUuid
     * @param charUuid
     * @param characterCallback
     * @return
     */
    public boolean writeData(byte[] data, UUID serviceUuid, UUID charUuid, BleCharacterCallback characterCallback) {
        if (mBleBluetooth == null) {
            throw new NullPointerException("mBleBluetooth is null!!!");
        }
        return mBleBluetooth.newBleConnector()
                .withUUIDString(serviceUuid, charUuid, null)
                .writeCharacteristic(data, characterCallback, charUuid);
    }


    public boolean isInScanning() {
        return mBleBluetooth.isInScanning();
    }

    /**
     * 当前设备是否支持BLE
     */
    public boolean isSupportBle() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 开启蓝牙
     */
    public void enableBluetooth() {
        if (mBleBluetooth != null) {
            mBleBluetooth.enableBluetooth();
        }
    }

    /**
     * 关闭蓝牙
     */
    public void disableBluetooth() {
        if (mBleBluetooth != null) {
            mBleBluetooth.disableBluetooth();
        }
    }

    /**
     * 关闭连接
     */
    public void closeBluetoothGatt() {
        if (mBleBluetooth != null) {
            try {
                mBleBluetooth.closeBluetoothGatt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
