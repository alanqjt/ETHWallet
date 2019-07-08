package com.alan.wallet.utils;

public class SPConfig {

    /**
     * 保存用户的重要信息
     **/
    public static final String SP_USER_INFO_NAME = "SP_USER_INFO_NAME";
    /**
     * 保存语言设置
     **/
    public static final String SP_USER_INFO_LANGUAGE = "SP_USER_INFO_LANGUAGE";
    public static final String SP_USER_INFO_WALLETPOSTION = "SP_USER_INFO_WALLETPOSTION";
    /**
     * 判断用户有没有选择语言设置了
     **/
    public static final String SP_USER_INFO_FIRST_RUN = "SP_USER_INFO_FIRST_RUN";

    /**
     * 英语
     **/
    public static final int LANGUAGE_ENGLISH = 0;
    /**
     * 中文繁体
     **/
    public static final int LANGUAGE_CHINESE = 1;
    /**
     * 中文简体
     **/
    public static final int LANGUAGE_CHINESE_SIM = 2;



    /**
     * 人民币
     **/
    public static final int CURRENCYINFO_ID_CNY = 1;
    public static final String CURRENCYINFO_NAME_CNY = "CNY";
    public static final String CURRENCYINFO_SYMBOL_CNY = "￥";
    /**
     * 美元
     **/
    public static final int CURRENCYINFO_ID_USD = 2;
    public static final String CURRENCYINFO_NAME_USD = "USD";
    public static final String CURRENCYINFO_SYMBOL_USD = "$";
    /**
     * 港币
     **/
    public static final int CURRENCYINFO_ID_HKD = 3;
    public static final String CURRENCYINFO_NAME_HKD = "HKD";
    public static final String CURRENCYINFO_SYMBOL_HKD = "HK$";
    /**
     * 澳元
     **/
    public static final int CURRENCYINFO_ID_AUD = 4;
    public static final String CURRENCYINFO_NAME_AUD = "AUD";
    public static final String CURRENCYINFO_SYMBOL_AUD= "A$";

}
