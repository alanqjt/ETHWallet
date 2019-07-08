package com.alan.wallet.ethwallet;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

public class EthWalletService {
    private static final EthWalletService ourInstance = new EthWalletService();

    public static EthWalletService getInstance() {
        return ourInstance;
    }

    private static final String ETHEREUM_NODE_URL = "http://jahwa.fr:49405";

    private Web3j web3;

    private EthWalletService() {
        this.web3 = Web3jFactory.build(new HttpService(ETHEREUM_NODE_URL));
    }

    public BigInteger getBalanceAddress(String address) throws Exception {
            EthGetBalance ethGetBalance = web3
                    .ethGetBalance( address, DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();

            return ethGetBalance.getBalance();
    }
}
