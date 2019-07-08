package com.alan.wallet.ethwallet;

/**
 * Created by Alan on 2018/5/16.
 */
public interface EthWalletCallBack {

    void onSuccessCallBack(EthWallet ethWallet, String fileName, String walletAddress, String storeText) throws Exception;

    void onErrorCallBack(Exception e);
}
