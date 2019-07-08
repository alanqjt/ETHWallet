package com.alan.wallet.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alan.wallet.R;
import com.alan.wallet.adapter.ExportKeystoreAdapter;
import com.alan.wallet.bean.Wallet;
import com.alan.wallet.greendao.WalletDaoMaster;
import com.alan.wallet.view.FontChangePagerTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alan on 2018/5/2.
 * 导出keystore
 */
public class ExportKeystoreActivity extends AppCompatActivity {

    @BindView(R.id.img_goback)
    ImageView imgGoback;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.main_viewpager)
    ViewPager mainViewpager;

    /**
     * 返回按钮
     **/
    private ExportKeystoreAdapter mAdapter;

    private Wallet wallet;


    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_export_keystore);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        try {
            address = bundle.getString("address");
        } catch (Exception e) {
            e.printStackTrace();
        }

        wallet = WalletDaoMaster.queryWalletByAddress(address);

        initView();

    }

    private void initView() {
        textTitle.setText(R.string.export_keystore);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

//        String[] mString = { getString( R.string.mnemonic)  , getString(R.string.official)};
        String[] mString = {getString(R.string.keystore), getString(R.string.qrcode)};

        String keystore = wallet.getKeyStore();

        mAdapter = new ExportKeystoreAdapter(getSupportFragmentManager(), keystore, mString);
        mainViewpager.setAdapter(mAdapter);

        initMagicIndicator(mString);

    }

    private void initMagicIndicator(String[] mString) {
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
//        magicIndicator.setBackgroundColor(Color.parseColor("#00c853"));
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.25f);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mString == null ? 0 : mString.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new FontChangePagerTitleView(context);
                simplePagerTitleView.setText(mString[index]);
                simplePagerTitleView.setNormalColor(Color.parseColor("#c1bfbf"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#149EFF"));
                simplePagerTitleView.setTextSize(15);
                simplePagerTitleView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
//                simplePagerTitleView.setBackgroundColor(Color.GREEN);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainViewpager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_MATCH_EDGE);
                indicator.setLineHeight(3);
//                indicator.setLineWidth();
//                indicator.setYOffset(UIUtil.dip2px(context, 0));
                indicator.setColors(Color.parseColor("#149EFF"));

                return indicator;
            }


        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mainViewpager);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick(R.id.img_goback)
    public void onViewClicked() {
        finish();
    }
}
