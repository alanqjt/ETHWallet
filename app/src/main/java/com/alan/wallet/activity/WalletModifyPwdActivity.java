package com.alan.wallet.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alan.wallet.R;
import com.alan.wallet.bean.Wallet;
import com.alan.wallet.ethwallet.EthWalletManager;
import com.alan.wallet.greendao.WalletDaoMaster;
import com.alan.wallet.utils.DialogV7Utils;
import com.alan.wallet.utils.Logger;
import com.alan.wallet.utils.RegexUtil;
import com.alan.wallet.utils.StrUtil;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by Alan on 2018/4/26.
 */
public class WalletModifyPwdActivity extends AppCompatActivity {

    @BindView(R.id.img_goback)
    ImageView imgGoback;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.text_save)
    TextView textSave;
    @BindView(R.id.common_back)
    RelativeLayout commonBack;
    @BindView(R.id.ed_old_pwd)
    EditText edOldPwd;
    @BindView(R.id.view_divide1)
    View viewDivide1;
    @BindView(R.id.ed_new_pwd)
    EditText edNewPwd;
    @BindView(R.id.view_divide2)
    View viewDivide2;
    @BindView(R.id.ed_ensure_pwd)
    EditText edEnsurePwd;
    @BindView(R.id.view_divide3)
    View viewDivide3;
    @BindView(R.id.tv_forget_tip)
    TextView tvForgetTip;

    private String address;

    private Wallet wallet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_wallet_modify_pwd);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        try {
            address = bundle.getString("address");
        } catch (Exception e) {
            e.printStackTrace();
        }
        wallet = WalletDaoMaster.queryWalletByAddress(address);

        textTitle.setText("修改密码");
        initFindView();

    }


    private void initFindView() {

        tvForgetTip.setHighlightColor(getResources().getColor(android.R.color.transparent));
        String tip1 = getResources().getString(R.string.forget_pwd_tip);
        String tip2 = getResources().getString(R.string.forget_pwd_tip2);
        SpannableString spanableInfo = new SpannableString(tip1 + tip2);
        spanableInfo.setSpan(new Clickable(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(WalletModifyPwdActivity.this, ImportWalletActivity.class));
                        finish();
                    }
                }),
                tip1.length(),
                (tip1 + tip2).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvForgetTip.setText(spanableInfo);
        tvForgetTip.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @OnClick({R.id.img_goback, R.id.text_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_goback:
                finish();
                break;
            case R.id.text_save:

                String old_pwd = edOldPwd.getText().toString();
                String new_pwd = edNewPwd.getText().toString();
                String ensure_pwd = edEnsurePwd.getText().toString();

                if (!validatingPassword(old_pwd, new_pwd, ensure_pwd)) {
                    Logger.d(this, "密码规则 没过");
                    return;
                }

                rx.Observable.just(null)
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Func1<Object, Object>() {
                            @Override
                            public Object call(Object o) {
                                showHud("please wait");
                                return null;
                            }
                        })
                        .observeOn(Schedulers.computation())
                        .map(new Func1<Object, Object>() {
                            @Override
                            public Object call(Object o) {
                                EthWalletManager ethWalletManager = new EthWalletManager();
                                ethWalletManager.modifyWalletPassword(address, new_pwd);
                                return o;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Func1<Object, Object>() {
                            @Override
                            public Object call(Object o) {
                                dismissHud();
                                showHud("success");
                                return o;
                            }
                        })
                        .delay(2000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                dismissHud();
                                finish();
                            }
                        });


                break;
        }
    }

    class Clickable extends ClickableSpan {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener l) {
            mListener = l;
        }

        /**
         * 重写父类点击事件
         */
        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }

        /**
         * 重写父类updateDrawState方法  我们可以给TextView设置字体颜色,背景颜色等等...
         */
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.parseColor("#149EFF"));
        }
    }

    private boolean validatingPassword(String old_pwd, String new_pwd, String ensure_pwd) {
        if (StrUtil.isNullOrEmpty(old_pwd) || StrUtil.isNullOrEmpty(new_pwd) || StrUtil.isNullOrEmpty(ensure_pwd)) {
            DialogV7Utils.showCancelDialog(this,
                    getResources().getString(R.string.pwd_alert_dialog_tip1),
                    getResources().getString(R.string.ok));
            return false;
        }

        if (!RegexUtil.isPasswordExact(old_pwd) || !RegexUtil.isPasswordExact(new_pwd)) {
            DialogV7Utils.showCancelDialog(this,
                    getResources().getString(R.string.password_regex),
                    getResources().getString(R.string.ok));
            return false;
        }

        if (!old_pwd.equals(wallet.getWalletPassword())) {
            DialogV7Utils.showCancelDialog(this,
                    getResources().getString(R.string.old_pwd_incorrect),
                    getResources().getString(R.string.ok));
            return false;
        }

        if (!new_pwd.equals(ensure_pwd)) {
            DialogV7Utils.showCancelDialog(this,
                    getResources().getString(R.string.the_passwords_you_entered_do_not_match),
                    getResources().getString(R.string.ok));
            return false;
        }

        return true;
    }


    /**
     * loading动画,唯一
     */
    protected KProgressHUD hud;

    /**
     * 在主线程调用
     * 显示loading动画
     */
    protected void showHud(String label) {
        if (hud == null) {
            hud = KProgressHUD.create(WalletModifyPwdActivity.this);
            hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(label)
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
