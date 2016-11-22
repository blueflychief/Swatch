package com.infinite;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.infinite.ble.BleManager;
import com.infinite.ble.Constants;
import com.infinite.ble.callback.BleCharacterCallback;
import com.infinite.ble.callback.BleGattCallback;
import com.infinite.ble.callback.ListScanCallback;
import com.infinite.ble.exception.BleException;
import com.infinite.ble.util.BleCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ble.swatch.tools.KLog;
import ble.swatch.tools.ToastUtils;

import static com.infinite.ble.Constants.RX_SERVICE_UUID;
import static com.infinite.ble.Constants.TX_CHAR_UUID;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar pb_progress;
    private ListView lv_devices;
    private List<BluetoothDevice> mDeviceList;
    private BluetoothDevice mBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ble);
        findViewById(R.id.bt_search).setOnClickListener(this);
        BleManager.getInstance().init(this);
        pb_progress = (ProgressBar) findViewById(R.id.pb_progress);
        lv_devices = (ListView) findViewById(R.id.lv_devices);
        mDeviceList = new ArrayList<>();
        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pb_progress.setVisibility(View.VISIBLE);
                mBluetoothDevice = mDeviceList.get(position);
                BleManager.getInstance().connect(mBluetoothDevice, false, mConnectCallback);
            }
        });
    }

    private ListScanCallback mScanCallback = new ListScanCallback(10000) {
        @Override
        public void onDeviceFound(final BluetoothDevice[] devices) {
            final List<Map<String, String>> list = new ArrayList<>();
            if (devices.length > 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, String> map;
                        for (BluetoothDevice device : devices) {
                            map = new HashMap<>();
                            map.put("device", device.getAddress() + "   name:" + device.getName());
                            map.put("mac", device.getAddress());
                            list.add(map);
                        }
                        mDeviceList = Arrays.asList(devices);
                        lv_devices.setAdapter(new SimpleAdapter(SearchActivity.this, list, R.layout.item_device, new String[]{"device"}, new int[]{R.id.tv_device}));
                    }
                });
            } else {
                ToastUtils.showToast("没有扫描到设备");
            }
        }

        @Override
        public void onScanTimeout() {
            super.onScanTimeout();
            pb_progress.setVisibility(View.GONE);
        }
    };

    private BleGattCallback mConnectCallback = new BleGattCallback() {
        @Override
        public void onConnectOk(BluetoothGatt gatt, int status) {

        }

        @Override
        public void onDiscoverOk(BluetoothGattCharacteristic characteristic, int status) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pb_progress.setVisibility(View.GONE);
                    KLog.i("----发现后开始操作");
                    BleManager.getInstance().writeData(BleCommand.firstConnectVibrate(100251), RX_SERVICE_UUID, Constants.RX_CHAR_UUID, mOperateCallback);
                }
            });
        }

        @Override
        public void onDiscoverFailed(String msg, int status) {
            ToastUtils.showToast(msg);
        }


        @Override
        public void onConnectFailed(BleException exception) {

        }
    };


    private BleCharacterCallback mOperateCallback = new BleCharacterCallback() {
        @Override
        public void onSuccess(BluetoothGattCharacteristic characteristic) {
            KLog.i("-------onSuccess");
        }

        @Override
        public void onFailure(BleException exception) {
            KLog.i("-------onFailure:" + exception.getDescription());
        }
    };


    private void onSwatchDataChange(BluetoothGattCharacteristic characteristic) {
        KLog.i("------onSwatchDataChange:" + characteristic.getUuid());
        if (TX_CHAR_UUID.equals(characteristic.getUuid())) {
            byte[] data = characteristic.getValue();
            byte cmd = data[0];
            KLog.i("-----cmd:" + cmd);
            switch (cmd) {

            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_search:
                scanDevice();
                break;
        }
    }

    /**
     * 搜索周围蓝牙设备
     */
    private void scanDevice() {
        if (BleManager.getInstance().isInScanning()) {
            return;
        }
        pb_progress.setVisibility(View.VISIBLE);
        BleManager.getInstance().scanAllDevice(mScanCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().closeBluetoothGatt();
    }

}
