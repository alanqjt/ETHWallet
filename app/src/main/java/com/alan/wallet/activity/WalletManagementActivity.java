package com.alan.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.alan.wallet.R;
import com.alan.wallet.adapter.WalletManagmentAdapter;
import com.alan.wallet.bean.Wallet;
import com.alan.wallet.greendao.WalletDaoMaster;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alan on 2018/5/2.
 * 管理钱包
 */
public class WalletManagementActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView_WalletManagement)
    RecyclerView recyclerViewWalletManagement;
    @BindView(R.id.btn_CreatWallet)
    LinearLayout btnCreatWallet;
    @BindView(R.id.btn_ImportWallet)
    LinearLayout btnImportWallet;

    WalletManagmentAdapter walletManagmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_wallet_management);
        ButterKnife.bind(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewWalletManagement.setLayoutManager(llm);
        walletManagmentAdapter = new WalletManagmentAdapter(R.layout.item_list_wallet_managment, null);

        recyclerViewWalletManagement.setAdapter(walletManagmentAdapter);

        walletManagmentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Wallet wallet = (Wallet) adapter.getData().get(position);

                Intent intent = new Intent();
                intent.setClass(WalletManagementActivity.this, WalletDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("address", wallet.getAddress());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Wallet> walletList = WalletDaoMaster.queryAllWallet();

        if (walletList.size() > 0) {
            walletManagmentAdapter.setNewData(walletList);

        } else {
            walletManagmentAdapter.setNewData(null);
        }
        walletManagmentAdapter.notifyDataSetChanged();

    }


    @OnClick({R.id.btn_CreatWallet, R.id.btn_ImportWallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_CreatWallet:
                startActivity(new Intent(WalletManagementActivity.this, CreateWalletActivity.class));
                break;
            case R.id.btn_ImportWallet:
                startActivity(new Intent(WalletManagementActivity.this, ImportWalletActivity.class));
                break;
        }
    }
}
