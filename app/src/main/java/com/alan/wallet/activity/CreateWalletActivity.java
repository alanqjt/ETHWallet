package com.alan.wallet.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.wallet.R;
import com.alan.wallet.ethwallet.EthWallet;
import com.alan.wallet.ethwallet.EthWalletCallBack;
import com.alan.wallet.ethwallet.EthWalletManager;
import com.alan.wallet.utils.Logger;
import com.alan.wallet.utils.PasswordUtils;
import com.alan.wallet.utils.RegexUtil;
import com.alan.wallet.utils.StrUtil;
import com.alan.wallet.view.PasswordLevelView;
import com.gyf.immersionbar.ImmersionBar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alan on 2018/5/2.
 * 创建钱包的界面
 */
public class CreateWalletActivity extends AppCompatActivity {
    @BindView(R.id.durian_back_image)
    ImageView durianBackImage;
    @BindView(R.id.text_title)
    TextView textTitle;
    @BindView(R.id.text_WalletName)
    EditText textWalletName;
    @BindView(R.id.text_Password1)
    EditText textPassword1;
    @BindView(R.id.pswd_level_view)
    PasswordLevelView pswdLevelView;
    @BindView(R.id.view_pwd_divide)
    View viewPwdDivide;
    @BindView(R.id.tv_pwd_tip)
    TextView tvPwdTip;
    @BindView(R.id.text_Password2)
    EditText textPassword2;
    @BindView(R.id.text_PasswordHit)
    EditText textPasswordHit;
    @BindView(R.id.femaleGroupID)
    RadioButton femaleGroupID;
    @BindView(R.id.text_userAgreement)
    TextView textUserAgreement;
    @BindView(R.id.btn_ImportWallet)
    TextView btnImportWallet;
    @BindView(R.id.btn_CreatWallet)
    Button btnCreatWallet;

    private KProgressHUD hud;
    private boolean isCanClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_create_wallet);
        ButterKnife.bind(this);

        textTitle.setText(R.string.wallet_management_creat_wallet);
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(true, 0.2f).init();

        initView();
    }

    private void initView() {
        initfemaleGroup();
        initPasswordSetting();
    }

    private void initfemaleGroup() {

        final GlobalValue globalValue = new GlobalValue();
        femaleGroupID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = globalValue.isCheck();
                if (isCheck) {
                    if (v == femaleGroupID) femaleGroupID.setChecked(false);
                } else {
                    if (v == femaleGroupID) femaleGroupID.setChecked(true);
                }
                globalValue.setCheck(!isCheck);
            }
        });

        btnBg();

        femaleGroupID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCanClick = isChecked;
                Logger.d(this, "isCanclick = " + isChecked);
                if (isCanClick) {

                    btnCreatWallet.setEnabled(true);
                } else {
                    btnCreatWallet.setEnabled(false);
                }
                btnBg();
            }
        });
    }


    private void initPasswordSetting() {
        InputFilter filter = PasswordUtils.generateEditTextFilter1();
        textPassword1.setFilters(new InputFilter[]{filter});

        textPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String ed_pwd_text = textPassword1.getText().toString().trim();
                if (ed_pwd_text.length() == 0) {
                    pswdLevelView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String ed_pwd_text = textPassword1.getText().toString().trim();
                if (ed_pwd_text.length() == 0) {
                    pswdLevelView.setVisibility(View.GONE);
                    tvPwdTip.setVisibility(View.GONE);
                    return;
                } else {
                    pswdLevelView.setVisibility(View.VISIBLE);
                    tvPwdTip.setVisibility(View.VISIBLE);
                }
//                Log.v("ccm","ed_pwd_text"+ed_pwd_text);
                PasswordLevelView.Level level = PasswordUtils.checkPassword(ed_pwd_text);
                switch (level) {
                    case DANGER:
                        tvPwdTip.setText(R.string.private_key_pwd_tip1);
                        tvPwdTip.setTextColor(level.getColor());
                        break;
                    case LOW:
                        tvPwdTip.setText(R.string.private_key_pwd_tip2);
                        tvPwdTip.setTextColor(level.getColor());
                        break;
                    default:
                        tvPwdTip.setVisibility(View.GONE);
                        break;
                }
                pswdLevelView.showLevel(level);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void btnBg() {
        if (isCanClick)
            btnCreatWallet.setBackgroundResource(R.drawable.shape_btn_blue);
        else
            btnCreatWallet.setBackgroundResource(R.drawable.shape_btn_gray);
    }

    final RxPermissions rxPermissions = new RxPermissions(this);
    @OnClick({R.id.durian_back_image, R.id.btn_ImportWallet, R.id.btn_CreatWallet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.durian_back_image:
                finish();
                break;
            case R.id.btn_ImportWallet:
                startActivity(new Intent(CreateWalletActivity.this, ImportWalletActivity.class));
                break;
            case R.id.btn_CreatWallet:

                rxPermissions
                        .request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                CreateWallet();
                            } else {
                            }
                        });
                break;
        }
    }


    public class GlobalValue {
        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        private boolean isCheck;
    }

    private EthWalletManager wManager;

    private void CreateWallet() {
        String WalletName = EditGetText(textWalletName);

        String Password1 = EditGetText(textPassword1);
        String Password2 = EditGetText(textPassword2);
        String PasswordHit = EditGetText(textPasswordHit);


        if (StrUtil.isNullOrEmpty(WalletName)) {
            MyToast(R.string.wallet_name_cannot_be_empty);
            return;
        }

        if (StrUtil.isNullOrEmpty(Password1)) {
            MyToast(R.string.password_cannot_be_empty);
            return;
        }

        if (!RegexUtil.isPasswordExact(Password1)) {
            MyToast(R.string.password_regex);
            return;
        }

        if (!Password1.equals(Password2)) {
            MyToast(R.string.the_passwords_you_entered_do_not_match);
            return;
        }


        requestLoading();
        wManager = new EthWalletManager();

        try {
            wManager.createWallet(WalletName, Password1, PasswordHit, false, new EthWalletCallBack() {

                @Override
                public void onSuccessCallBack(EthWallet ethWallet, String fileName, String walletAddress, String storeText) throws Exception {
                    Logger.d(this, "生成的钱包地址:" + walletAddress);
                    dismiss();
                    finish();
                }

                @Override
                public void onErrorCallBack(Exception e) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void dismiss() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hud.dismiss();
            }
        });
    }

    public void requestLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hud = KProgressHUD.create(CreateWalletActivity.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel(getResources().getString(R.string.please_wait))
                        .setCancellable(false)
                        .setAnimationSpeed(2)
                        .setDimAmount(0.5f)
                        .show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wManager != null) {
            wManager.finish();
            wManager = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void MyToast(String str) {

        Toast.makeText(this, "" + str, Toast.LENGTH_SHORT).show();
    }

    public void MyToast(int res) {
        String str = this.getResources().getString(res);
        Toast.makeText(this, "" + str, Toast.LENGTH_SHORT).show();
    }

    public String EditGetText(EditText editText) {
        return editText.getText().toString().trim();
    }


}
