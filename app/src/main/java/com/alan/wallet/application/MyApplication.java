package com.alan.wallet.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.alan.wallet.greendao.DaoMaster;
import com.alan.wallet.greendao.DaoSession;
import com.alan.wallet.greendao.MySQLiteOpenHelper;
import com.alan.wallet.utils.Logger;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

public class MyApplication extends Application {


    private static Web3j web3j;

    public static Web3j getWeb3j() {
        return web3j;
    }

    private static DaoSession daoSession;
    public static DaoSession getDaoInstant() {
        return daoSession;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        //先执行上面代码再执行初始化
        //配置数据库
        setupDatabase();
    }
    /**
     * 配置数据库
     */
    private void setupDatabase() {

        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, "wallet.db",
                null);


//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, Configs.DEVICE_DATABASE_FILE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
//        daoMaster = new DaoMaster(helper.getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        web3j = Web3jFactory.build(new HttpService("https://ropsten.infura.io/XLN13NCI3kUmHdTJ0XDv"));
        Logger.setDebug(true);
    }
}
