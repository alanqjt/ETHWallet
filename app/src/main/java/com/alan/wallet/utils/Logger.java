package com.alan.wallet.utils;

import android.util.Log;

import java.text.SimpleDateFormat;

public class Logger {

    private static final String TAG = "vending";
    private static SimpleDateFormat loginfo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
    public static boolean DEBUG = false;

    public static void setDebug(boolean flag) {
        DEBUG = flag;
    }

    public static void v(Object tag, Object info) {
        StringBuffer toStringBuffer = new StringBuffer("[");
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
        if (tag != null) {
            Class<? extends Object> clz = tag.getClass();
            if (clz.getName().equals(String.class.getName())) {
                toStringBuffer.append(TAG).append("|").append(tag).append("|").append(traceElement.getMethodName())
                        .append("]");
            } else {
                toStringBuffer.append(TAG).append("|").append(clz.getSimpleName()).append("|")
                        .append(traceElement.getMethodName()).append("]");
            }

        } else {
            toStringBuffer.append(TAG).append("|").append(traceElement.getMethodName()).append("]");
        }
        Log.e(toStringBuffer.toString(), "" + info);
    }

    public static void d(Object tag, Object info) {
        if (DEBUG) {
            StringBuffer toStringBuffer = new StringBuffer("[");
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            if (tag != null) {
                Class<? extends Object> clz = tag.getClass();
                if (clz.getName().equals(String.class.getName())) {
                    toStringBuffer.append(TAG).append("|").append(tag).append("|").append(traceElement.getMethodName())
                            .append("]");
                } else {
                    toStringBuffer.append(TAG).append("|").append(clz.getSimpleName()).append("|")
                            .append(traceElement.getMethodName()).append("]");
                }

            } else {
                toStringBuffer.append(TAG).append("|").append(traceElement.getMethodName()).append("]");
            }

            Log.d(toStringBuffer.toString(), "" + info);
        }
    }

    public static void save(Object tag, Object info) {
        if (DEBUG) {
            StringBuffer toStringBuffer = new StringBuffer("[");
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            if (tag != null) {
                Class<? extends Object> clz = tag.getClass();
                if (clz.getName().equals(String.class.getName())) {
                    toStringBuffer.append(TAG).append("|").append(tag).append("|").append(traceElement.getMethodName())
                            .append("]");
                } else {
                    toStringBuffer.append(TAG).append("|").append(clz.getSimpleName()).append("|")
                            .append(traceElement.getMethodName()).append("]");
                }

            } else {
                toStringBuffer.append(TAG).append("|").append(traceElement.getMethodName()).append("]");
            }
            Log.d(toStringBuffer.toString(), "" + info);
        }
    }
}