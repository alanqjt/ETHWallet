package com.alan.wallet.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
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
import com.alan.wallet.utils.StrUtil;
import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.ethereum.keystore.CipherException;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.web3j.crypto.ECKeyPair;

import java.io.IOException;

public class OfficialViewPagerFragment extends BaseImportFragment {

    private int mTitle;
    private int mColor;
    private TextView mTextView;
    private LinearLayout mLinear;
    private EditText mEd_keystore_content,mEd_pwd;

    private CheckBox mFemaleGroupID;

    private static OfficialViewPagerFragment sFragment;

    public static OfficialViewPagerFragment newInstance(){
        if (sFragment ==null){
            OfficialViewPagerFragment fragment = new OfficialViewPagerFragment();
            sFragment =fragment;
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

        View view = inflater.inflate(R.layout.fragment_viewpager_official, container, false);
        mEd_keystore_content = (EditText) view.findViewById(R.id.ed_keystore_content);
        mEd_pwd = (EditText) view.findViewById(R.id.ed_pwd);
        mFemaleGroupID = (CheckBox) view.findViewById(R.id.femaleGroupID);
        mBtn_Import = (Button) view.findViewById(R.id.btn_Import);
        text_userAgreement = view.findViewById(R.id.text_userAgreement);

        final RxPermissions rxPermissions = new RxPermissions(this);

        mFemaleGroupID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Toast.makeText(getActivity(),"mFemaleGroupID:"+b,Toast.LENGTH_SHORT).show();
                if (b){
//                    mBtn_Import.setBackgroundResource(R.drawable.shape_btn_blue);
                    mBtn_Import.setEnabled(true);
                }else {
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
        String keystore_content = mEd_keystore_content.getText().toString();
        String Password1 = mEd_pwd.getText().toString();

        if (StrUtil.isNullOrEmpty(keystore_content)||StrUtil.isNullOrEmpty(Password1)) {
//                    MyToast(getResources().getString(R.string.mnemonic_alert_dialog_tip1));
            showCancelDialog(getResources().getString(R.string.official_alert_dialog_tip1));
            return;
        }
        rxImportStep1(Password1, "", new OnCheckECKeyPairListener() {
            @Override
            public ECKeyPair checkECKeyPairMethod() {
                ECKeyPair ecKeyPair=null;
                try {
                    try {
                        ecKeyPair = wManager.generateECKeyPairByKeyStore(keystore_content, Password1);
                    } catch (org.web3j.crypto.CipherException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CipherException e) {
                    e.printStackTrace();
                } catch (ValidationException e) {
                    e.printStackTrace();
                }

                return ecKeyPair;
            }

            @Override
            public String alertWrongTip() {
                return getResources().getString(R.string.official_alert_dialog_tip3);
            }
        });
    }


    @Override
    public void excuteByScanResult(String result) {
        mEd_keystore_content.setText(result);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sFragment =null;
    }
}