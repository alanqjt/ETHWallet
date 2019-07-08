package com.alan.btctest;

import java.util.List;

public class BtcWallet {

    //测试网地址
    private String address_T;
    //正式网地址
    private String address_M;
    //隔离见证地址
    private String address_S;

    //助记词
    List<String> mnemonicWords;

    //公钥
    private String publickey;
    //私钥
    private String privateKey;


    @Override
    public String toString() {
        return "BtcWallet{" +
                "address_T='" + address_T + '\'' +
                ", address_M='" + address_M + '\'' +
                ", address_S='" + address_S + '\'' +
                ", mnemonicWords=" + mnemonicWords +
                ", publickey='" + publickey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }

    public String getAddress_T() {
        return address_T;
    }

    public void setAddress_T(String address_T) {
        this.address_T = address_T;
    }

    public String getAddress_M() {
        return address_M;
    }

    public void setAddress_M(String address_M) {
        this.address_M = address_M;
    }

    public String getAddress_S() {
        return address_S;
    }

    public void setAddress_S(String address_S) {
        this.address_S = address_S;
    }

    public List<String> getMnemonicWords() {
        return mnemonicWords;
    }

    public void setMnemonicWords(List<String> mnemonicWords) {
        this.mnemonicWords = mnemonicWords;
    }

    public String getPublickey() {
        return publickey;
    }

    public void setPublickey(String publickey) {
        this.publickey = publickey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
