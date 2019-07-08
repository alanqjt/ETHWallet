package com.alan.wallet.ethwallet;

import org.json.JSONObject;
import org.web3j.crypto.ECKeyPair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.math.BigInteger;

public class EthWallet implements Serializable {

    private EthWalletService ethWalletService;
    private File walletFile;
    private String address;
    private BigInteger balance;
    private ECKeyPair ECKeyPair;

    private String walletName;
    private String walletPassword;
    private String walletPasswordHit;
    private boolean isBackUp = false;

    com.quincysx.crypto.ECKeyPair decrypt;

    protected EthWallet(File walletFile, EthWalletService ethWalletService, ECKeyPair ECKeyPair, String walletName, String walletPassword, String walletPasswordHit, boolean isBackUp) {
        this.ethWalletService = ethWalletService;
        this.walletFile = walletFile;
        this.ECKeyPair = ECKeyPair;
        this.walletName = walletName;
        this.walletPassword = walletPassword;
        this.walletPasswordHit = walletPasswordHit;
        this.isBackUp = isBackUp;
        this.load();
    }

    protected EthWallet(File walletFile, EthWalletService ethWalletService, com.quincysx.crypto.ECKeyPair decrypt, String walletName, String walletPassword, String walletPasswordHit, boolean isBackUp) {
        this.ethWalletService = ethWalletService;
        this.walletFile = walletFile;
        this.decrypt = decrypt;
        this.walletName = walletName;
        this.walletPassword = walletPassword;
        this.walletPasswordHit = walletPasswordHit;
        this.isBackUp = isBackUp;
        this.load();
    }

    public com.quincysx.crypto.ECKeyPair getdecryptECKeyPair() {
        return decrypt;
    }

    public org.web3j.crypto.ECKeyPair getWeb3jECKeyPair() {
        return ECKeyPair;
    }

    private void load() {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(this.walletFile));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

            JSONObject jsonWallet = new JSONObject(sb.toString());

            this.address = jsonWallet.getString("address");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAddress() {
        return address;
    }

    public BigInteger getBalance() throws Exception {
        return this.ethWalletService.getBalanceAddress(this.getAddress());
    }

    public void setBackUp(boolean backUp) {
        isBackUp = backUp;
    }

    public EthWalletService getEthWalletService() {
        return ethWalletService;
    }

    public File getWalletFile() {
        return walletFile;
    }

    public org.web3j.crypto.ECKeyPair getECKeyPair() {
        return ECKeyPair;
    }

    public String getWalletName() {
        return walletName;
    }

    public String getWalletPassword() {
        return walletPassword;
    }

    public String getWalletPasswordHit() {
        return walletPasswordHit;
    }

    public boolean isBackUp() {
        return isBackUp;
    }

    public com.quincysx.crypto.ECKeyPair getDecrypt() {
        return decrypt;
    }

    @Override
    public String toString() {
        return "EthWallet{" +
                "ethWalletService=" + ethWalletService +
                ", walletFile=" + walletFile +
                ", address='" + address + '\'' +
                ", balance=" + balance +
                ", ECKeyPair=" + ECKeyPair +
                ", walletName='" + walletName + '\'' +
                ", walletPassword='" + walletPassword + '\'' +
                ", walletPasswordHit='" + walletPasswordHit + '\'' +
                ", isBackUp=" + isBackUp +
                ", decrypt=" + decrypt +
                '}';
    }
}
