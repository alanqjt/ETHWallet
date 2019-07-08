package com.alan.wallet.utils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <h1>常用工具类</h1> 1.字符串常用操作 <br>
 * 2.日期时间操作 <br>
 * 提供静态方法，不可以实例化。
 */
@SuppressWarnings("rawtypes")
public class StrUtil {

    /**
     * 禁止实例化
     **/
    private StrUtil() {
    }

    /**
     * 检查字符串是否为null或者为空串<br>
     * 为空串或者为null返回true，否则返回false
     */
    public static boolean isNullOrEmpty(String str) {
        boolean result = true;
        if (str != null && !"".equals(str.trim())) {
            result = false;
        }
        return result;
    }

    /**
     * 判断数组是否为null或者size为0 <br>
     * 为null或size为0时返回true，否则返回false
     *
     * @param obj
     * @return
     */
    public static boolean isArrayNull(Object[] obj) {
        if (obj == null || obj.length <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断list是否为null或者size为0 <br>
     * 为null或size为0时返回true，否则返回false
     *
     * @return
     */
    public static boolean isListNull(List list) {
        if (list == null || list.size() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 检查字符串是否为null或者为"null"<br>
     * 为null或者为"null",返回""，否则返回字符串 <br>
     * 用于处理数据库查询数据
     */
    public static String getCleanString(Object obj) {
        if (obj == null) {
            return "";
        } else if (String.valueOf(obj).equals("null")) {
            return "";
        } else {
            return String.valueOf(obj);
        }
    }

    /**
     * 检查整形数据是否为null<br>
     * 为null,返回0，否则返回原值 <br>
     * 用于处理数据库查询数据
     */
    public static int getCleanInteger(Object obj) {
        if (obj == null) {
            return 0;
        } else {
            return Integer.parseInt((String) obj);
        }
    }

    /**
     * 检查float数据是否为null<br>
     * 为null,返回0，否则返回原值 <br>
     * 用于处理数据库查询数据
     */
    public static float getCleanFloat(Object obj) {
        if (obj == null) {
            return 0f;
        } else {
            return Float.parseFloat((String) obj);
        }
    }

    /**
     * 数组转换为字符串 <br>
     * 转换失败或数组为null,返回""
     *
     * @param strArray 要转换的数组
     * @param split    分割符
     * @return
     */
    public static String strArrayByStr(Object[] strArray, String split) {
        // return StringUtils.join(strArray, split);
        StringBuilder ss = new StringBuilder();
        for (int i = 0, len = (strArray == null ? 0 : strArray.length); i < len; i++) {
            if (i > 0)
                ss.append(split);
            ss.append(strArray[i]);
        }
        return ss.toString();
    }

    /**
     * 两个字符串取不为null的值，都为null则返回""
     *
     * @param str1
     * @param str2
     * @return 优先返回str1
     */
    public static String emailOrAlias(String str1, String str2) {
        String str = getCleanString(str1);
        if (null == str1 || "".equals(str1)) {
            str = getCleanString(str2);
        }
        return str;
    }

    // ================================ 日期常用方法 =============================

    /**
     * 时间戳 转换
     **/
    public static String getTimeStamp(long time) {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss")
                .format(new Date(time));
        return timeStamp;
    }
    /**
     * 时间戳 转换
     **/
    public static String getTimeStamp(long time, String pattern) {
        String timeStamp = new SimpleDateFormat(pattern)
                .format(new Date(time));
        return timeStamp;
    }

    /**
     * 获得本周第一天时间(yyyy-MM-dd)
     **/
    public static String getMonOfWeek() {
        // Calendar c = new GregorianCalendar();
        // c.setFirstDayOfWeek(Calendar.MONDAY);
        // c.setTime(new Date());
        // c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, c.getActualMinimum(Calendar.DAY_OF_WEEK));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(c.getTime());
        return str;
    }

    /**
     * 获得本周的最后一天(yyyy-MM-dd)
     **/
    public static String getSunOfWeek() {
        // Calendar c = new GregorianCalendar();
        // c.setFirstDayOfWeek(Calendar.MONDAY);
        // c.setTime(new Date());
        // c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, c.getActualMaximum(Calendar.DAY_OF_WEEK));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(c.getTime());
        return str;
    }

    /**
     * 获得当月的第一天(yyyy-MM-dd)
     **/
    public static String getFirstDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(cal.getTime());
        return str;
    }

    /**
     * 获得当月的最后一天(yyyy-MM-dd)
     **/
    public static String getLastDayOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdfs.format(cal.getTime());
        return str;
    }

    /**
     * 根据时间获取相差天、小时数的新时间
     *
     * @param date 参照时间
     * @param type 天或小时[d/h](大小写敏感)
     * @param num  差值，例如：2表示之后2天或小时，-2表示之前2天或小时
     * @return
     */
    public static Date getNextDay(Date date, char type, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (type) {
            case 'd':
                calendar.add(Calendar.DAY_OF_MONTH, num);
                break;
            case 'h':
                calendar.add(Calendar.HOUR_OF_DAY, num);
                break;
            default:
                break;
        }
        date = calendar.getTime();
        return date;
    }

    /**
     * 比较两个时间的相差值（d1与d2）
     *
     * @param d1   时间一
     * @param d2
     * @param type 类型【h/m/s】(大小写敏感)
     * @return d1-d2，表示d1比d2早或晚xxx
     */
    public static long compareDate(Date d1, Date d2, char type) {
        try {
            long num = d1.getTime() - d2.getTime();
            num /= 1000;
            if ('m' == type) {
                num /= 60;
            } else if ('h' == type) {
                num /= 3600;
            }
            return num;
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    /**
     * 格式化时间，时间转字符串
     *
     * @param date   null则为当前系统时间
     * @param format 格式，null则默认为：'yyyy-MM-dd HH:mm:ss'
     * @return 字符串格式的日期
     */
    public static String getDateTimeByStr(Date date, String format) {
        if (date == null)
            date = new Date();
        if (format == null)
            format = "yyyy-MM-dd HH:mm:ss";
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 格式化时间，字符串转时间
     *
     * @param dataStr 需要转换的字符串
     * @param format  格式，null则默认为：'yyyy-MM-dd HH:mm:ss'
     * @return 转换的Date
     */
    public static Date getStrByDataTime(String dataStr, String format) {
        if (dataStr == null)
            return new Date();
        if (format == null)
            format = "yyyy-MM-dd HH:mm:ss";
        DateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(dataStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(String date, String strformat) {
        SimpleDateFormat format = new SimpleDateFormat(strformat);
        Date time = getStrByDataTime(date, null);
        if (time == null) {
            return StrUtil.getDateTimeByStr(new Date(), strformat);
        }
        return format.format(time);
    }

    // ================================ 其他常用方法 =============================

    /**
     * 从number数值中随机抽取count个数字
     *
     * @param count  要抽取的数值个数
     * @param number 数字范围为 (0, number]
     * @return
     */
    public static int[] randomNumber(int count, int number) {
        int k = count, n = number;
        int[] numbers = new int[n];
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = i + 1;
        int[] result = new int[k];
        for (int i = 0; i < result.length; i++) {
            int r = (int) (Math.random() * n);
            result[i] = numbers[r];
            numbers[r] = numbers[n - 1];
            n--;
        }

        // Arrays.sort(result);
        // for (int r : result)
        // System.out.println(r);
        return result;

    }


    /**
     * 检验字符串是否是一个标准的Json
     **/
    public static boolean isGoodJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            System.out.println("bad json: " + json);
            return false;
        }
    }


    /**
     * 读取txt文件的内容
     *
     * @param file 想要读取的文件对象
     * @return 返回文件内容 File file = new File("D:/XXX.txt");
     */
    public static String getTxtString(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = br.readLine()) != null) {
                result.append(System.getProperty("line.separator") + s);
            }
            br.close();
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
        return result.toString();
    }


    /**
     * 传入一个String   数  值
     **/
    public static String formatStringOfNum(String val) {
        try {
            //传入的值没有小数点 表示这个 是 一个整数 不做处理 直接返回
            if (!val.contains(".")){
                return val;
            }

            for (int i = 0; i < 9999999; i++) {
                if (val.length()==1)
                    break;
                if (val.contains(".")) {
                    if (val.endsWith("0") || val.endsWith(".")) {
                        val = val.substring(0, val.length() - 1);
                    } else {
                        break;
                    }
                } else {
                    //没有包含小数点的 那表明是个整数
                    break;
                }

            }
            return val;
        } catch (Exception e) {
            e.printStackTrace();
            return val;
        }
    }


}
