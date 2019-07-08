package com.alan.wallet.utils;

import android.text.InputFilter;
import android.text.Spanned;


import com.alan.wallet.view.PasswordLevelView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordUtils {

    /**
     * 密码强度
     *
     * @return Z = 字母 S = 数字 T = 特殊字符
     */
    public static PasswordLevelView.Level checkPassword(String passwordStr) {
        //数字
        String regexZ = "\\d*";
        //大小写字母
        String regexS = "[a-zA-Z]+";
        //特殊字符
        String regexT = "\\W+$";
        //非数字字符
        String regexZT = "\\D*";
        //数字和特殊字符
        String regexST = "[\\d\\W]*";
        //常规字符
        String regexZS = "\\w*";
        //常规和非常规字符
        String regexZST = "[\\w\\W]*";

        if (passwordStr.length()==0){
//            Log.v("ccm","str match:"+0);
            return null;
        }

        if (passwordStr.length()<8){
//            Log.v("ccm","str match:"+1);
            return PasswordLevelView.Level.DANGER;
        }

        if (passwordStr.matches(regexZ)) {
//            Log.v("ccm","str match:"+2);
            return PasswordLevelView.Level.LOW;
        }
        if (passwordStr.matches(regexS)) {
//            Log.v("ccm","str match:"+3);
            return PasswordLevelView.Level.LOW;
        }
        if (passwordStr.matches(regexT)) {
//            Log.v("ccm","str match:"+4);
            return PasswordLevelView.Level.LOW;
        }
        if (passwordStr.matches(regexZT)) {
//            Log.v("ccm","str match:"+5);
            return PasswordLevelView.Level.MID;
        }
        if (passwordStr.matches(regexST)) {
//            Log.v("ccm","str match:"+6);
            return PasswordLevelView.Level.MID;
        }
        if (passwordStr.matches(regexZS)) {
//            Log.v("ccm","str match:"+7);
            return PasswordLevelView.Level.MID;
        }
        if (passwordStr.matches(regexZST)) {
//            Log.v("ccm","str match:"+8);
            return PasswordLevelView.Level.STRONG;
        }
//        Log.v("ccm","str match:"+9);
        return null;

    }

    /**
     * 只允许EditText输入数字/字母/`~!@#$%^&*()_+
     * 不允许输入换行符号,空格
     * @return
     */
    public static InputFilter generateEditTextFilter1() {
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                String speChat = "[0-9A-Za-z`~!@#$%^&*()_+]";
//                String speChat = "[0-9A-Za-z~!@#$%^*&amp;()_+-=]";
                Pattern pattern = Pattern.compile(speChat);
                Matcher matcher = pattern.matcher(source.toString());

                if (source.equals(" ") || source.toString().contentEquals("\n")) {
                    return "";
                }else if(!matcher.find()){
                    return "";
                } else {
                    return null;
                }

            }
        };
        return filter;
    }


}
