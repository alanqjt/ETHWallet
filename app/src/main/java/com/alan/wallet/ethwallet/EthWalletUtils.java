package com.alan.wallet.ethwallet;

import android.content.Context;

import com.alan.wallet.application.MyApplication;
import com.alan.wallet.greendao.WalletDaoMaster;
import com.alan.wallet.utils.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaworks.crypto.SCrypt;
import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.ethereum.keystore.KeyStore;
import com.quincysx.crypto.ethereum.keystore.KeyStoreFile;

import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Alan on 2018/5/30.
 */
public class EthWalletUtils {
    private static Web3j web3j;


    private static Admin admin;

    private final static String fromAddress = "0x657BeC1b9c9220b8CeDB69457C24309b4A070464";
//    private static String fromAddress = "0xb7f08bd309aed717095947ce14227b5e66d628d1";

    private static String contractAddress = "0xf735c949420B03fBA7e44587c7972D9b89114b7A";

    private static String emptyAddress = "0x0000000000000000000000000000000000000000";


    private static String path = "./app/src/main/assets/";
    private static String filename = "UTC--2018-03-21T01-32-42.340664500Z--657bec1b9c9220b8cedb69457c24309b4a070464";


    static String content = "{\"address\":\"81358242cf0731acb535b5f28a4d19eef0c9f4ed\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"2df65e404fff55e61f28595af61d78543137c5cd44ab6c91f984dc1de1baa900\",\"cipherparams\":{\"iv\":\"5a09063f438c778efe347599536c1787\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"1a595ce9d57c6ed0f8fcb94d6cf43afa8d4dbcb5977ed56f980c793c5fdbd2d9\"},\"mac\":\"8ab6ab64e6f424b5afe36f7fbefc25bad44c53ade2df257b5fb9058ea33c6e13\"},\"id\":\"a93e984d-edb9-4e1f-a893-79e43c85ca75\",\"version\":3}";

    static String password = "12345678";

    public static void main(String[] args) throws IOException, com.quincysx.crypto.ethereum.keystore.CipherException, ValidationException, CipherException, GeneralSecurityException {
//        web3j = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/XLN13NCI3kUmHdTJ0XDv"));
//        web3j = Web3jFactory.build(new HttpService("http://192.168.0.103:8545"));
//        admin = AdminFactory.build(new HttpService("http://192.168.0.103:8545"));
        ;
//        System.out.println("balance:"+ getTokenBalance(web3j, fromAddress, contractAddress));
//        System.out.println(getTokenName(web3j, contractAddress));
//        System.out.println(getTokenDecimals(web3j, contractAddress));
//        System.out.println(getTokenSymbol(web3j, contractAddress));
//        System.out.println(getTokenTotalSupply(web3j, contractAddress));


//        String    privatekey=  "92191745551477161058672620473680583076675289959770297787091187057887404613229";
//        BigInteger bigInteger =  new BigInteger(privatekey);
//        System.out.println("privatekey: "+bigInteger.toString(16));
        System.out.println("------------------------原本的方法-------------------------------");
        long startNor = System.currentTimeMillis();
        System.out.println("开始:" + startNor);

        KeyStoreFile keyStoreFile = null;
        keyStoreFile = KeyStoreFile.parse(content);
        com.quincysx.crypto.ECKeyPair master = null;

        master = KeyStore.decrypt(password, keyStoreFile);

        System.out.println("credentials  getPublicKey=" + master.getPublicKey());
        System.out.println("credentials  getPrivateKey=" + master.getPrivateKey());
        long endNor = System.currentTimeMillis();
        System.out.println("结束:" + endNor);
        System.out.println("用时:" + (endNor - startNor));
        System.out.println("-------------------------------------------------------");

        System.out.println("------------------------新的方法-------------------------------");
        long startNew = System.currentTimeMillis();
        System.out.println("开始:" + startNew);
        WalletFile walletFile = new ObjectMapper().readValue(content, WalletFile.class);

        ECKeyPair ecKeyPair = Wallet.decrypt(password, walletFile);

        WalletFile.Crypto crypto = walletFile.getCrypto();

        byte[] mac = Numeric.hexStringToByteArray(crypto.getMac());
        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
        byte[] cipherText = Numeric.hexStringToByteArray(crypto.getCiphertext());

        byte[] derivedKey;

        if (crypto.getKdfparams() instanceof WalletFile.ScryptKdfParams) {
            System.out.println("----------进入加密1------------");
            WalletFile.ScryptKdfParams scryptKdfParams =
                    (WalletFile.ScryptKdfParams) crypto.getKdfparams();
            int dklen = scryptKdfParams.getDklen();
            int n = scryptKdfParams.getN();
            int p = scryptKdfParams.getP();
            int r = scryptKdfParams.getR();
            byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());

            derivedKey = SCrypt.scrypt(password.getBytes(Charset.forName("UTF-8")), salt, n, r, p, dklen);
        } else if (crypto.getKdfparams() instanceof WalletFile.Aes128CtrKdfParams) {
            System.out.println("----------进入加密2------------");
            WalletFile.Aes128CtrKdfParams aes128CtrKdfParams =
                    (WalletFile.Aes128CtrKdfParams) crypto.getKdfparams();
            int c = aes128CtrKdfParams.getC();
            String prf = aes128CtrKdfParams.getPrf();
            byte[] salt = Numeric.hexStringToByteArray(aes128CtrKdfParams.getSalt());

            derivedKey = generateAes128CtrDerivedKey(password.getBytes(Charset.forName("UTF-8")), salt, c, prf);
        } else {
            throw new CipherException("Unable to deserialize params: " + crypto.getKdf());
        }

