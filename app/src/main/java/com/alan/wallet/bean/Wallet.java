package com.alan.wallet.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
/**
 * Created by Alan on 2018/5/22.
 */
@Entity
public class Wallet {

//    @Id(autoincrement = true)
//    private Long id;

    private String walletName;
    private String walletPassword;
    private String walletPasswordHit;
    private boolean isBackUp = false;

    @Id
    private String address;
    private String privateKey;
    private String publicKey;

    private String mnemonicWords;

    //帮记词生成种子的密码
    private String password;


    private String keyStore;

    private String keyStoreFilePath;

    @Generated(hash = 1918663456)
    public Wallet(String walletName, String walletPassword,
                  String walletPasswordHit, boolean isBackUp, String address,
                  String privateKey, String publicKey, String mnemonicWords,
                  String password, String keyStore, String keyStoreFilePath) {
        this.walletName = walletName;
        this.walletPassword = walletPassword;
        this.walletPasswordHit = walletPasswordHit;
        this.isBackUp = isBackUp;
        this.address = address;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.mnemonicWords = mnemonicWords;
        this.password = password;
        this.keyStore = keyStore;
        this.keyStoreFilePath = keyStoreFilePath;
    }

    @Generated(hash = 1197745249)
    public Wallet() {
    }

    public String getWalletName() {
        return this.walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletPassword() {
        return this.walletPassword;
    }

    public void setWalletPassword(String walletPassword) {
        this.walletPassword = walletPassword;
    }

    public String getWalletPasswordHit() {
        return this.walletPasswordHit;
    }

    public void setWalletPasswordHit(String walletPasswordHit) {
        this.walletPasswordHit = walletPasswordHit;
    }

    public boolean getIsBackUp() {
        return this.isBackUp;
    }

    public void setIsBackUp(boolean isBackUp) {
        this.isBackUp = isBackUp;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getMnemonicWords() {
        return this.mnemonicWords;
    }

    public void setMnemonicWords(String mnemonicWords) {
        this.mnemonicWords = mnemonicWords;
    }

    public String getKeyStore() {
        return this.keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStoreFilePath() {
        return this.keyStoreFilePath;
    }

    public void setKeyStoreFilePath(String keyStoreFilePath) {
        this.keyStoreFilePath = keyStoreFilePath;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public String toString() {
        return "Wallet{" +
                "walletName='" + walletName + '\'' +
                ", walletPassword='" + walletPassword + '\'' +
                ", walletPasswordHit='" + walletPasswordHit + '\'' +
                ", isBackUp=" + isBackUp +
                ", address='" + address + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", mnemonicWords='" + mnemonicWords + '\'' +
                ", password='" + password + '\'' +
                ", keyStore='" + keyStore + '\'' +
                ", keyStoreFilePath='" + keyStoreFilePath + '\'' +
                '}';
    }
}
