package com.alan.wallet.ethwallet;

import android.os.Environment;
import android.util.Log;

import com.alan.wallet.bean.Wallet;
import com.alan.wallet.greendao.WalletDaoMaster;
import com.alan.wallet.utils.Logger;
import com.alan.wallet.utils.StrUtil;
import com.quincysx.crypto.CoinTypes;
import com.quincysx.crypto.bip32.ExtendedKey;
import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.bip44.AddressIndex;
import com.quincysx.crypto.bip44.BIP44;
import com.quincysx.crypto.bip44.CoinPairDerive;
import com.quincysx.crypto.ethereum.keystore.CipherException;
import com.quincysx.crypto.ethereum.keystore.KeyStore;
import com.quincysx.crypto.ethereum.keystore.KeyStoreFile;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

public class EthWalletManager {
    public static final String PATH_KEYSTORE = Environment.getExternalStorageDirectory() + "/keyStore/";
    private File directory;

    private ExecutorService fixedThreadPool;

    public EthWalletManager() {
        directory = new File(PATH_KEYSTORE);
        if (!directory.exists())
            directory.mkdirs();

        fixedThreadPool = Executors.newFixedThreadPool(3);
    }

    /**
     * 创建钱包
     **/
    public void createWallet(String walletName, String password, String walletPasswordHit, boolean isBackUp, final EthWalletCallBack ethWalletCallBack) throws Exception {
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //随机生成助记词
                    StringBuilder sb = new StringBuilder();
                    byte[] entropy = new byte[Words.TWELVE.byteLength()];
                    new SecureRandom().nextBytes(entropy);
                    new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, sb::append);
                    System.out.println(sb.toString());
                    String[] parts = sb.toString().split(" ");
                    //助记词 list
                    List<String> mnemonicWordsInAList = new ArrayList<>();
                    for (int i = 0; i < parts.length; i++) {
                        System.out.println(parts[i]);
                        mnemonicWordsInAList.add(parts[i]);
                    }
                    //助记词种子 byte
                    byte[] seed = getSeed(mnemonicWordsInAList);
                    Logger.d(this, "==========开始============");
                    ExtendedKey extendedKey = ExtendedKey.create(seed);
                    AddressIndex address = BIP44.m().purpose44().coinType(CoinTypes.Ethereum).account(0).external().address(0);
                    CoinPairDerive coinKeyPair = new CoinPairDerive(extendedKey);
                    com.quincysx.crypto.ECKeyPair master = coinKeyPair.derive(address);
                    Logger.d(this, "address1=" + address.toString());
                    Logger.d(this, "pr=" + master.getPrivateKey());
                    Logger.d(this, "pu=" + master.getPublicKey());
                    Logger.d(this, "address2=" + master.getAddress());
                    System.out.println("--------------------------------------------------------------------------");
                    // 通过  com.quincysx.crypto.ECKeyPair 的 私钥 byte[]       作为 web3j原生的ECKeyPair  PrivateKey生成 钥匙对
                    ECKeyPair ecKeyPair = ECKeyPair.create(master.getRawPrivateKey());
                    //16进制打印
                    String prk = ecKeyPair.getPrivateKey().toString(16);
                    String puk = ecKeyPair.getPublicKey().toString(16);
                    System.out.println("password:" + password);
                    System.out.println("ecKeyPair----Pr=" + ecKeyPair.getPrivateKey());
                    System.out.println("16进制打印的私钥=" + prk);
                    Logger.d(this, "--------------------------------------------------------------------------");
                    System.out.println("ecKeyPair----Pu=" + ecKeyPair.getPublicKey());
                    System.out.println("16进制打印的公钥=" + puk);
                    System.out.println("file:" + directory);
                    //通过web3j 原生 的 方法生成 wallet

                    File fileDirectory = new File(directory.getAbsolutePath());
                    if (!fileDirectory.exists()) {
                        fileDirectory.mkdirs();
                    }
                    String filename = WalletUtils.generateWalletFile(password, ecKeyPair, fileDirectory, false);


