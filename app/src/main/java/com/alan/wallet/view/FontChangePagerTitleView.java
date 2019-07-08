package com.alan.wallet.view;

import android.content.Context;
import android.graphics.Typeface;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

public class FontChangePagerTitleView extends SimplePagerTitleView {
    public FontChangePagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);
        setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);
        setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
    }
}
