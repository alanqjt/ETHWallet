package com.alan.wallet.adapter;

import android.support.annotation.Nullable;

import com.alan.wallet.R;
import com.alan.wallet.bean.Wallet;
import com.alan.wallet.utils.Logger;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Alan on 2018/4/25.
 */
public class WalletManagmentAdapter extends BaseQuickAdapter<Wallet, BaseViewHolder> {

    public WalletManagmentAdapter(int layoutResId, @Nullable List<Wallet> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Wallet ethWallet) {
        String address = ethWallet.getAddress();
        if (ethWallet.getAddress().length() > 18) {
            String head = ethWallet.getAddress().substring(0, 8);
            String end = ethWallet.getAddress().substring(ethWallet.getAddress().length() - 8, ethWallet.getAddress().length());
            address = head + "..." + end;
        }
        helper.setText(R.id.text_WalletName, ethWallet.getWalletName());
        helper.setText(R.id.text_WalletAddress, address);

        Logger.d(this, ethWallet.toString());

    }

}