                    EthWallet wallet = new EthWallet(new File(fileDirectory + "/" + filename), EthWalletService.getInstance(), ecKeyPair, walletName, password, walletPasswordHit, isBackUp);
                    Log.d("EthWalletManager", "Wallet file: " + filename);

                    Wallet wallet1 = new Wallet();

                    wallet1.setWalletName(walletName);
                    wallet1.setWalletPassword(password);
                    wallet1.setWalletPasswordHit(walletPasswordHit);
                    wallet1.setIsBackUp(false);
                    wallet1.setAddress("0x" + wallet.getAddress());
                    wallet1.setPrivateKey("" + ecKeyPair.getPrivateKey());
                    wallet1.setPublicKey("" + ecKeyPair.getPublicKey());
                    wallet1.setKeyStoreFilePath(fileDirectory + "/" + filename);
                    String keyStore = StrUtil.getTxtString(new File(fileDirectory + "/" + filename));
                    wallet1.setMnemonicWords(sb.toString());
                    wallet1.setKeyStore(keyStore);
                    WalletDaoMaster.insertWallet(wallet1);
                    Logger.d(this, wallet1.toString());
                    ethWalletCallBack.onSuccessCallBack(wallet, filename, wallet.getAddress(), keyStore);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 文件导入
     **/
    public void importKeyStore(File filePath, String password, String walletName, String walletPasswordHit, boolean isBackUp, final EthWalletCallBack ethWalletCallBack) throws Exception {

        String keyStore = StrUtil.getTxtString(filePath);
        System.out.println("-------------------keyStore---------------------");
        System.out.println("keyStore = " + keyStore);
        KeyStoreFile keyStoreFile = null;
        try {
            keyStoreFile = KeyStoreFile.parse(keyStore);
        } catch (IOException e) {
            e.printStackTrace();
        }
        com.quincysx.crypto.ECKeyPair decrypt = null;

        try {
            decrypt = KeyStore.decrypt(password, keyStoreFile);
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        System.out.println("-----------------------------------------------");
        System.out.println(" ecKeyPair1.getPublicKey()=" + decrypt.getPublicKey());
        System.out.println("ecKeyPair1.getPrivateKey()=" + decrypt.getPrivateKey());
        System.out.println("ecKeyPair1.getAddress()=" + decrypt.getAddress());

        System.out.println("-----------------------------------------------");

        deleteKeystoreFile(decrypt.getAddress());
        // 通过  com.quincysx.crypto.ECKeyPair 的 私钥 byte[]       作为 web3j原生的ECKeyPair  PrivateKey生成 钥匙对
        ECKeyPair ecKeyPair = ECKeyPair.create(decrypt.getRawPrivateKey());
        //16进制打印
        String prk = ecKeyPair.getPrivateKey().toString(16);
        String puk = ecKeyPair.getPublicKey().toString(16);
        System.out.println("password:" + password);
        System.out.println("ecKeyPair----Pr=" + ecKeyPair.getPrivateKey());
        System.out.println("16进制打印的私钥=" + prk);
        Logger.d(this, "--------------------------------------------------------------------------");
        System.out.println("ecKeyPair----Pu=" + ecKeyPair.getPublicKey());
        System.out.println("16进制打印的公钥=" + puk);
        System.out.println("file:" + directory);


        File fileDirectory = new File(directory.getAbsolutePath());
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }

        String filename = WalletUtils.generateWalletFile(password, ecKeyPair, fileDirectory, false);
        File file = new File(fileDirectory.getAbsolutePath() + "/" + filename);
        EthWallet wallet = new EthWallet(file.getAbsoluteFile(), EthWalletService.getInstance(), decrypt, walletName, password, walletPasswordHit, true);
        Log.d("EthWalletManager", "Wallet file: " + filePath);
        String storeText = StrUtil.getTxtString(new File(file.getAbsolutePath()));


        List<Wallet> wallets = WalletDaoMaster.queryAllWallet();

        //sp中存有旧钱包
        for (int i = 0; i < wallets.size(); i++) {
            Wallet wallet1 = wallets.get(i);
            if (wallet1.getAddress().contains(decrypt.getAddress())) {
                wallet1.setWalletName(walletName);
                wallet1.setWalletPassword(password);
                wallet1.setWalletPasswordHit(walletPasswordHit);
                wallet1.setIsBackUp(false);
                wallet1.setAddress("0x" + wallet.getAddress());
                wallet1.setPrivateKey("" + ecKeyPair.getPrivateKey());
                wallet1.setPublicKey("" + ecKeyPair.getPublicKey());
                wallet1.setKeyStoreFilePath(fileDirectory + "/" + filename);
                wallet1.setKeyStore(keyStore);
                WalletDaoMaster.insertWallet(wallet1);
                ethWalletCallBack.onSuccessCallBack(wallet, filePath.getName(), wallet.getAddress(), storeText);

                return;
            }
        }

        //sp中没有旧钱包
        Wallet wallet1 = new Wallet();

        wallet1.setWalletName(walletName);
        wallet1.setWalletPassword(password);
        wallet1.setWalletPasswordHit(walletPasswordHit);
        wallet1.setIsBackUp(true);
        wallet1.setAddress("0x" + wallet.getAddress());
        wallet1.setPrivateKey("" + ecKeyPair.getPrivateKey());
        wallet1.setPublicKey("" + ecKeyPair.getPublicKey());
        wallet1.setKeyStoreFilePath(fileDirectory + "/" + filename);
        wallet1.setMnemonicWords("");
        wallet1.setKeyStore(keyStore);
        WalletDaoMaster.insertWallet(wallet1);
        ethWalletCallBack.onSuccessCallBack(wallet, filePath.getName(), wallet.getAddress(), storeText);
    }

