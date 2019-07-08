package com.alan.wallet.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import com.alan.wallet.adapter.MyAdapter;
import com.alan.wallet.fragment.BaseImportFragment;
import com.alan.wallet.view.FontChangePagerTitleView;
import com.gyf.immersionbar.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
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
 * 导入钱包
 */
public class ImportWalletActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 999;
    @BindView(R.id.durian_back_image)
    ImageView durianBackImage;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R.id.main_Viewpager)
    ViewPager mainViewpager;
    @BindView(R.id.img_scan)
    ImageView imgScan;

    private MyAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_import_wallet);
        ButterKnife.bind(this);
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(true, 0.2f).init();

        initView();

    }

    private void initView() {

        textTitle.setText(R.string.wallet_management_import_wallet);
        textTitle.setTextColor(Color.WHITE);
        durianBackImage.setColorFilter(Color.parseColor("#ffffff"));
        imgScan.setColorFilter(Color.parseColor("#ffffff"));
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        String[] mString = {getString(R.string.mnemonic), getString(R.string.official), getString(R.string.private_key)};

        mAdapter = new MyAdapter(getSupportFragmentManager(), mString);
        mainViewpager.setOffscreenPageLimit(3);
        mainViewpager.setAdapter(mAdapter);

        initMagicIndicator(mString);

    }

    private void initMagicIndicator(String[] mString) {
        magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setScrollPivotX(0.25f);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mString == null ? 0 : mString.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new FontChangePagerTitleView(context);
                simplePagerTitleView.setText(mString[index]);
                simplePagerTitleView.setMinWidth(UIUtil.dip2px(context, 45));
                simplePagerTitleView.setNormalColor(Color.WHITE);
                simplePagerTitleView.setSelectedColor(Color.WHITE);
                simplePagerTitleView.setTextSize(15);
                simplePagerTitleView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
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
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setLineHeight(UIUtil.dip2px(context, 2));
                indicator.setYOffset(UIUtil.dip2px(context, 0));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setColors(Color.parseColor("#ffffff"));

                return indicator;
            }


        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mainViewpager);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    BaseImportFragment fragment = (BaseImportFragment) mAdapter.getItem(mainViewpager.getCurrentItem());
                    fragment.excuteByScanResult(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
//                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    final RxPermissions rxPermissions = new RxPermissions(this);

    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick({R.id.durian_back_image, R.id.img_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.durian_back_image:
                finish();
                break;
            case R.id.img_scan:

                rxPermissions
                        .request(Manifest.permission.CAMERA, Manifest.permission.VIBRATE)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                Intent intent = new Intent(ImportWalletActivity.this, DiyCaptureActivity.class);
                                startActivityForResult(intent, REQUEST_CODE);
                            } else {
                            }
                        });


                break;
        }
    }
}
