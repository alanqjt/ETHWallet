package com.alan.wallet.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.alan.wallet.ethwallet.EthWalletManager;
import com.alan.wallet.utils.PasswordUtils;
import com.alan.wallet.utils.RegexUtil;
import com.alan.wallet.utils.StrUtil;
import com.alan.wallet.view.PasswordLevelView;
import com.quincysx.crypto.bip32.ValidationException;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.angmarch.views.NiceSpinner;
import org.web3j.crypto.ECKeyPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MnemonicViewPagerFragment extends BaseImportFragment {

    private int mTitle;
    private int mColor;
    private TextView mTextView;
    private LinearLayout mLinear;
    /**
     * spinner
     */
    private NiceSpinner mNiceSpinner;
    private EditText mEd_mnemonic, mEd_pwd, mEd_ensure_pwd, mEd_pwd_hint;
    private PasswordLevelView mPlv;
    private TextView mTv_pwd_tip;
    private CheckBox mFemaleGroupID;

    private List<String> spinner_dataset;
    private static MnemonicViewPagerFragment sFragment;

    TextView text_userAgreement;

    public static MnemonicViewPagerFragment newInstance(int title, int color) {
        if (sFragment == null) {
            MnemonicViewPagerFragment fragment = new MnemonicViewPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("title", title);
            bundle.putInt("color", color);
            fragment.setArguments(bundle);
            sFragment = fragment;
        }
        return sFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewpager_mnemonic, container, false);

        mNiceSpinner = (NiceSpinner) view.findViewById(R.id.nice_spinner);
        mEd_mnemonic = (EditText) view.findViewById(R.id.ed_mnemonic);
        mEd_pwd = (EditText) view.findViewById(R.id.ed_pwd);
        mEd_ensure_pwd = (EditText) view.findViewById(R.id.ed_ensure_pwd);
        mPlv = (PasswordLevelView) view.findViewById(R.id.pswd_level_view);
        mTv_pwd_tip = (TextView) view.findViewById(R.id.tv_pwd_tip);
        mEd_pwd_hint = (EditText) view.findViewById(R.id.ed_pwd_hint);
        mFemaleGroupID = (CheckBox) view.findViewById(R.id.femaleGroupID);
        mBtn_Import = (Button) view.findViewById(R.id.btn_Import);
        final RxPermissions rxPermissions = new RxPermissions(this);

        text_userAgreement = view.findViewById(R.id.text_userAgreement);

        mNiceSpinner.setPadding(0, 0, 0, 0);
//        mNiceSpinner.showArrow();
        spinner_dataset = new LinkedList<>(Arrays.asList("m/44'/60'0'/0/’"));
//        spinner_dataset = new LinkedList<>(Arrays.asList("m/44'/60'0'/0/’", "m/44'/60'0'/0/’", "Three"));
        mNiceSpinner.attachDataSource(spinner_dataset);

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

        String mnemonicWords = mEd_mnemonic.getText().toString();
        String Password1 = mEd_pwd.getText().toString();
        String Password2 = mEd_ensure_pwd.getText().toString();
        String PasswordHit = mEd_pwd_hint.getText().toString();


        if (StrUtil.isNullOrEmpty(mnemonicWords) || StrUtil.isNullOrEmpty(Password1)) {
            showCancelDialog(getResources().getString(R.string.mnemonic_alert_dialog_tip1));
            return;
        }
        if (!RegexUtil.isPasswordExact(Password1)) {
            showCancelDialog(getResources().getString(R.string.password_regex));
            return;
        }

        if (!Password1.equals(Password2)) {
            showCancelDialog(getResources().getString(R.string.the_passwords_you_entered_do_not_match));
            return;
        }


        String[] split = mnemonicWords.split(" ");
        if (split.length < 12) {
            showCancelDialog(getResources().getString(R.string.mnemonic_alert_dialog_tip2));
            return;
        }

        rxImportStep1(Password1, PasswordHit, mnemonicWords, new OnCheckECKeyPairListener() {
            @Override
            public ECKeyPair checkECKeyPairMethod() {
                wManager = new EthWalletManager();
                List<String> mnemonicWordsInAList = new ArrayList<>();
                for (String s : split) {
                    mnemonicWordsInAList.add(s);
                }
                ECKeyPair ecKeyPair = null;
                try {
                    ecKeyPair = wManager.generateECKeyPairByMnemonicWords(mnemonicWordsInAList, Password1);
                } catch (ValidationException e) {
                    e.printStackTrace();
                }
                return ecKeyPair;
            }

            @Override
            public String alertWrongTip() {
                return getResources().getString(R.string.mnemonic_alert_dialog_tip3);
            }
        });

    }


    @Override
    public void excuteByScanResult(String result) {
        mEd_mnemonic.setText(result);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sFragment = null;
    }
}