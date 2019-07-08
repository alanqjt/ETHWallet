package com.alan.wallet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alan.wallet.fragment.ExportKeystoreFileFragment;
import com.alan.wallet.fragment.ExportKeystoreQRFragment;


public class ExportKeystoreAdapter extends FragmentPagerAdapter {
    private  String[] title;
    private  String keystore;

    public ExportKeystoreAdapter(FragmentManager fm) {
        super(fm);
    }
    public ExportKeystoreAdapter(FragmentManager fm , String keystore, String [] title) {
        super(fm);
        this.title = title;
        this.keystore=keystore;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fg=null;
        switch (position){
            case 0:
                fg= ExportKeystoreFileFragment.newInstance(keystore);
                break;
            case 1:
                fg= ExportKeystoreQRFragment.newInstance(keystore);
                break;

        }
        return fg;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return  title[position];
    }

}