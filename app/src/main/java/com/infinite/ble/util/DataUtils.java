package com.infinite.ble.util;

import java.math.BigInteger;

/**
 * Created by guoning on 16-4-13.
 */
public class DataUtils {

    /**
     * byte数组转int
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        int value = 0;
        int len = b.length;
        for (int i = 0; i < len; i++) {
            int shift = (len - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    /**
     * int转byte数组
     * @param i
     * @return
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * short转byte数组
     * @param i
     * @return
     */
    public static byte[] shortToByteArray(short i) {
        byte[] result = new byte[2];
        result[0] = (byte) ((i >> 8) & 0xFF);
        result[1] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * long转byte数组
     * @param value
     * @return
     */
    public static byte[] longToByteArray(long value) {

        byte[] result = new byte[8];

        result[0] = (byte) ((value >> 56) & 0xFF);
        result[1] = (byte) ((value >> 48) & 0xFF);
        result[2] = (byte) ((value >> 40) & 0xFF);
        result[3] = (byte) ((value >> 32) & 0xFF);
        result[4] = (byte) ((value >> 24) & 0xFF);
        result[5] = (byte) ((value >> 16) & 0xFF);
        result[6] = (byte) ((value >> 8) & 0xFF);
        result[7] = (byte) (value & 0xFF);

        return result;
    }

    /**
     * 16进制转2进制
     * @param hex
     * @return
     */
    public static byte hexTobyte(String hex) {
        //0xA5
        return (byte) Short.parseShort(hex, 16);
    }

    /**
     * 字节转16进制字符串
     * @param data
     * @return
     */
    public static String byteTohex(byte data){
        int v = data & 0xFF;
        String str = Integer.toHexString(v);
        if(str.length() == 1){
            str = "0" + str;
        }
        return str;
    }

//    public static String getUTCTime(){
//        Calendar calendar = Calendar.getInstance();
//        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
//        int dstOffset = calendar.get(Calendar.DST_OFFSET);
//        calendar.add(Calendar.MILLISECOND,-(zoneOffset+dstOffset));
//        long utcTime = calendar.getTimeInMillis();
//        return new Date(utcTime).toString();


//        Calendar calendar1 = Calendar.getInstance();
//        TimeZone tztz = TimeZone.getTimeZone("GMT");
//        calendar1.setTimeZone(tztz);
//        System.out.println("cc"+calendar.getTime());
//        System.out.println("dd"+calendar.getTimeInMillis());//这个地方获得的utc时间，在用utc转成日期，是没有问题的

//    }


    /**
     * utc转16进制字符串
     * @return
     */
    public static String getLongToHex(long time){
//        return Long.toHexString((System.currentTimeMillis() + 60 * 1000) /1000);
        return Long.toHexString(time);
    }

    /**
     * utc转16进制字符串
     * @return
     */
    public static String getLongToHexTest(){
        long ttt = System.currentTimeMillis();
        return Long.toHexString((ttt + 60 * 1000) /1000);
    }

    /**
     * 十六进制转十进制
     */
    public static int getHexToDec(String hexStrnig){
        return Integer.parseInt(hexStrnig,16);
    }

    /**
     * 十进制数转byte
     * @param hexInt
     * @return
     */
    public static byte getDexToByte(int hexInt){
        return (byte)hexInt;
    }

    /**
     * 十六进制转byte
     * @param hexString
     * @return
     */
    public static byte getHexToByte(String hexString){
        return getDexToByte(getHexToDec(hexString));
    }


    public static byte getStringToByte(String releatDay){
        BigInteger src1= new BigInteger(releatDay,2);//转换为BigInteger类型
        return src1.byteValue();//转换为10进制并输出结果
    }


}
