package com.a99live.zhibo.live.utils;


import android.text.TextUtils;

import com.a99live.zhibo.live.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 时间工具类
 * 单位：秒
 */
public class TimeUtils {


    /**
     * 将时间（单位：秒）格式化为 X天X小时X秒的形式
     */
    public static String timeForDayHours(int time) {
        int currentTime = (int) (System.currentTimeMillis() / 1000);

        if (currentTime < time) return "";

        int dTime = currentTime - time;
        int dHour = dTime / 60 / 60;
        if (dHour > 24) {
            return timeFormat(time);
        } else {
            if (dHour > 0) {
                return dHour + "小时" + dTime % (60 * 60) / 60 + "分钟前";
            } else {
                int m = dTime % (60 * 60) / 60;
                if (m > 0) {
                    return m + "分钟前";
                } else {
                    return "刚刚";
                }
            }
        }
    }

    /**
     * 格式化为yyyy-MM-dd hh:mm:ss
     *
     * @param time 秒
     */
    public static String timeFormat(long time) {
        time *= 1000;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(time));
    }

    /**
     * 转化为时分秒
     */
    public static String getHMS(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat fmat = new SimpleDateFormat("HH:mm:ss");
        String curTime = fmat.format(calendar.getTime());
        return curTime;
    }

    /**
     * 格式化为yyyy-MM-dd
     *
     * @param time 秒
     */
    public static String timeFormatYMD(long time) {
        time *= 1000;
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
    }


    /**
     * 根据传入的时间戳计算分组时间戳
     * 参数单位：秒
     * 返回单位：秒  分组基准
     */
    public static long byGroupTimestamp(long time) {
        long result = 0;
        try {
            result = new SimpleDateFormat("yyyy-MM-dd").parse(timeFormatYMD(time)).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将字符串解析为时间戳
     */
    public static long parseString(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) {
            return 0;
        } else if (UIUtils.getString(R.string.today).equals(timeStr)) {
            return byGroupTimestamp(System.currentTimeMillis() / 1000);
        } else if (UIUtils.getString(R.string.yesterday).equals(timeStr)) {
            return byGroupTimestamp(System.currentTimeMillis() / 1000 - 24 * 60 * 60);
        } else {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(timeStr).getTime() / 1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static String timeFormatHour(long time) {
        return new SimpleDateFormat("HH:mm").format(new Date(time * 1000));
    }

    public static String timeForDay(long time) {
        long today = System.currentTimeMillis() / 1000;
        long yesterday = System.currentTimeMillis() - 24 * 60 * 60;
        String sTime = timeFormatYMD(time);
        if (timeFormatYMD(today).equals(sTime)) {
            return "今天";
        } else if (timeFormatYMD(yesterday).equals(sTime)) {
            return "明天";
        } else {
            return timeFormat(time);
        }
    }
    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    // long类型转换为String类型
    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    /*
 * 毫秒转化时分秒毫秒
 */
    public static String timeToDayHourMilS(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if(day >= 0) {
            sb.append(day+"天 ");
        }
        if(hour >= 0) {
            sb.append(hour+"小时 ");
        }
        if(minute >= 0) {
            sb.append(minute+"分 ");
        }
        if(second >= 0) {
            sb.append(second+"秒");
        }
//        if(milliSecond > 0) {
//            sb.append(milliSecond+"毫秒");
//        }
        return sb.toString();
    }

}
