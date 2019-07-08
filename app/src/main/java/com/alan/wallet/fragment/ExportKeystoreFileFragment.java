package com.alan.wallet.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alan.wallet.R;


public class ExportKeystoreFileFragment extends Fragment {

    private TextView textView7;
    private Button btn_copy;
    private String mKeystore;

    private static ExportKeystoreFileFragment sFragment;

    public static ExportKeystoreFileFragment newInstance(String keystore) {
        if (sFragment == null) {
            ExportKeystoreFileFragment fragment = new ExportKeystoreFileFragment();
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

        View view = inflater.inflate(R.layout.fragment_keystore_file, container, false);
        textView7 = (TextView) view.findViewById(R.id.textView7);
        btn_copy = (Button) view.findViewById(R.id.btn_copy);

        textView7.setText(mKeystore);
        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("keystore", mKeystore);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);

                Toast.makeText(getActivity(),getResources().getString(R.string.copied),Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
