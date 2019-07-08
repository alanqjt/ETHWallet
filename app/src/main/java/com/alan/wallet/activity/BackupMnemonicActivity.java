package com.alan.wallet.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alan.wallet.R;
import com.alan.wallet.bean.Wallet;
import com.alan.wallet.greendao.WalletDaoMaster;
import com.alan.wallet.utils.DialogV7Utils;
import com.alan.wallet.utils.Logger;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zhy.view.flowlayout.TagView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Alan on 2018/4/26.
 * 备份助记词
 */
public class BackupMnemonicActivity extends AppCompatActivity {

    @BindView(R.id.img_goback)
    ImageView imgGoback;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_mnemonic)
    TextView tvMnemonic;
    @BindView(R.id.id_flowlayout)
    TagFlowLayout idFlowlayout;
    @BindView(R.id.id_flowlayout2)
    TagFlowLayout idFlowlayout2;
    @BindView(R.id.btn_ImportWallet)
    TextView btnImportWallet;
    @BindView(R.id.btn_next)
    Button btnNext;

    private String address;

    private TagAdapter adapter;
    private TagAdapter adapter2;

    private List<String> arrTab = new ArrayList();
    private List<String> arrTab2 = new ArrayList();

    private Map<String, View> mTextViewMap = new HashMap<>();

    private int step = 0;

    Wallet wallet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Bundle bundle = getIntent().getExtras();
        try {
            address = bundle.getString("address");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_backup_mnemonic);
        ButterKnife.bind(this);
        wallet = WalletDaoMaster.queryWalletByAddress(address);
        initTagFlowLayoutData();
        idFlowlayout.setVisibility(View.GONE);
        idFlowlayout2.setVisibility(View.GONE);
        tvMnemonic.setText(wallet.getMnemonicWords());
        initTagView();
        textTitle.setText(R.string.backup_mnemonics);

        Logger.d(this, "助记词:" + wallet.getMnemonicWords());
    }

    private void initTagView() {
        //设置adapter
        adapter = new TagAdapter<String>(arrTab) {
            @SuppressLint("NewApi")
            @Override
            public View getView(FlowLayout parent, final int position, String s) {
                TextView tv = new TextView(BackupMnemonicActivity.this);
                tv.setText(s);
                //动态设置shape
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(40);
                drawable.setColor(Color.WHITE);
                if (!"".equals(s)) {
                    tv.setBackground(drawable);
                    tv.setPadding(12, 5, 12, 5);
                    tv.setTextColor(Color.parseColor("#303133"));
                }

                return tv;

            }
        };
        adapter2 = new TagAdapter<String>(arrTab2) {
            @Override
            public View getView(FlowLayout parent, final int position, String s) {
                TextView tv = new TextView(BackupMnemonicActivity.this);
                tv.setText(s);
                tv.setTextColor(Color.parseColor("#149EFF"));
                tv.setGravity(Gravity.CENTER);
                tv.setBackgroundResource(R.drawable.selector_btn_mnemonic_textview);
                return tv;

            }
        };

        idFlowlayout.setAdapter(adapter);
        idFlowlayout2.setAdapter(adapter2);

        idFlowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                TagView tagView = (TagView) mTextViewMap.get(arrTab.get(position));
                if (tagView == null) {
                    return true;
                }
                TextView textView = (TextView) tagView.getTagView();
                textView.setTextColor(Color.parseColor("#149EFF"));
                tagView.setSelected(false);
                arrTab2.add(arrTab.get(position));
                arrTab.remove(position);
                adapter.notifyDataChanged();
                adapter2.notifyDataChanged();
                return false;
            }
        });


        //点击助记词
        idFlowlayout2.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                TextView textView = (TextView) ((TagView) view).getTagView();
                textView.setTextColor(Color.parseColor("#149EFF"));
                textView.setSelected(true);
                mTextViewMap.put(textView.getText().toString(), view);
                arrTab.add(arrTab.size() - 1, arrTab2.get(position));
                arrTab2.remove(position);
                adapter.notifyDataChanged();
                adapter2.notifyDataChanged();
                return false;
            }
        });
    }


    private void initTagFlowLayoutData() {
        //添加一条数据用于添加标签的替换
        arrTab.add("");
        String[] split = wallet.getMnemonicWords().split(" ");
        for (String s : randStringArr(split)) {
            arrTab2.add(s);
        }
    }

    public String[] randStringArr(String[] arr) {
        String[] arr2 = new String[arr.length];
        int count = arr.length;
        int cbRandCount = 0;// 索引
        int cbPosition = 0;// 位置
        int k = 0;
        do {
            Random rand = new Random();
            int r = count - cbRandCount;
            cbPosition = rand.nextInt(r);
            arr2[k++] = arr[cbPosition];
            cbRandCount++;
            arr[cbPosition] = arr[r - 1];// 将最后一位数值赋值给已经被使用的cbPosition
        } while (cbRandCount < count);
        return arr2;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @OnClick({R.id.img_goback, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_goback:
                finish();
                break;
            case R.id.btn_next:
                if (step == 0) {
                    tvMnemonic.setVisibility(View.GONE);
                    idFlowlayout.setVisibility(View.VISIBLE);
                    idFlowlayout2.setVisibility(View.VISIBLE);
                    step++;
                } else {
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < arrTab.size(); i++) {
                        sb.append(arrTab.get(i));
                        if (i < arrTab.size() - 2) {
                            //倒数第二个不加空格
                            sb.append(" ");
                        }
                    }
                    if (sb.toString().equals(wallet.getMnemonicWords())) {
                        DialogV7Utils.showNormalDialog(this,
                                getResources().getString(R.string.confirmed_mnemonic),
                                new DialogV7Utils.OnNormalDialogSureListener() {
                                    @Override
                                    public void sureCallBack() {
                                        wallet.setMnemonicWords("");
                                        wallet.setIsBackUp(true);
                                        WalletDaoMaster.insertWallet(wallet);
                                        finish();
                                    }

                                    @Override
                                    public void cancelCallBack() {
                                        WalletDaoMaster.insertWallet(wallet);
                                    }
                                });
                    } else {
                        DialogV7Utils.showCancelDialog(this,
                                getResources().getString(R.string.backup_failed),
                                getResources().getString(R.string.backup_failed_reason),
                                getResources().getString(R.string.ok));
                    }
                }
                break;
        }
    }
}
