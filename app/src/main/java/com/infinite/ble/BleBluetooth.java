package com.infinite.ble;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;

import com.infinite.ble.callback.BleGattCallback;
import com.infinite.ble.callback.PeriodScanCallback;
import com.infinite.ble.conn.BleConnector;
import com.infinite.ble.util.BleUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

import ble.swatch.tools.KLog;

import static com.infinite.ble.Constants.CCCD;
import static com.infinite.ble.Constants.RX_CHAR_UUID;
import static com.infinite.ble.Constants.RX_SERVICE_UUID;
import static com.infinite.ble.Constants.TX_CHAR_UUID;

/**
 * Ble操作对象
 * Created by lsq on 11/22/2016.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleBluetooth {

    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BleGattCallback mBleGattCallback;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_SCANNING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_SERVICES_DISCOVERED = 4;

    private int connectionState = STATE_DISCONNECTED;

    public BleBluetooth(Context context) {
        this.mContext = context = context.getApplicationContext();
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    /**
     * 检查蓝牙是否关闭，如果关闭则开启
     */
    public void enableBluetoothIfDisabled(Activity activity, int requestCode) {
        if (!isBlueEnable()) {
            BleUtils.enableBluetooth(activity, requestCode);
        }
    }

    /**
     * 蓝牙是否打开
     */
    public boolean isBlueEnable() {
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 是否正在扫描
     *
     * @return
     */
    public boolean isInScanning() {
        return connectionState == STATE_SCANNING;
    }


    public void enableBluetooth() {
        mBluetoothAdapter.enable();
    }

    public void disableBluetooth() {
        mBluetoothAdapter.disable();
    }

    /**
     * 开始扫描
     *
     * @param callback
     * @return
     */
    public boolean startLeScan(PeriodScanCallback callback) {
        callback.setBleBluetooth(this).notifyScanStarted();
        boolean suc = mBluetoothAdapter.startLeScan(callback);
        if (suc) {
            connectionState = STATE_SCANNING;
        } else {
            callback.removeHandlerMsg();
        }
        return suc;
    }

    /**
     * 停止扫描
     *
     * @param callback
     */
    public void stopScan(BluetoothAdapter.LeScanCallback callback) {
        if (callback instanceof PeriodScanCallback) {
            ((PeriodScanCallback) callback).removeHandlerMsg();
        }
        mBluetoothAdapter.stopLeScan(callback);
        if (connectionState == STATE_SCANNING) {
            connectionState = STATE_DISCONNECTED;
        }
    }


    public synchronized BluetoothGatt connect(final BluetoothDevice device,
                                              final boolean autoConnect,
                                              final BleGattCallback callback) {
        KLog.i("connect name：" + device.getName()
                + " mac:" + device.getAddress()
                + " autoConnect：" + autoConnect);
//        addConnectGattCallback(callback);
        mBleGattCallback = callback;
        return device.connectGatt(mContext, autoConnect, mGattCallback);
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (mBleGattCallback != null) {
                mBluetoothGatt = gatt;
                switch (newState) {
                    case BluetoothProfile.STATE_DISCONNECTED:
                        KLog.i("----断开成功！");
                        connectionState = STATE_DISCONNECTED;
                        break;
                    case BluetoothProfile.STATE_CONNECTING:
                        KLog.i("----正在连接！");
                        connectionState = STATE_CONNECTING;
                        break;
                    case BluetoothProfile.STATE_CONNECTED:
                        KLog.i("----连接成功！");
                        connectionState = STATE_CONNECTED;
                        mBleGattCallback.onConnectOk(gatt, newState);
                        mBluetoothGatt.discoverServices(); // 发现服务
                        break;
                    case BluetoothProfile.STATE_DISCONNECTING:
                        KLog.i("----正在断开连接！");
                        break;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            BleUtils.printService(gatt);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                KLog.i("----发现成功！_status:" + status);
                connectionState = STATE_SERVICES_DISCOVERED;
                BluetoothGattService service = gatt.getService(RX_SERVICE_UUID);
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(TX_CHAR_UUID);
                    if (characteristic != null) {
                        gatt.setCharacteristicNotification(characteristic, true);
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CCCD);
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
                        BluetoothGattCharacteristic mRxCharacteristic = service.getCharacteristic(RX_CHAR_UUID);
                        mRxCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        if (mBleGattCallback != null) {
                            mBleGattCallback.onDiscoverOk(characteristic, status);
                        }
                    } else {
                        if (mBleGattCallback != null) {
                            mBleGattCallback.onDiscoverFailed("不支持的BluetoothGattService", status);
                        }
                    }
                } else {
                    if (mBleGattCallback != null) {
                        mBleGattCallback.onDiscoverFailed("不支持的BluetoothGattService", status);
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            KLog.i("----onCharacteristicRead！_status:" + status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            KLog.i("----onCharacteristicWrite！_status:" + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            KLog.i("----onCharacteristicChanged！_status:" + Arrays.toString(characteristic.getValue()));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            KLog.i("----onDescriptorRead！_status:" + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            KLog.i("----onDescriptorWrite！_status:" + status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            KLog.i("----onReliableWriteCompleted！_status:" + status);
        }
    };


    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }


    public BleConnector newBleConnector() {
        return new BleConnector(this);
    }

    /**
     * 断开、刷新、关闭 bluetooth gatt.
     */
    public void closeBluetoothGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }

        if (mBluetoothGatt != null) {
            refreshDeviceCache();
        }

        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
    }

    public boolean refreshDeviceCache() {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                final boolean success = (Boolean) refresh.invoke(mBluetoothGatt);
                KLog.i("Refreshing result: " + success);
                return success;
            }
        } catch (Exception e) {
            KLog.e("An exception occured while refreshing device", e);
        }
        return false;
    }
}
