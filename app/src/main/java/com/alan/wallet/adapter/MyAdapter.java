package com.alan.wallet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.alan.wallet.fragment.MnemonicViewPagerFragment;
import com.alan.wallet.fragment.OfficialViewPagerFragment;
import com.alan.wallet.fragment.PrivateKeyViewPagerFragment;


public class MyAdapter extends FragmentPagerAdapter {
    private String[] title;

    public MyAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyAdapter(FragmentManager fm, String[] title) {
        super(fm);
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fg = null;
        switch (position) {
            case 0:
                fg = MnemonicViewPagerFragment.newInstance(position, android.R.color.holo_orange_dark);
                break;
            case 1:
                fg = OfficialViewPagerFragment.newInstance();
                break;
            case 2:
                fg = PrivateKeyViewPagerFragment.newInstance();
                break;
            default:
                break;
        }
        Log.v("ccm", "fg:" + fg);
        return fg;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

}