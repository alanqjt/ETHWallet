package com.alan.wallet.greendao;

import com.alan.wallet.application.MyApplication;
import com.alan.wallet.bean.Wallet;

import java.util.List;

public class WalletDaoMaster {

    public static void insertWallet(Wallet entry) {
        MyApplication.getDaoInstant().getWalletDao().insertOrReplace(entry);
    }

    public static void deleteWallet(Wallet entry) {
        MyApplication.getDaoInstant().getWalletDao().delete(entry);
    }


    public static List<Wallet> queryAllWallet() {
        return MyApplication.getDaoInstant().getWalletDao().loadAll();
    }


    public static Wallet queryWalletByAddress(String address) {
        return MyApplication.getDaoInstant().getWalletDao()
                .queryBuilder()
                .where(WalletDao.Properties.Address.eq(address))
                .build()
                .unique();
    }


}
