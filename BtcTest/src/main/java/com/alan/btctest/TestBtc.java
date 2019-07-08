package com.alan.btctest;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

/**
 * Created by Alan on 2018/7/16.
 */
public class TestBtc {

    public static void main(String[] args) {

        // 12ge 英文单词
        //随机生成助记词
        StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, sb::append);
        System.out.println(sb.toString());
        String[] parts = sb.toString().split(" ");
//------------------------------------------------------------------------------------------------
        //助记词 list
        List<String> mnemonicWordsInAList = new ArrayList<>();
        mnemonicWordsInAList.add("wolf");
        mnemonicWordsInAList.add("kangaroo");
        mnemonicWordsInAList.add("spirit");
        mnemonicWordsInAList.add("invest");

        mnemonicWordsInAList.add("small");
        mnemonicWordsInAList.add("stadium");
        mnemonicWordsInAList.add("drill");
        mnemonicWordsInAList.add("myself");

        mnemonicWordsInAList.add("old");
        mnemonicWordsInAList.add("escape");
        mnemonicWordsInAList.add("nasty");
        mnemonicWordsInAList.add("chef");
//        mnemonicWordsInAList.clear();
        for (int i = 0; i < parts.length; i++) {
            System.out.println(parts[i]);
//            mnemonicWordsInAList.add(parts[i]);
        }
        try {
            BtcUtils.createBTCWalletFromWords(mnemonicWordsInAList);
            System.out.println("生成种子的助记词 不一定是英文单词，也可以是 French，Japanese，Spanish。理论上任何语言都可以所以肯定是支持中文的助记词的。自己动手在bip39库增加一个就行 com.quincysx.crypto.bip39.wordlists");
            System.out.println("生成的地址是否有效 查询地址 ：http://lenschulwitz.com/base58");
            System.out.println("bitcoin地址是如何生成的： https://www.jianshu.com/p/954e143e97d2");
            System.out.println("地址意义说明 ：https://learnku.com/articles/5087/bitcoin-test-chain-testnet");
            System.out.println("获取测试比特币(国内记得科学上网才能获取到)：https://testnet.manu.backend.hamburg/faucet");
            System.out.println("查询测试比特币余额：https://www.blocktrail.com/tBTC     https://btc.com/");
            System.out.println("无论是比特币还是以太坊的钱包生成，使用的算法大同小异，不过以太坊的钱包比 比特币的钱包多了一个keystore。");
            System.out.println("注意的是比特币具有匿名特性。以及隔离见证");
//            testAddress(mnemonicWordsInAList);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
