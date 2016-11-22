package com.infinite.ble;

import java.util.UUID;

/**
 * Created by lsq on 11/22/2016.
 */

public interface Constants {
    UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");

    UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");

    UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
}
