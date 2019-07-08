package com.alan.wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.alan.wallet.R;
import com.alan.wallet.bean.Wallet;
import com.alan.wallet.ethwallet.EthWallet;
import com.alan.wallet.ethwallet.EthWalletCallBack;
import com.alan.wallet.ethwallet.EthWalletManager;
import com.alan.wallet.greendao.WalletDaoMaster;
import com.alan.wallet.utils.DialogV7Utils;
import com.alan.wallet.utils.Logger;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class BaseImportFragment extends Fragment {
    protected Button mBtn_Import;

    /**
     * loading动画,唯一
     */
    protected KProgressHUD hud;
    protected EthWalletManager wManager;

    /**
     * 调用摄像头,获取扫码结果
     *
     * @param result
     */
    public abstract void excuteByScanResult(String result);

    interface OnCheckECKeyPairListener {
        ECKeyPair checkECKeyPairMethod();

        String alertWrongTip();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wManager = new EthWalletManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wManager != null) {
            wManager.finish();
        }
    }

    /**
     * 在主线程调用
     * 显示loading动画
     */
    protected void showHud() {
        if (hud == null) {
            hud = KProgressHUD.create(getActivity());
            hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(getResources().getString(R.string.please_wait))
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
        }
    }

    /**
     * 在主线程调用
     * 结束loading动画
     */
    protected void dismissHud() {
        if (hud != null) {
            hud.dismiss();
            hud = null;
        }
    }


    /**
     * 这是兼容的 AlertDialog
     * 只有取消
     */
    protected void showCancelDialog(String message) {
        DialogV7Utils.showCancelDialog(getActivity(), message, getResources().getString(R.string.ok));
    }

    /**
     * 这是兼容的 AlertDialog
     * 有确定和取消
     */
    protected void showNormalDialog(String message, DialogV7Utils.OnNormalDialogSureListener listener) {

        DialogV7Utils.showNormalDialog(getActivity(), message, listener);
    }

    protected void showEditTextDialog(boolean isShowSubtitle, String message1, String message2, DialogV7Utils.OnEditTextDialogSureListener listener) {
        DialogV7Utils.showEditTextDialog(getActivity(), message1, message2, listener);
    }

    public void MyToast(String message) {
        Toast toast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
        toast.setText(message);
        toast.show();
        toast = null;
    }


    protected void rxImportStep1(String Password1, String PasswordHit, String mnemonicWords, OnCheckECKeyPairListener listener) {
        getRxCheckECKeyPairObservable(listener)
                .observeOn(Schedulers.io())
                .map(new Func1<ECKeyPair, Map<ECKeyPair, Wallet>>() {
                    @Override
                    public Map<ECKeyPair, Wallet> call(ECKeyPair ecKeyPair) {
                        Map<ECKeyPair, Wallet> map = new HashMap<>();
                        Wallet myEthWallet = null;
                        try {

                            myEthWallet = WalletDaoMaster.queryWalletByAddress("0x" + Keys.getAddress(ecKeyPair));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        map.put(ecKeyPair, myEthWallet);
                        return map;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<ECKeyPair, Wallet>>() {
                    @Override
                    public void call(Map<ECKeyPair, Wallet> ecKeyPairBooleanMap) {
                        for (Map.Entry<ECKeyPair, Wallet> entry : ecKeyPairBooleanMap.entrySet()) {
                            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                            ECKeyPair ecKeyPair = entry.getKey();
                            Wallet myEthWallet = entry.getValue();
                            if (myEthWallet == null) {
                                //本地没有将要导入的钱包
                                //直接导入
                                rxImportStep2(ecKeyPair, Keys.getAddress(ecKeyPair).substring(0, 5), Password1, PasswordHit, mnemonicWords);
                            } else {
                                //询问
                                dismissHud();
                                showNormalDialog(getResources().getString(R.string.repeat_import_dialog_tip), new DialogV7Utils.OnNormalDialogSureListener() {
                                    @Override
                                    public void sureCallBack() {
                                        showHud();
                                        //重新设置本地钱包
                                        rxImportStep2(ecKeyPair, myEthWallet.getWalletName(), Password1, PasswordHit, mnemonicWords);
                                    }

                                    @Override
                                    public void cancelCallBack() {

                                    }
                                });
                            }
                        }
                    }
                });

    }

    protected void rxImportStep1(String Password1, String PasswordHit, OnCheckECKeyPairListener listener) {
        rxImportStep1(Password1, PasswordHit, "", listener);
    }


    private Observable<ECKeyPair> getRxCheckECKeyPairObservable(OnCheckECKeyPairListener listener) {
        return Observable.just(null)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        Log.e("ccm", "observable1");
                        showHud();
                        return null;
                    }
                })
                .observeOn(Schedulers.computation())
                .map(new Func1<Object, ECKeyPair>() {
                    @Override
                    public ECKeyPair call(Object o) {
                        Log.e("ccm", "observable2");

                        return listener.checkECKeyPairMethod();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<ECKeyPair, Boolean>() {
                    @Override
                    public Boolean call(ECKeyPair ecKeyPair) {
                        Log.e("ccm", "observable4");
                        if (ecKeyPair == null) {
                            //无效输入
                            dismissHud();
                            showCancelDialog(listener.alertWrongTip());
                            return false;
                        } else {
                            return true;
                        }
                    }
                });

    }

    private void rxImportStep2(ECKeyPair ecKeyPair, String walletName, String Password1, String PasswordHit, String mnemonicWords) {
//        wManager.importWallet();
        Observable.just(null)
                .observeOn(Schedulers.newThread())
                .map(new Func1<Object, Boolean>() {

                    boolean isSuccess = false;

                    @Override
                    public Boolean call(Object o) {

                        try {
                            //方法本身和回调都在同一线程中
                            wManager.importWallet(ecKeyPair, walletName, Password1, PasswordHit, mnemonicWords, new EthWalletCallBack() {
                                @Override
                                public void onSuccessCallBack(EthWallet ethWallet, String fileName, String walletAddress, String storeText) throws Exception {
                                    isSuccess = true;
                                    Logger.d("importWallet", "导入钱包成功:" + ethWallet.toString());
                                    Logger.d("importWallet", "fileName:" + fileName);
                                    Logger.d("importWallet", "walletAddress:" + walletAddress);
                                    Logger.d("importWallet", "storeText:" + storeText);
                                }

                                @Override
                                public void onErrorCallBack(Exception e) {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return isSuccess;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        dismissHud();
                        getActivity().finish();
                    }
                });

    }

}
