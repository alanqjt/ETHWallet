package com.alan.btctest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

public class MainActivity extends AppCompatActivity {

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 12ge 英文单词
        //随机生成助记词
        StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, sb::append);
        System.out.println(sb.toString());
        String[] parts = sb.toString().split(" ");

        //-------------------------------------------------




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

        try {
          BtcWallet btcWallet =   BtcUtils.createBTCWalletFromWords(mnemonicWordsInAList,0);

          text.setText(btcWallet.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