    /**
     * 文本导入
     **/
    public void importKeyStore(String content, String password, String walletName, String walletPasswordHit, boolean isBackUp, final EthWalletCallBack ethWalletCallBack) throws Exception {

        System.out.println("-----------------------------------------------");

        System.out.println("-------------------keyStore---------------------");
        System.out.println("keyStore = " + content);
        KeyStoreFile keyStoreFile = null;
        try {
            keyStoreFile = KeyStoreFile.parse(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        com.quincysx.crypto.ECKeyPair decrypt = null;

        try {
            decrypt = KeyStore.decrypt(password, keyStoreFile);
        } catch (CipherException e) {
            e.printStackTrace();
            ethWalletCallBack.onErrorCallBack(e);
            return;
        } catch (ValidationException e) {
            e.printStackTrace();
            ethWalletCallBack.onErrorCallBack(e);
            return;
        }
        System.out.println("-----------------------------------------------");
        System.out.println(" ecKeyPair1.getPublicKey()=" + decrypt.getPublicKey());
        System.out.println("ecKeyPair1.getPrivateKey()=" + decrypt.getPrivateKey());
        System.out.println("ecKeyPair1.getAddress()=" + decrypt.getAddress());

        System.out.println("-----------------------------------------------");

        deleteKeystoreFile(decrypt.getAddress());
        // 通过  com.quincysx.crypto.ECKeyPair 的 私钥 byte[]       作为 web3j原生的ECKeyPair  PrivateKey生成 钥匙对
        ECKeyPair ecKeyPair = ECKeyPair.create(decrypt.getRawPrivateKey());
        //16进制打印
        String prk = ecKeyPair.getPrivateKey().toString(16);
        String puk = ecKeyPair.getPublicKey().toString(16);
        System.out.println("password:" + password);
        System.out.println("ecKeyPair----Pr=" + ecKeyPair.getPrivateKey());
        System.out.println("16进制打印的私钥=" + prk);
        Logger.d(this, "--------------------------------------------------------------------------");
        System.out.println("ecKeyPair----Pu=" + ecKeyPair.getPublicKey());
        System.out.println("16进制打印的公钥=" + puk);
        System.out.println("file:" + directory);


        File fileDirectory = new File(directory.getAbsolutePath());
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        //通过web3j 原生 的 方法生成 wallet
        String filename = WalletUtils.generateWalletFile(password, ecKeyPair, fileDirectory, false);
        String path = fileDirectory.getAbsolutePath() + "/" + filename;

        File filePath = new File(path);

        EthWallet wallet = new EthWallet(filePath, EthWalletService.getInstance(), decrypt, walletName, password, walletPasswordHit, true);
        String storeText = StrUtil.getTxtString(filePath);
        List<Wallet> wallets = WalletDaoMaster.queryAllWallet();

        //sp中存有旧钱包
        for (int i = 0; i < wallets.size(); i++) {
            Wallet wallet1 = wallets.get(i);
            if (wallet1.getAddress().contains(decrypt.getAddress())) {
                wallet1.setWalletName(walletName);
                wallet1.setWalletPassword(password);
                wallet1.setWalletPasswordHit(walletPasswordHit);
                wallet1.setIsBackUp(false);
                wallet1.setAddress("0x" + wallet.getAddress());
                wallet1.setPrivateKey("" + ecKeyPair.getPrivateKey());
                wallet1.setPublicKey("" + ecKeyPair.getPublicKey());
                wallet1.setKeyStoreFilePath(fileDirectory + "/" + filename);
                wallet1.setKeyStore(storeText);
                WalletDaoMaster.insertWallet(wallet1);
                ethWalletCallBack.onSuccessCallBack(wallet, filePath.getName(), wallet.getAddress(), storeText);

                return;
            }
        }

        //sp中没有旧钱包
        Wallet wallet1 = new Wallet();

        wallet1.setWalletName(walletName);
        wallet1.setWalletPassword(password);
        wallet1.setWalletPasswordHit(walletPasswordHit);
        wallet1.setIsBackUp(true);
        wallet1.setAddress("0x" + wallet.getAddress());
        wallet1.setPrivateKey("" + ecKeyPair.getPrivateKey());
        wallet1.setPublicKey("" + ecKeyPair.getPublicKey());
        wallet1.setKeyStoreFilePath(fileDirectory + "/" + filename);
        wallet1.setMnemonicWords("");
        wallet1.setKeyStore(storeText);
        WalletDaoMaster.insertWallet(wallet1);
        ethWalletCallBack.onSuccessCallBack(wallet, filePath.getName(), wallet.getAddress(), storeText);

    }

    /**
     * 助记词
     *
     * @param mnemonicWordsInAList
     * @param password
     * @return
     * @throws ValidationException
     */
    public ECKeyPair generateECKeyPairByMnemonicWords(List<String> mnemonicWordsInAList, String password) throws ValidationException {
        //助记词种子 byte
        byte[] seed = getSeed(mnemonicWordsInAList);
        Logger.d(this, "==========开始============");
        ExtendedKey extendedKey = ExtendedKey.create(seed);
        AddressIndex address = BIP44.m().purpose44().coinType(CoinTypes.Ethereum).account(0).external().address(0);
        CoinPairDerive coinKeyPair = new CoinPairDerive(extendedKey);
        com.quincysx.crypto.ECKeyPair master = coinKeyPair.derive(address);
        System.out.println("-----------------------------------------------");
        System.out.println(" ecKeyPair1.getPublicKey()=" + master.getPublicKey());
        System.out.println("ecKeyPair1.getPrivateKey()=" + master.getPrivateKey());
        System.out.println("ecKeyPair1.getAddress()=" + master.getAddress());
        System.out.println("-----------------------------------------------");
        // 通过  com.quincysx.crypto.ECKeyPair 的 私钥 byte[]       作为 web3j原生的ECKeyPair  PrivateKey生成 钥匙对
        ECKeyPair ecKeyPair = ECKeyPair.create(master.getRawPrivateKey());

        return ecKeyPair;
    }

    //m/44/60/0/0 算法
    //生成助记词的种子  不带密码
    public byte[] getSeed(List<String> mnemonicWordsInAList) {
        return new SeedCalculator().withWordsFromWordList(English.INSTANCE).calculateSeed(mnemonicWordsInAList, "");
    }

    //生成助记词的种子  带密码
    public byte[] getSeed(List<String> mnemonicWordsInAList, String password) {
        return new SeedCalculator().withWordsFromWordList(English.INSTANCE).calculateSeed(mnemonicWordsInAList, password);
    }


    /**
     * keystore
     *
     * @param content
     * @param password
     * @return
     * @throws IOException
     * @throws CipherException
     * @throws ValidationException
     */
    public ECKeyPair generateECKeyPairByKeyStore(String content, String password) throws IOException, CipherException, ValidationException, org.web3j.crypto.CipherException {
        System.out.println("-------------------keyStore---------------------");
        System.out.println("keyStore = " + content);
        System.out.println("---------------------keyStoreFile--------------------------");

        ECKeyPair ecKeyPair = null;
        try {
            ecKeyPair = EthWalletUtils.getEckeyPair(content, password);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        System.out.println("----------------------decrypt-------------------------");
        System.out.println(" ecKeyPair1.getPublicKey()=" + ecKeyPair.getPublicKey());
        System.out.println("ecKeyPair1.getPrivateKey()=" + ecKeyPair.getPrivateKey());
        System.out.println("-----------------------------------------------");
        Logger.d(this, "解析成功 获得 ");
        return ecKeyPair;
    }

    /**
     * private key
     *
     * @param content
     * @param password
     * @return
     */
    public ECKeyPair generateECKeyPairByPK(String content, String password) {

        BigInteger bigInteger = new BigInteger(content, 16);
        // 通过  com.quincysx.crypto.ECKeyPair 的 私钥 byte[]       作为 web3j原生的ECKeyPair  PrivateKey生成 钥匙对
        ECKeyPair ecKeyPair = ECKeyPair.create(bigInteger);

        return ecKeyPair;
    }

    /**
     * 助记词导入
     *
     * @param mnemonicWords 助记词,12个单词,空格隔开
     **/
    public void importWallet(ECKeyPair ecKeyPair, String walletName, String password, String walletPasswordHit, String mnemonicWords, final EthWalletCallBack ethWalletCallBack) throws Exception {

        deleteKeystoreFile(Keys.getAddress(ecKeyPair));
        //16进制打印
        String prk = ecKeyPair.getPrivateKey().toString(16);
        String puk = ecKeyPair.getPublicKey().toString(16);
        System.out.println("password:" + password);
        System.out.println("ecKeyPair----Pr=" + ecKeyPair.getPrivateKey());
        System.out.println("16进制打印的私钥=" + prk);
        Logger.d(this, "--------------------------------------------------------------------------");
        System.out.println("ecKeyPair----Pu=" + ecKeyPair.getPublicKey());
        System.out.println("16进制打印的公钥=" + puk);
        System.out.println("file:" + directory);


        File fileDirectory = new File(directory.getAbsolutePath());
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        //通过web3j 原生 的 方法生成 wallet
        String filename = WalletUtils.generateWalletFile(password, ecKeyPair, fileDirectory, false);
        String path = fileDirectory.getAbsolutePath() + "/" + filename;

        File filePath = new File(path);

        EthWallet wallet = new EthWallet(filePath, EthWalletService.getInstance(), ecKeyPair, walletName, password, walletPasswordHit, true);
        String storeText = StrUtil.getTxtString(filePath);

        List<Wallet> wallets = WalletDaoMaster.queryAllWallet();

        //sp中存有旧钱包
        for (int i = 0; i < wallets.size(); i++) {
            Wallet wallet1 = wallets.get(i);
            if (wallet1.getAddress().contains(Keys.getAddress(ecKeyPair))) {
                wallet1.setWalletPassword(password);
                wallet1.setWalletPasswordHit(walletPasswordHit);
                wallet1.setIsBackUp(true);
                wallet1.setKeyStoreFilePath(path);
                if (!StrUtil.isNullOrEmpty(mnemonicWords)) {
                    Logger.d("ccm", "import mnemonicWords:" + mnemonicWords);
                    wallet1.setMnemonicWords(mnemonicWords);
                }
                WalletDaoMaster.insertWallet(wallet1);
                ethWalletCallBack.onSuccessCallBack(wallet, filePath.getName(), wallet.getAddress(), storeText);
                return;
            }
        }

        //sp中没有旧钱包
        Wallet wallet1 = new Wallet();
        wallet1.setWalletName(walletName);
        wallet1.setWalletPassword(password);
        wallet1.setWalletPasswordHit(walletPasswordHit);
        wallet1.setIsBackUp(true);
        wallet1.setAddress("0x" + wallet.getAddress());
        wallet1.setPrivateKey("" + ecKeyPair.getPrivateKey());
        wallet1.setPublicKey("" + ecKeyPair.getPublicKey());
        wallet1.setKeyStoreFilePath(fileDirectory + "/" + filename);
        wallet1.setMnemonicWords(mnemonicWords);
        wallet1.setKeyStore(storeText);
        WalletDaoMaster.insertWallet(wallet1);

        ethWalletCallBack.onSuccessCallBack(wallet, filePath.getName(), wallet.getAddress(), storeText);

    }


    /**
     * 重命名钱包
     *
     * @param address
     * @param newName
     */
    public void renameWallet(String address, String newName) {
        List<Wallet> wallets = WalletDaoMaster.queryAllWallet();
        for (int i = 0; i < wallets.size(); i++) {
            Wallet wallet = wallets.get(i);
            if (wallet.getAddress().equals(address)) {
                wallet.setWalletName(newName);
                WalletDaoMaster.insertWallet(wallet);
                return;
            }
        }
    }

    public void modifyWalletPassword(String address, String pwd) {
        Logger.d(this, "密码:" + pwd + "  钱包地址:" + address);
        Wallet wallet = WalletDaoMaster.queryWalletByAddress(address);
        String pk = new BigInteger(wallet.getPrivateKey()).toString(16);
        ECKeyPair ecKeyPair = generateECKeyPairByPK(pk, wallet.getWalletPassword());
        //通过web3j 原生 的 方法生成 wallet
        File file = new File(wallet.getKeyStoreFilePath());
        File fileDirectory = file.getParentFile();
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        try {
            deleteKeystoreFile(wallet);
//            deleteKeystoreFile(wallet.getAddress().replaceFirst("0x", ""));
            String filename = WalletUtils.generateWalletFile(pwd, ecKeyPair, fileDirectory, false);
            wallet.setWalletPassword(pwd);
            wallet.setKeyStoreFilePath(fileDirectory + "/" + filename);
            WalletDaoMaster.insertWallet(wallet);


            Logger.d(this, "当前钱包信息" + WalletDaoMaster.queryWalletByAddress(address).toString());

        } catch (org.web3j.crypto.CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除钱包
     **/
    public void deleteWallet(Wallet myEthWallet, EthWalletCallBack ethWalletCallBack) throws Exception {
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    deleteKeystoreFile(myEthWallet.getAddress());
                    WalletDaoMaster.deleteWallet(myEthWallet);
                    ethWalletCallBack.onSuccessCallBack(null, null, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    ethWalletCallBack.onErrorCallBack(e);
                }
            }
        });

    }


    public void deleteKeystoreFile(Wallet wallet) {
        Logger.d(this, "filePath:" + wallet.getKeyStoreFilePath());
        File fileDirectory = new File(wallet.getKeyStoreFilePath());
        if (!fileDirectory.exists()) {
            fileDirectory.delete();
        }
    }

    public void deleteKeystoreFile(String address) {
        Wallet wallet = WalletDaoMaster.queryWalletByAddress(address);
        if (null == wallet) {
            return;
        }
        Logger.d(this, "filePath:" + wallet.getKeyStoreFilePath());
        File fileDirectory = new File(wallet.getKeyStoreFilePath());
        if (!fileDirectory.exists()) {
            fileDirectory.delete();
        }
    }

    public void finish() {
        if (!fixedThreadPool.isShutdown()) {
            fixedThreadPool.shutdown();
        }
    }

}
