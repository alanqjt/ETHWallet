package com.alan.wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.alan.wallet.R;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class ExportKeystoreQRFragment extends Fragment {

    private ImageView iv_keystore_qr;
    private Button btn_copy;
    private String mKeystore;

    private static ExportKeystoreQRFragment sFragment;

    public static ExportKeystoreQRFragment newInstance(String keystore) {
        if (sFragment == null) {
            ExportKeystoreQRFragment fragment = new ExportKeystoreQRFragment();
            sFragment = fragment;
            Bundle b = new Bundle();
            b.putString("keystore", keystore);
            fragment.setArguments(b);
        }
        return sFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mKeystore = args.getString("keystore");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_keystore_qr, container, false);
        iv_keystore_qr=(ImageView) view.findViewById(R.id.iv_keystore_qr);
        iv_keystore_qr.setImageBitmap(CodeUtils.createImage(mKeystore, 500, 500, null));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
