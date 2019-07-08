package com.alan.btctest;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.math.BigInteger;
import java.util.List;

import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.wordlists.English;

import static org.bitcoinj.crypto.HDUtils.parsePath;

public class BtcUtils {


    public static BtcWallet createBTCWalletFromWords(List<String> mnemonicWordsInAList) throws Exception {
        BtcWallet btcWallet = new BtcWallet();
        //助记词种子 byte
        byte[] seed = getSeed(mnemonicWordsInAList);

        DeterministicSeed deterministicSeed = new DeterministicSeed(mnemonicWordsInAList, seed, "", 0);


        DeterministicKeyChain deterministicKeyChain = DeterministicKeyChain.builder().seed(deterministicSeed).build();
        //这里运用了BIP44里面提到的算法, 44'是固定的, 后面的一个0'代表的是币种BTC


        //EIP85  提议 以太坊 路径为 :  m/44'/60'/a'/0/n
        //
        //这里 的 a 表示帐号，n 是第 n 生成的地址，60 是在 SLIP44 提案中暂定的，因为 BIP44 只定义到 0 - 31。


        for (int i = 0; i < 1; i++) {
            byte[] privKeyBTC = deterministicKeyChain.getKeyByPath(parsePath("M/44/0/0/0/" + i), true).getPrivKeyBytes();

            // 比特币创建的钱包 与连接的网络 有关  如果 选 TestNet   生成的地址一般是以 N \M 开头   MainNet 生成的地址是以 1  \ 3  开头
            ECKey ecKey = ECKey.fromPrivate(privKeyBTC);
            String publickey = Numeric.toHexStringNoPrefixZeroPadded(new BigInteger(ecKey.getPubKey()), 66);
            String privateKey = ecKey.getPrivateKeyEncoded(MainNetParams.get()).toString();
            System.out.println(i + " ----------------主钱包的 主公私钥 和地址---------------------------");
            System.out.println(i + "主 address:" + ecKey.toAddress(MainNetParams.get()).toBase58());
            System.out.println(i + "测 address:" + ecKey.toAddress(TestNet3Params.get()).toBase58());
            System.out.println(i + " publickey:" + publickey.length());
            System.out.println(i + " publickey:" + publickey);
            System.out.println(i + " privateKey:" + privateKey.length());
            System.out.println(i + " privateKey:" + privateKey);

            System.out.println("head=" + MainNetParams.get().getAddressHeader());

            String msg = "600FFE422B4E00731A59557A5CCA46CC183944191006324A447BDB2D98D4B408";
            String msg2 = "010966776006953D5567439E5E39F86A0D273BEE";

            System.out.println(i + " msg1:" + msg.length());
            System.out.println(i + " msg2:" + msg2.length());
            System.out.println(i + " msg:" + ecKey.getPublicKeyAsHex().length());
            System.out.println(i + " msg:" + ecKey.getPublicKeyAsHex());
            System.out.println(i + " msg:" + ecKey.getPubKeyHash().length);
            System.out.println(i + " msg:" + ecKey.getPubKey().length);


            System.out.println("-----------------通过公钥获取地址--------------------------");

            String testNetAddress = getAddress(publickey, true, false);

            System.out.println("-----------------Testnet pub key   n/m开头--------------------------");

            String mainNetAddress_Standard_Public = getAddress(publickey, false, false);
            System.out.println("-----------------P2PKH address    1开头--------------------------");
            String mainNetAddress_Multi_Signature = getAddress(publickey, false, true);
            System.out.println("-----------------P2SH address     3开头--------------------------");

            btcWallet.setAddress_M(mainNetAddress_Standard_Public);
            btcWallet.setAddress_S(mainNetAddress_Multi_Signature);
            btcWallet.setAddress_T(testNetAddress);

            btcWallet.setPublickey(publickey);
            btcWallet.setPrivateKey(privateKey);
            btcWallet.setMnemonicWords(mnemonicWordsInAList);


            System.out.println(btcWallet.toString());


        }






        return btcWallet;
    }
//Multi-Signature


    /**
     * @param PKY       公钥
     * @param isTestNet 主网 还是测试网
     * @param isP2SHAddress  P2SH address
     **/
    public static String getAddress(String PKY, boolean isTestNet, Boolean isP2SHAddress) throws Exception {
        byte[] publicKey = new BigInteger(PKY, 16).toByteArray();
        byte[] sha256Bytes = Utils.sha256(publicKey);
        System.out.println("sha256加密=" + Utils.bytesToHexString(sha256Bytes));
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(sha256Bytes, 0, sha256Bytes.length);
        byte[] ripemd160Bytes = new byte[digest.getDigestSize()];
        digest.doFinal(ripemd160Bytes, 0);
        System.out.println("ripemd160加密=" + Utils.bytesToHexString(ripemd160Bytes));

        String val = "";

        if (isTestNet) {
            //测试网络 n 、m开头的地址 十六进制
            val = "6F";
        } else if (isP2SHAddress) {
            //主网  3开头的地址 十六进制
            val = "05";
        } else {
            //主网 1开头得到地址 十六进制
            val = "00";
        }
        byte[] networkID = new BigInteger(val, 16).toByteArray();
        byte[] extendedRipemd160Bytes = Utils.add(networkID, ripemd160Bytes);

        System.out.println("添加NetworkID=" + Utils.bytesToHexString(extendedRipemd160Bytes));

        byte[] twiceSha256Bytes = Utils.sha256(Utils.sha256(extendedRipemd160Bytes));

        System.out.println("两次sha256加密=" + Utils.bytesToHexString(twiceSha256Bytes));

        byte[] checksum = new byte[4];
        System.arraycopy(twiceSha256Bytes, 0, checksum, 0, 4);

        System.out.println("checksum=" + Utils.bytesToHexString(checksum));

        byte[] binaryBitcoinAddressBytes = Utils.add(extendedRipemd160Bytes, checksum);

        System.out.println("添加checksum之后=" + Utils.bytesToHexString(binaryBitcoinAddressBytes));

        String bitcoinAddress = Base58.encode(binaryBitcoinAddressBytes);
        System.out.println("bitcoinAddress=" + bitcoinAddress);

        return bitcoinAddress;
        //生成的地址是否有效 查询地址 ：http://lenschulwitz.com/base58

        //0主 address:1TFFEVsGK8wEv6e77MHo9cnAdhs6tKfEU
        //0主 address:329GAmzJpDTKL5o5ED1tDmyiK9zahMcFFF
        //0测 address:mfyCYHar5LaC22aFpgKfd4q72dJa5sSaG3


    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    //m/44/60/0/0 算法
    //生成助记词的种子  不带密码
    public static byte[] getSeed(List<String> mnemonicWordsInAList) {
        return new SeedCalculator().withWordsFromWordList(English.INSTANCE).calculateSeed(mnemonicWordsInAList, "");
    }

}
