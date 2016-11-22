package com.infinite.ble.util;

/**
 * Created by lsq on 11/22/2016.
 */

public class BleCommand {

    private static final int data_size = 20;
    private static  byte[] user_id = DataUtils.intToByteArray(01020304);

    /**
     * 首次连接腕表时，下发振动命令
     *
     * @return
     */
    public static byte[] firstConnectVibrate(int userId) {
        byte[] data = new byte[data_size];

        int index = 0;
        //命令类型
        data[index++] = AllCmd.CMD_0X0C;

        //用户id
        setUserId(userId);
        System.arraycopy(user_id, 0, data, index, user_id.length);
        index += user_id.length;
        //数据类型
        data[index] = (byte) 2;
        return data;
    }

    public static void setUserId(int userId){
        user_id = DataUtils.intToByteArray(userId) ;
    }
}
