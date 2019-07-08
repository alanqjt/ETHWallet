package com.alan.wallet.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.wallet.R;
import com.alan.wallet.bean.Wallet;
import com.alan.wallet.dialog.NDialog;
import com.alan.wallet.ethwallet.EthWallet;
import com.alan.wallet.ethwallet.EthWalletCallBack;
import com.alan.wallet.ethwallet.EthWalletManager;
import com.alan.wallet.greendao.WalletDaoMaster;
import com.alan.wallet.utils.DialogV7Utils;
import com.alan.wallet.utils.StrUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.maksim88.passwordedittext.PasswordEditText;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.graphics.Typeface.SANS_SERIF;


/**
 * Created by Alan on 2018/4/26.
 * 钱包详情页----从管理钱包 点击钱包列表 进来
 */
public class WalletDetailActivity extends AppCompatActivity {

    @BindView(R.id.img_goback)
    ImageView imgGoback;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.text_WalletAddress)
    TextView textWalletAddress;
    @BindView(R.id.ll_head)
    LinearLayout llHead;
    @BindView(R.id.ed_WalletName)
    EditText edWalletName;
    @BindView(R.id.ed_WalletPasswordHint)
    PasswordEditText edWalletPasswordHint;
    @BindView(R.id.ll_WalletPasswordHint)
    LinearLayout llWalletPasswordHint;
    @BindView(R.id.tv_modify_pwd)
    TextView tvModifyPwd;
    @BindView(R.id.tv_export_pk)
    TextView tvExportPk;
    @BindView(R.id.tv_export_ks)
    TextView tvExportKs;
    @BindView(R.id.btn_backup)
    Button btnBackup;
    @BindView(R.id.btn_DelWallet)
    Button btnDelWallet;
    @BindView(R.id.text_save)
    TextView textSave;

    private String address;


    private CompositeSubscription mCompositeSubscription
            = new CompositeSubscription();


    Wallet wallet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_wallet_detail);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        try {
            address = bundle.getString("address");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(true, 0.2f).init();

        imgGoback.setColorFilter(Color.parseColor("#ffffff"));


        wallet = WalletDaoMaster.queryWalletByAddress(address);
        textTitle.setText(wallet.getWalletName());
        textTitle.setTextColor(Color.WHITE);
        if (!StrUtil.isNullOrEmpty(wallet.getWalletPasswordHit())) {
            edWalletPasswordHint.setText(wallet.getWalletPasswordHit());
        }
        textWalletAddress.setText(address);
        edWalletName.setText(wallet.getWalletName());
    }


    @Override
    protected void onResume() {
        super.onResume();
        wallet = WalletDaoMaster.queryWalletByAddress(address);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (wallet != null) {
            if (StrUtil.isNullOrEmpty(wallet.getMnemonicWords())) {
                btnBackup.setVisibility(View.GONE);
            }
        }
    }

    Subscriber<? super Integer> mSubscriber;
    private KProgressHUD hud;

    private static final int STEP1 = 1;
    private static final int STEP2 = 2;


    /**
     * 删除钱包
     **/
    public void DelWallet() {

        //发送者
        Observable<Integer> deleteWalletObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                mSubscriber = subscriber;
                subscriber.onNext(STEP1);
            }
        })
                //切换到主线程
                .observeOn(AndroidSchedulers.mainThread())
                //转化值
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        hud = KProgressHUD.create(WalletDetailActivity.this);
                        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel(getResources().getString(R.string.please_wait))
                                .setCancellable(false)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();


                        return integer;
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Func1<Integer, Integer>() {

                    //当前分支
                    private Integer nextStepInteger = 2;
                    private Object o = new Object();

                    @Override
                    public Integer call(final Integer integer) {
                        synchronized (o) {
                            try {
                                EthWalletManager manager = new EthWalletManager();

                                manager.deleteWallet(wallet, new EthWalletCallBack() {
                                    @Override
                                    public void onSuccessCallBack(EthWallet ethWallet, String fileName, String walletAddress, String storeText) throws Exception {
                                        nextStepInteger = integer;
                                        synchronized (o) {
                                            o.notify();
                                        }
                                    }

                                    @Override
                                    public void onErrorCallBack(Exception e) {
                                        //删除失败
                                        nextStepInteger = STEP2;
                                        synchronized (o) {
                                            o.notify();
                                        }
                                    }
                                });
                                o.wait(15000);

                            } catch (Exception e) {
                                //删除失败
                                nextStepInteger = STEP2;
                                o.notify();
                            }

                        }


                        return nextStepInteger;
                    }
                })
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        Observable<Integer> observable = Observable.just(integer);
                        if (integer == STEP1) {
                            return observable
                                    .delay(2, TimeUnit.SECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .map(new Func1<Integer, Integer>() {
                                        @Override
                                        public Integer call(Integer integer) {
                                            hud.dismiss();
                                            hud = null;
                                            WalletDetailActivity.this.finish();
                                            mSubscriber.onCompleted();
                                            return integer;
                                        }
                                    });
                        } else {
                            return observable
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .map(new Func1<Integer, Integer>() {
                                        @Override
                                        public Integer call(Integer integer) {
                                            hud.dismiss();
                                            hud = null;
                                            mSubscriber.onCompleted();
                                            return integer;
                                        }
                                    });
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        Subscriber<Integer> deleteWalletSubscriber = new Subscriber<Integer>() {

            @Override
            public void onCompleted() {
                //Log.e(TAG, "onCompleted" + "\n");
//                btn_DelWallet.setEnabled(true);
            }

            @Override
            public void onError(Throwable e) {
                //Log.e(TAG, "onError"+e.getMessage()+","+e.toString()+"\n");
            }

            @Override
            public void onNext(Integer integer) {
                //Log.e(TAG, "onNext:" + integer + "\n");

            }
        };

        //进行订阅
        Subscription deleteWalletSubscription = deleteWalletObservable.subscribe(deleteWalletSubscriber);
        //将订阅关系传到添加到进一个集合里,以后统一取消订阅
        mCompositeSubscription.add(deleteWalletSubscription);

    }

    /**
     * 这是兼容的 AlertDialog
     */
    private void showDialog(String message) {

        DialogV7Utils.showCancelDialog(WalletDetailActivity.this, message, getResources().getString(R.string.cancel));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
        mCompositeSubscription = null;
    }


    @OnClick({R.id.img_goback, R.id.tv_modify_pwd, R.id.tv_export_pk, R.id.tv_export_ks, R.id.btn_backup, R.id.btn_DelWallet, R.id.text_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_goback:
                finish();
                break;
            case R.id.tv_modify_pwd:
                Intent intent = new Intent(this, WalletModifyPwdActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("address", address);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            case R.id.text_save:
                String newName = edWalletName.getText().toString();
                if (StrUtil.isNullOrEmpty(newName)) {
                    showDialog(getResources().getString(R.string.wallet_name_cannot_be_empty));
                    return;
                }
                wallet.setWalletName(newName);
                wallet.setWalletPasswordHit(edWalletPasswordHint.getText().toString());
                WalletDaoMaster.insertWallet(wallet);
                this.finish();
                break;

            case R.id.tv_export_pk:
                DialogV7Utils.showEditTextDialog(
                        WalletDetailActivity.this,
                        null,
                        getResources().getString(R.string.please_enter_password),
                        new DialogV7Utils.OnEditTextDialogSureListener() {

                            @SuppressLint("NewApi")
                            @Override
                            public void sureCallBack(String edittext) {
                                if (edittext.equals(wallet.getWalletPassword())) {
                                    LayoutInflater inflater = getLayoutInflater();
                                    View layout = inflater.inflate(R.layout.dialog_export_pk, null);
                                    TextView tv_export_pk_title = (TextView) layout.findViewById(R.id.tv_export_pk_title);
                                    TextView tv_export_pk_tip1 = (TextView) layout.findViewById(R.id.tv_export_pk_tip1);
                                    TextView tv_export_pk_tip2 = (TextView) layout.findViewById(R.id.tv_export_pk_tip2);
                                    tv_export_pk_title.setTypeface(SANS_SERIF);

                                    //动态设置shape
                                    GradientDrawable drawable1 = new GradientDrawable();
                                    drawable1.setGradientType(GradientDrawable.RECTANGLE);
                                    drawable1.setColor(Color.parseColor("#FAF0EE"));
                                    drawable1.setStroke(1, Color.parseColor("#E5C2B9"));
                                    float cornerRadii = 5;
                                    drawable1.setCornerRadii(new float[]{cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii});

                                    GradientDrawable drawable2 = new GradientDrawable();
                                    drawable2.setGradientType(GradientDrawable.RECTANGLE);
                                    drawable2.setColor(ContextCompat.getColor(WalletDetailActivity.this, R.color.gray_f6));
                                    drawable2.setCornerRadii(new float[]{cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii});

                                    GradientDrawable drawable3 = new GradientDrawable();
                                    drawable3.setGradientType(GradientDrawable.RECTANGLE);
                                    drawable3.setColor(Color.parseColor("#FFFFFF"));
                                    drawable3.setCornerRadii(new float[]{cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii, cornerRadii});

                                    tv_export_pk_tip1.setTextColor(Color.parseColor("#D87F69"));
                                    tv_export_pk_tip2.setTextColor(Color.BLACK);
                                    tv_export_pk_tip1.setBackground(drawable1);
                                    tv_export_pk_tip2.setBackground(drawable2);
                                    String pk = new BigInteger(wallet.getPrivateKey()).toString(16);
                                    tv_export_pk_tip2.setText(pk);

                                    Button btn_copy = (Button) layout.findViewById(R.id.btn_copy);
                                    AlertDialog alertDialog = new NDialog(WalletDetailActivity.this)
                                            .setPositiveButtonText(null)
                                            .setNegativeButtonText(null)
                                            .create(layout);
                                    alertDialog.show();
                                    Window window = alertDialog.getWindow();
                                    window.setBackgroundDrawableResource(android.R.color.transparent);
                                    layout.setBackground(drawable3);

                                    btn_copy.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                            //获取剪贴板管理器：
                                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                            // 创建普通字符型ClipData
                                            ClipData mClipData = ClipData.newPlainText("pk", pk);
                                            // 将ClipData内容放到系统剪贴板里。
                                            cm.setPrimaryClip(mClipData);

                                            Toast.makeText(WalletDetailActivity.this, getResources().getString(R.string.copied), Toast.LENGTH_LONG).show();
                                        }
                                    });

                                } else {
                                    showDialog(getResources().getString(R.string.the_passwords_you_entered_do_not_match));
                                }
                            }

                            @Override
                            public void cancelCallBack(String edittext) {

                            }
                        }
                );
                break;
            case R.id.tv_export_ks:
                DialogV7Utils.showEditTextDialog(
                        WalletDetailActivity.this,
                        null,
                        getResources().getString(R.string.please_enter_password),
                        new DialogV7Utils.OnEditTextDialogSureListener() {

                            @Override
                            public void sureCallBack(String edittext) {
                                if (edittext.equals(wallet.getWalletPassword())) {
                                    Intent intent = new Intent(WalletDetailActivity.this, ExportKeystoreActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("address", address);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else {
                                    showDialog(getResources().getString(R.string.the_passwords_you_entered_do_not_match));
                                }
                            }

                            @Override
                            public void cancelCallBack(String edittext) {

                            }
                        }
                );
                break;
            case R.id.btn_backup:
                DialogV7Utils.showEditTextDialog(
                        WalletDetailActivity.this,
                        getResources().getString(R.string.please_enter_password),
                        getResources().getString(R.string.irrevocable_operation),
                        new DialogV7Utils.OnEditTextDialogSureListener() {

                            @Override
                            public void sureCallBack(String edittext) {
                                Log.e("ccm", "edittext:");
                                Log.e("ccm", "edittext1:" + edittext);
                                Log.e("ccm", "edittext2:" + wallet.getWalletPassword());
                                if (edittext.equals(wallet.getWalletPassword())) {
                                    Intent intent = new Intent(WalletDetailActivity.this, BackupMnemonicActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("address", address);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else {
                                    showDialog(getResources().getString(R.string.the_passwords_you_entered_do_not_match));
                                }
                            }

                            @Override
                            public void cancelCallBack(String edittext) {

                            }
                        }
                );
                break;
            case R.id.btn_DelWallet:
                DialogV7Utils.showEditTextDialog(
                        WalletDetailActivity.this,
                        getResources().getString(R.string.please_enter_password),
                        getResources().getString(R.string.irrevocable_operation),
                        new DialogV7Utils.OnEditTextDialogSureListener() {

                            @Override
                            public void sureCallBack(String edittext) {
                                Log.e("ccm", "edittext:");
                                Log.e("ccm", "edittext1:" + edittext);
                                Log.e("ccm", "edittext2:" + wallet.getWalletPassword());
                                if (edittext.equals(wallet.getWalletPassword())) {
                                    DelWallet();
                                } else {
                                    showDialog(getResources().getString(R.string.the_passwords_you_entered_do_not_match));
                                }
                            }

                            @Override
                            public void cancelCallBack(String edittext) {

                            }
                        }
                );
                break;
        }
    }
}
