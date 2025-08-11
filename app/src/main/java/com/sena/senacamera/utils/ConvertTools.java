package com.sena.senacamera.utils;

import com.sena.senacamera.log.AppLog;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ConvertTools {
    private static final String TAG = ConvertTools.class.getSimpleName();

    private static String timeFormatFromFw = "yyyyMMdd'T'HHmmss";
    private static String timeFormatFromApp = "yyyy-MM-dd HH:mm:ss";
    public static String secondsToMinuteOrHours(int remainTime) {
        String time = "";
        if (remainTime < 0) {
            return "--:--:--";
        }
        Integer h = remainTime / 3600;
        Integer m = (remainTime % 3600) / 60;
        Integer s = remainTime % 60;

        if (h > 0) {
            if (h < 10) {
                time = "0" + h.toString();
            } else {
                time = h.toString();
            }
            time = time + ":";
        }

        if (m < 10) {
            time = time + "0" + m.toString();
        } else {
            time = time + m.toString();
        }
        time = time + ":";
        if (s < 10) {
            time = time + "0" + s.toString();
        } else {
            time = time + s.toString();
        }
        return time;
    }

    public static String secondsToMinute(int remainTime) {
        String time = "";
        if (remainTime < 0) {
            return "--:--:--";
        }
        Integer h = remainTime / 3600;
        Integer m = (remainTime % 3600) / 60;
        Integer s = remainTime % 60;

        if (h > 0) {
            if (h < 10) {
                time = "0" + h.toString();
            } else {
                time = h.toString();
            }
            time = time + ":";
        }

        if (m < 10) {
            time = time + "0" + m.toString();
        } else {
            time = time + m.toString();
        }
        time = time + ":";
        if (s < 10) {
            time = time + "0" + s.toString();
        } else {
            time = time + s.toString();
        }
        return time;
    }

    public static String millisecondsToMinuteOrHours(int remainTime) {
        int durationSec = 0 ;
        int duration = remainTime;
        if (duration > 0) {
            durationSec =  duration / 1000;
        }
        return  ConvertTools.secondsToMinuteOrHours(durationSec);
    }

    public static String secondsToHours(int remainTime) {
        String time = "";
        Integer h = remainTime / 3600;
        Integer m = (remainTime % 3600) / 60;
        Integer s = remainTime % 60;
        if (h < 10) {
            time = "0" + h.toString();
        } else {
            time = h.toString();
        }
        time = time + ":";
        if (m < 10) {
            time = time + "0" + m.toString();
        } else {
            time = time + m.toString();
        }
        time = time + ":";
        if (s < 10) {
            time = time + "0" + s.toString();
        } else {
            time = time + s.toString();
        }
        return time;
    }

    static double GB = 1024 * 1024 * 1024;//定义GB的计算常量
    static double MB = 1024 * 1024;//定义MB的计算常量
    static double KB = 1024;//定义KB的计算常量

    public static String ByteConversionGBMBKB(long KSize) {
        String fileSize;
        DecimalFormat df = new DecimalFormat("######0.0");
        if (KSize / GB >= 1)
            return df.format(KSize / GB).toString() + "G";
        else if (KSize / MB >= 1)//如果当前Byte的值大于等于1MB
            return df.format(KSize / MB).toString() + "M";
        else if (KSize / KB >= 1)//如果当前Byte的值大于等于1KB
            return df.format(KSize / KB).toString() + "K";
        else
            return String.valueOf(KSize) + "B";
    }

    public static String resolutionConvert(String resolution) {
        AppLog.d(TAG, "start resolution = " + resolution);
        String ret = null;
        String[] temp;
        temp = resolution.split("\\?|&");
        temp[1] = temp[1].replace("W=", "");
        temp[2] = temp[2].replace("H=", "");
        temp[3] = temp[3].replace("BR=", "");
        ret = temp[0] + "?W=" + temp[1] + "&H=" + temp[2] + "&BR=" + temp[3];

        if (resolution.contains("FPS")) {
            if (temp[2].equals("720")) {
                ret = ret + "&FPS=15&";
            } else if (temp[2].equals("1080")) {
                ret = ret + "&FPS=10&";
            } else {
                ret = resolution;
            }
        } else {
            ret = resolution;
        }

        AppLog.d(TAG, "end ret = " + ret);
        return ret;
    }


    public static String getTimeByFileDate(String fileDate) {
        //20161010T144422-->20161010
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormatFromFw);
        Date currentTime = null;
        try {
            currentTime = formatter.parse(fileDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        if (currentTime != null) {
            String date = formatter2.format(currentTime);
            return date;
        }
        return "formatError";
    }

    public static String getExposureCompensation(int value) {
        AppLog.d(TAG, "start getExposureCompensation value=" + value);
        String ret = "";
        int temp = 0x80000000;
        int temp2 = 0x40000000;
        int temp3 = 0x00ffffff;

        //获取最高位的值 ，1表示负数，0表示正数
        if ((value & temp) == temp) {
            ret = ret + "-";
        }
        //获取第二位的值 ，1表示小数点左移一位负数，0表示不移位
        int value2 = value & temp3;
        if ((value & temp2) == temp2) {
            ret = ret + value2 / 10.0 + "";
        } else {
            ret = ret + value2 / 1.0 + "";
        }
        AppLog.d(TAG, "End getExposureCompensation ret=" + ret);
        return ret;
    }


    public static long getDateTime(String dateString) {
        //20161010T144422 to long
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormatFromFw);
        Date currentTime = null;
        try {
            currentTime = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (currentTime != null) {
            return currentTime.getTime();
        }
        return 0;
    }

    public static String getDateTimeString(String dateString) {
        //20161010T144422 to 2016-10-10 14:44:22
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormatFromFw);
        Date currentTime = null;
        try {
            currentTime = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter2 = new SimpleDateFormat(timeFormatFromApp);
        if (currentTime != null) {
            String date = formatter2.format(currentTime);
            return date;
        }
        return "formatError";
    }

    public static String getHexStringFromByteArray(byte[] param) {
        StringBuilder result = new StringBuilder();
        for (byte b: param) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public static String getStringFromByteArray(byte[] param) {
        // get the end index by skipping 00 bytes
        int endIndex = param.length;
        for (int i = 0; i < param.length; i ++) {
            if (param[i] == 0) {
                endIndex = i;
                break;
            }
        }
        return new String(Arrays.copyOfRange(param, 0, endIndex), StandardCharsets.UTF_8);
    }

    public static byte getHiByte(byte[] param) {
        return param[0];
    }

    public static byte getLoByte(byte[] param) {
        return param[1];
    }

    public static byte[] addByteToByteArray(byte[] orgArray, byte newByte) {
        ByteBuffer buffer = ByteBuffer.allocate(orgArray.length + 1);
        buffer.put(orgArray);
        buffer.put(newByte);
        return buffer.array();
    }

    public static byte[] addByteArrayToByteArray(byte[] orgArray, byte[] newArray) {
        ByteBuffer buffer = ByteBuffer.allocate(orgArray.length + newArray.length);
        buffer.put(orgArray);
        buffer.put(newArray);
        return buffer.array();
    }

    public static byte[] getByteArrayFromHexString(String param) {
        String hexString = param.replaceAll("[^0-9a-fA-F]", "");
        int length = hexString.length();
        byte[] array = new byte[length / 2];

        for (int i = 0; i < array.length * 2; i += 2) {
            array[i / 2] = (byte)  ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return array;
    }

    public static byte[] getByteArrayFromString(String param, int length) {
        byte[] result = new byte[length];
        if (param.length() > length) {
            param = param.substring(0, length);
        }

        System.arraycopy(param.getBytes(StandardCharsets.UTF_8), 0, result, 0, param.length());
        return result;
    }
}