        byte[] derivedMac = generateMac(derivedKey, cipherText);

        if (!Arrays.equals(derivedMac, mac)) {
            throw new CipherException("Invalid password provided");
        }

        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
        byte[] privateKey = performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKey, cipherText);
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        long endNew = System.currentTimeMillis();
        System.out.println("结束:" + endNew);
        System.out.println("用时:" + (endNew - startNew));
        System.out.println("-------------改了加密-------------------");
        System.out.println("keyPair  publickey= " + keyPair.getPublicKey().toString(16));
        System.out.println("keyPair  publickey= " + keyPair.getPrivateKey().toString(16));
        System.out.println("-------------改了加密-------------------");

    }


    public static ECKeyPair getEckeyPair(String content, String password) throws IOException, GeneralSecurityException, CipherException {
        WalletFile walletFile = new ObjectMapper().readValue(content, WalletFile.class);

//        ECKeyPair ecKeyPair = Wallet.decrypt(password, walletFile);

        WalletFile.Crypto crypto = walletFile.getCrypto();

        byte[] mac = Numeric.hexStringToByteArray(crypto.getMac());
        byte[] iv = Numeric.hexStringToByteArray(crypto.getCipherparams().getIv());
        byte[] cipherText = Numeric.hexStringToByteArray(crypto.getCiphertext());

        byte[] derivedKey;

        if (crypto.getKdfparams() instanceof WalletFile.ScryptKdfParams) {
            System.out.println("----------进入加密1------------");
            WalletFile.ScryptKdfParams scryptKdfParams =
                    (WalletFile.ScryptKdfParams) crypto.getKdfparams();
            int dklen = scryptKdfParams.getDklen();
            int n = scryptKdfParams.getN();
            int p = scryptKdfParams.getP();
            int r = scryptKdfParams.getR();
            byte[] salt = Numeric.hexStringToByteArray(scryptKdfParams.getSalt());

            derivedKey = SCrypt.scrypt(password.getBytes(Charset.forName("UTF-8")), salt, n, r, p, dklen);
        } else if (crypto.getKdfparams() instanceof WalletFile.Aes128CtrKdfParams) {
            System.out.println("----------进入加密2------------");
            WalletFile.Aes128CtrKdfParams aes128CtrKdfParams =
                    (WalletFile.Aes128CtrKdfParams) crypto.getKdfparams();
            int c = aes128CtrKdfParams.getC();
            String prf = aes128CtrKdfParams.getPrf();
            byte[] salt = Numeric.hexStringToByteArray(aes128CtrKdfParams.getSalt());

            derivedKey = generateAes128CtrDerivedKey(password.getBytes(Charset.forName("UTF-8")), salt, c, prf);
        } else {
            throw new CipherException("Unable to deserialize params: " + crypto.getKdf());
        }

        byte[] derivedMac = generateMac(derivedKey, cipherText);

        if (!Arrays.equals(derivedMac, mac)) {
            throw new CipherException("Invalid password provided");
        }

        byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
        byte[] privateKey = performCipherOperation(Cipher.DECRYPT_MODE, iv, encryptKey, cipherText);
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        System.out.println("结束:" + System.currentTimeMillis());

        System.out.println("-------------改了加密-------------------");
        System.out.println("keyPair  publickey= " + keyPair.getPublicKey().toString(16));
        System.out.println("keyPair  publickey= " + keyPair.getPrivateKey().toString(16));
        System.out.println("-------------改了加密-------------------");
        return keyPair;
    }


    private static byte[] performCipherOperation(int mode, byte[] iv, byte[] encryptKey, byte[] text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

        SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
        cipher.init(mode, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(text);
    }

    private static byte[] generateMac(byte[] derivedKey, byte[] cipherText) {
        byte[] result = new byte[16 + cipherText.length];

        System.arraycopy(derivedKey, 16, result, 0, 16);
        System.arraycopy(cipherText, 0, result, 16, cipherText.length);

        return Hash.sha3(result);
    }

    private static byte[] generateAes128CtrDerivedKey(
            byte[] password, byte[] salt, int c, String prf) throws CipherException {

        if (!prf.equals("hmac-sha256")) {
            throw new CipherException("Unsupported prf:" + prf);
        }

        // Java 8 supports this, but you have to convert the password to a character array, see
        // http://stackoverflow.com/a/27928435/3211687

        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
        gen.init(password, salt, c);
        return ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
    }

    public static BigInteger getEthTokenBalance(Web3j web3j, String fromAddress) {

        Logger.d("getEthTokenBalance", "excute");
        EthGetBalance ethGetBalance1 = null;
        try {
            ethGetBalance1 = web3j.ethGetBalance(fromAddress, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ethGetBalance1.getBalance();
    }


    /**
     * 查询代币余额
     */
    public static BigInteger getTokenBalance(Web3j web3j, String fromAddress, String contractAddress) {

        String methodName = "balanceOf";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        Address address = new Address(fromAddress);
        inputParameters.add(address);

        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

        EthCall ethCall;
        BigInteger balanceValue = BigInteger.ZERO;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            if (results.size() > 0) {
                balanceValue = (BigInteger) results.get(0).getValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return balanceValue;
    }

    /**
     * 查询代币名称
     *
     * @param web3j
     * @param contractAddress
     * @return
     */
    public static String getTokenName(Web3j web3j, String contractAddress) {
        String methodName = "name";
        String name = null;
        String fromAddr = emptyAddress;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            name = results.get(0).getValue().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 查询代币符号
     *
     * @param web3j
     * @param contractAddress
     * @return
     */
    public static String getTokenSymbol(Web3j web3j, String contractAddress) {
        String methodName = "symbol";
        String symbol = null;
        String fromAddr = emptyAddress;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            symbol = results.get(0).getValue().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return symbol;
    }

    /**
     * 查询代币精度
     *
     * @param web3j
     * @param contractAddress
     * @return
     */
    public static int getTokenDecimals(Web3j web3j, String contractAddress) {
        String methodName = "decimals";
        String fromAddr = emptyAddress;
        int decimal = 0;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Uint8> typeReference = new TypeReference<Uint8>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            decimal = Integer.parseInt(results.get(0).getValue().toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return decimal;
    }

    /**
     * 查询代币发行总量
     *
     * @param web3j
     * @param contractAddress
     * @return
     */
    public static BigInteger getTokenTotalSupply(Web3j web3j, String contractAddress) {
        String methodName = "totalSupply";
        String fromAddr = emptyAddress;
        BigInteger totalSupply = BigInteger.ZERO;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            totalSupply = (BigInteger) results.get(0).getValue();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return totalSupply;
    }

    /**
     * 代币转账
     */
    public static String sendTokenTransaction(String fromAddress, String password, String toAddress, String contractAddress, BigInteger amount) {
        String txHash = null;

        try {
            PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(fromAddress, password, BigInteger.valueOf(10)).send();
            if (personalUnlockAccount.accountUnlocked()) {
                String methodName = "transfer";
                List<Type> inputParameters = new ArrayList<>();
                List<TypeReference<?>> outputParameters = new ArrayList<>();

                Address tAddress = new Address(toAddress);

                Uint256 value = new Uint256(amount);
                inputParameters.add(tAddress);
                inputParameters.add(value);

                TypeReference<Bool> typeReference = new TypeReference<Bool>() {
                };
                outputParameters.add(typeReference);

                Function function = new Function(methodName, inputParameters, outputParameters);

                String data = FunctionEncoder.encode(function);

//            web3j = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/XLN13NCI3kUmHdTJ0XDv"));
                EthGetTransactionCount ethGetTransactionCount = web3j
                        .ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).sendAsync().get();
                BigInteger nonce = ethGetTransactionCount.getTransactionCount();
                BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(5), Convert.Unit.GWEI).toBigInteger();

                Transaction transaction = Transaction.createFunctionCallTransaction(fromAddress, BigInteger.ONE, gasPrice,
                        BigInteger.valueOf(60000), contractAddress, data);

                System.out.println("web3j:" + web3j.toString());


                EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).sendAsync().get();
                txHash = ethSendTransaction.getTransactionHash();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return txHash;
    }


    /**
     * 计算合约地址
     *
     * @param address
     * @param nonce
     * @return
     */
    private static String calculateContractAddress(String address, long nonce) {
        //样例 https://ropsten.etherscan.io/tx/0x728a95b02beec3de9fb09ede00ca8ca6939bad2ad26c702a8392074dc04844c7
        byte[] addressAsBytes = Numeric.hexStringToByteArray(address);

        byte[] calculatedAddressAsBytes =
                Hash.sha3(RlpEncoder.encode(
                        new RlpList(
                                RlpString.create(addressAsBytes),
                                RlpString.create((nonce)))));

        calculatedAddressAsBytes = Arrays.copyOfRange(calculatedAddressAsBytes,
                12, calculatedAddressAsBytes.length);
        String calculatedAddressAsHex = Numeric.toHexString(calculatedAddressAsBytes);
        return calculatedAddressAsHex;
    }

    /**
     * 转账ETH方法
     **/
    public void transfer(float coin, Context context)
            throws InterruptedException, IOException, TransactionException, Exception {
//        EthWalletManager ethWalletManager = new EthWalletManager(context);
//        String toAddress = ethWallet_one.getAddress();
//        String password = "";
//        String walletfile = "/Users/neo/netkiller/UTC--2018-01-20T04-04-06.786586541Z--83fda0ba7e6cfa8d7319d78fa0e6b753a2bcb5a6";
//        Credentials credentials = WalletUtils.loadCredentials(ethWallet_two.getWalletPassword(), ethWallet_two.getFilePath());
//
//        Logger.d(this, "oneAddress:" + toAddress);
//        Logger.d(this, "twoAddress:" + ethWallet_two.getAddress());
//        TransactionReceipt transactionReceipt = Transfer.sendFunds(
//                MyApplication.getWeb3j(), credentials,
//                toAddress,  // you can put any address here
//                BigDecimal.ONE, Convert.Unit.ETHER)  // 1 wei = 10^-18 Ether
//                .send();
//        System.out.println("转账结果:" + transactionReceipt.getStatus());

    }

    /**
     * 代币转账
     */
    public String sendTokenTransaction(String fromAddress, String toAddress, BigInteger amount, Context context) {
        //代币合约
        String contractAddress = "0xf735c949420B03fBA7e44587c7972D9b89114b7A";
        String password = "Qq123456";
        String txHash = null;

        try {
//            PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(
//                    fromAddress, password, BigInteger.valueOf(10)).send();
//            if (personalUnlockAccount.accountUnlocked()) {
            String methodName = "transfer";
            List<Type> inputParameters = new ArrayList<>();
            List<TypeReference<?>> outputParameters = new ArrayList<>();
            Address tAddress = new Address(toAddress);
//            Address fAddress = new Address(fromAddress);
            Uint256 value = new Uint256(amount);
//            inputParameters.add(fAddress);
            inputParameters.add(tAddress);
            inputParameters.add(value);

            TypeReference<Bool> typeReference = new TypeReference<Bool>() {
            };
            outputParameters.add(typeReference);
            Function function = new Function(methodName, inputParameters, outputParameters);
            String data = FunctionEncoder.encode(function);
            Web3j web3j = MyApplication.getWeb3j();
//            EthGetTransactionCount ethGetTransactionCount = web3j
//                    .ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).sendAsync().get();
//            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(5), Convert.Unit.GWEI).toBigInteger();
            Transaction transaction = Transaction.createFunctionCallTransaction(fromAddress, BigInteger.ONE, gasPrice,
                    BigInteger.valueOf(60000), contractAddress, data);
            System.out.println("web3j:" + web3j.toString());
            EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).sendAsync().get();
            txHash = ethSendTransaction.getTransactionHash();
            Logger.d(this, "交易hash:" + txHash);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return txHash;
    }

}
