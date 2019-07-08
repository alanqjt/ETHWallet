说明
====
此项目基于web3j开发的一个android的ETHWallet,其中包含了助记词生成、创建钱包、导入钱包等基础方法。并且包含了btc钱包的生成过程
使用原生web3j生成ECKeyPair的时候 低端机型(华为P6)会闪退，原因是算法复杂度高，CPU使用过高,java运行的速度慢。解决方法把当需要调用web3j 调用SCrypt.scrypt()方法的时候把java算法换成使用JNI C库中的代码


ScreenShot
==========

部分界面效果
---------
<img src="img/ethWallet1.png" width="360">    <img src="img/ethWallet2.png" width="360"> 

         
---------
<img src="img/ethWallet3.png" width="360">    <img src="img/ethWallet4.png" width="360"> 

         
---------
<img src="img/ethWallet5.png" width="360">    <img src="img/ethWallet6.png" width="360"> 

         
---------
<img src="img/ethWallet7.png" width="360">    <img src="img/ethWallet8.png" width="360"> 



<img src="img/createwallet.gif" width="360">    <img src="img/keystore_import.gif" width="360"> 


<img src="img/privatekey.gif" width="360">    <img src="img/wordsimport.gif" width="360"> 


<img src="img/backup.gif" width="360"> 


使用
===

创建钱包
-------
```Java
        EthWalletManager wManager = new EthWalletManager();
        wManager.createWallet(walletName, password, passwordHit, false, new EthWalletCallBack() {

            @Override
            public void onSuccessCallBack(EthWallet ethWallet, String fileName, String walletAddress, String storeText) throws Exception {

            }

            @Override
            public void onErrorCallBack(Exception e) {

            }
        });
```


导入钱包
-------
```Java
        //三种方法导入
        EthWalletManager wManager = new EthWalletManager();//助记词导入钱包
        ECKeyPair ecKeyPair = wManager.generateECKeyPairByMnemonicWords(mnemonicWordsInAList, password);
        ECKeyPair ecKeyPair = wManager.generateECKeyPairByKeyStore(keystore_content, password) //keystore导入钱包
        ECKeyPair ecKeyPair = wManager.generateECKeyPairByPK(pk_text, password);//私钥导入钱包
        wManager.importWallet(ecKeyPair, walletName, password, passwordHit, mnemonicWords, new EthWalletCallBack() {
            @Override
            public void onSuccessCallBack(EthWallet ethWallet, String fileName, String walletAddress, String storeText) throws Exception {

            }

            @Override
            public void onErrorCallBack(Exception e) {

            }
        });

```









