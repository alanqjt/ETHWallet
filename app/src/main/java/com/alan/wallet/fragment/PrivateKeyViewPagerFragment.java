package com.alan.wallet.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.alan.wallet.R;
import com.alan.wallet.utils.PasswordUtils;
import com.alan.wallet.utils.RegexUtil;
import com.alan.wallet.utils.StrUtil;
import com.alan.wallet.view.PasswordLevelView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.web3j.crypto.ECKeyPair;

public class PrivateKeyViewPagerFragment extends BaseImportFragment {

    private int mTitle;
    private int mColor;
    private TextView mTextView;
    private LinearLayout mLinear;
    /**
     * 显示密码强度控件
     */
    private PasswordLevelView mPlv;
    private EditText mEd_private_key, mEd_pwd, mEd_ensure_pwd, mEd_pwd_hint;
    private TextView mTv_pwd_tip;
    private CheckBox mFemaleGroupID;

    private static PrivateKeyViewPagerFragment sFragment;

    public static PrivateKeyViewPagerFragment newInstance() {
        if (sFragment == null) {
            PrivateKeyViewPagerFragment fragment = new PrivateKeyViewPagerFragment();
            sFragment = fragment;
        }
        return sFragment;
    }

    TextView text_userAgreement;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewpager_privatekey, container, false);
        mPlv = (PasswordLevelView) view.findViewById(R.id.pswd_level_view);
        mEd_private_key = (EditText) view.findViewById(R.id.ed_private_key);
        mEd_pwd = (EditText) view.findViewById(R.id.ed_pwd);
        mEd_ensure_pwd = (EditText) view.findViewById(R.id.ed_ensure_pwd);
        mTv_pwd_tip = (TextView) view.findViewById(R.id.tv_pwd_tip);
        mEd_pwd_hint = (EditText) view.findViewById(R.id.ed_pwd_hint);
        mFemaleGroupID = (CheckBox) view.findViewById(R.id.femaleGroupID);
        mBtn_Import = (Button) view.findViewById(R.id.btn_Import);
        text_userAgreement = view.findViewById(R.id.text_userAgreement);
        final RxPermissions rxPermissions = new RxPermissions(this);
        InputFilter filter = PasswordUtils.generateEditTextFilter1();
        mEd_pwd.setFilters(new InputFilter[]{filter});

        mEd_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String ed_pwd_text = mEd_pwd.getText().toString().trim();
                if (ed_pwd_text.length() == 0) {
                    mPlv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String ed_pwd_text = mEd_pwd.getText().toString().trim();
                if (ed_pwd_text.length() == 0) {
                    mPlv.setVisibility(View.GONE);
                    mTv_pwd_tip.setVisibility(View.GONE);
                    return;
                } else {
                    mPlv.setVisibility(View.VISIBLE);
                    mTv_pwd_tip.setVisibility(View.VISIBLE);
                }
//                Log.v("ccm","ed_pwd_text"+ed_pwd_text);
                PasswordLevelView.Level level = PasswordUtils.checkPassword(ed_pwd_text);
                switch (level) {
                    case DANGER:
                        mTv_pwd_tip.setText(R.string.private_key_pwd_tip1);
                        mTv_pwd_tip.setTextColor(level.getColor());
                        break;
                    case LOW:
                        mTv_pwd_tip.setText(R.string.private_key_pwd_tip2);
                        mTv_pwd_tip.setTextColor(level.getColor());
                        break;
                    default:
                        mTv_pwd_tip.setVisibility(View.GONE);
                        break;
                }
                mPlv.showLevel(level);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mFemaleGroupID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mBtn_Import.setEnabled(true);
                } else {
                    mBtn_Import.setEnabled(false);
                }
            }
        });


        mBtn_Import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                rxPermissions
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) { // Always true pre-M
                                importWallet();
                            } else {
                            }
                        });

            }
        });


        return view;
    }

    private void importWallet() {


        String pk_text = mEd_private_key.getText().toString();
        String Password1 = mEd_pwd.getText().toString();
        String Password2 = mEd_ensure_pwd.getText().toString();
        String PasswordHit = mEd_pwd_hint.getText().toString();


        if (StrUtil.isNullOrEmpty(pk_text) || StrUtil.isNullOrEmpty(Password1)) {
//                    MyToast(getResources().getString(R.string.mnemonic_alert_dialog_tip1));
            showCancelDialog(getResources().getString(R.string.private_key_alert_dialog_tip1));
            return;
        }

        if (!RegexUtil.isPasswordExact(Password1)) {
//                    MyToast(getResources().getString(R.string.password_regex));
            showCancelDialog(getResources().getString(R.string.password_regex));
            return;
        }

        if (!Password1.equals(Password2)) {
//                    MyToast(getResources().getString(R.string.the_passwords_you_entered_do_not_match));
            showCancelDialog(getResources().getString(R.string.the_passwords_you_entered_do_not_match));
            return;
        }


        if (!pk_text.matches("[a-z\\d]{64}")) {
//                    MyToast(getResources().getString(R.string.mnemonic_alert_dialog_tip2));
            showCancelDialog(getResources().getString(R.string.private_key_alert_dialog_tip2));
            return;
        }

        rxImportStep1(Password1, PasswordHit, new OnCheckECKeyPairListener() {
            @Override
            public ECKeyPair checkECKeyPairMethod() {

                return wManager.generateECKeyPairByPK(pk_text, Password1);
            }

            @Override
            public String alertWrongTip() {
                return getResources().getString(R.string.private_key_alert_dialog_tip2);
            }
        });
    }


    @Override
    public void excuteByScanResult(String result) {
        mEd_private_key.setText(result);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sFragment = null;
    }
}