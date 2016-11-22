package com.infinite.ble.callback;


import com.infinite.ble.exception.BleException;

public class BleTimeoutException extends BleException {
    public BleTimeoutException() {
        super(ERROR_CODE_TIMEOUT, "Timeout Exception Occurred! ");
    }
}
