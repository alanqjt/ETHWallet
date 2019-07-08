package com.alan.wallet.utils;

import java.util.regex.Pattern;

/**
 * Created by Alan on 2018/5/7.
 */
public class RegexUtil {

    /**
     * 正则：手机号（精确）
     * <p>移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188、198</p>
     * <p>联通：130、131、132、145、155、156、175、176、185、186、166</p>
     * <p>电信：133、153、173、177、180、181、189、199</p>
     * <p>全球星：1349</p>
     * <p>虚拟运营商：170</p>
     */
    public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\d{8}$";

    public static final String REGEX_EMAIL_EXACT = "^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$";

    /**
     * 8-16 位，字母、数字
     **/
    public static final String REGEX_PASSWORD_EXACT = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,16}$";

    /**
     * 验证手机号（精确）
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMobileExact(String input) {
        return isMatch(REGEX_MOBILE_EXACT, input);
    }
    /**
     * 验证手机号（精确）
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isEmailExact(String input) {
        return isMatch(REGEX_EMAIL_EXACT, input);
    }

    /**
     * 验证密码格式
     * 以 6 -20 位，字母、数字、字符
     *
     * @param input 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isPasswordExact(String input) {
        return isMatch(REGEX_PASSWORD_EXACT, input);
    }


    private static boolean isMatch(String regex, String input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }


}
