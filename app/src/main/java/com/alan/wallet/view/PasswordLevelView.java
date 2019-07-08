package com.alan.wallet.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.alan.wallet.R;


/**
 * 显示密码的强度等级的控件。
 * Created by jiangfei on 2016/10/21.
 */
public class PasswordLevelView extends View {

    // 文字尺寸
    private float mTextWidth;
    private float mTextHeight;

    private float mSingleCharWidth;

    // 文字和图形的间距
    private int mPaddingText = 0;
    private int mPaddingBlock=2;
    // 文字大小
    private float mTextSize = 50F;

    // 当前密级强度
    private Level mCurLevel;

    // 默认情况下的密级颜色
    private int defaultColor = Color.argb(255, 220, 220, 220);

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static String danger=null;
    private static String low=null;
    private static String mid=null;
    private static String strong=null;
    public enum Level {
        DANGER(danger, Color.parseColor("#FC3403"), 0), LOW(low,Color.parseColor("#149EFF"), 1), MID(mid, Color.parseColor("#149EFF"), 2), STRONG(strong,Color.parseColor("#149EFF"), 3);
        String mStrLevel;
        int mLevelResColor;
        int mIndex;

        Level(String levelText, int levelResColor, int index) {
            mStrLevel = levelText;
            mLevelResColor = levelResColor;
            mIndex = index;
        }

        public int getColor(){
            return mLevelResColor;
        }
    }

    public PasswordLevelView(Context context) {
        this(context, null);
    }

    public PasswordLevelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordLevelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 从xml读取自定义属性
        TypedArray typedArray =
                context.getTheme().obtainStyledAttributes(attrs, R.styleable.PasswordLevelView, defStyleAttr, 0);
        mPaddingText = typedArray.getDimensionPixelSize(R.styleable.PasswordLevelView_text_padding_level, mPaddingText);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.PasswordLevelView_text_size_level, (int)mTextSize);
//        Log.e("ccm","mTextSize:"+mTextSize);

        Resources resources = context.getResources();
        danger=resources.getString(R.string.pwd_level0);
        low=resources.getString(R.string.pwd_level1);
        mid=resources.getString(R.string.pwd_level2);
        strong=resources.getString(R.string.pwd_level3);

        calculateTextWidth();
    }

    private void calculateTextWidth() {

        /*mTextSize=this.getResources().getDimension(R.dimen.x17);
        Log.v("ccm","mTextSize:"+this.getResources().getDimension(R.dimen.x17));*/

        // 测量文字宽高，这里最多就2个字:风险
        mPaint.setTextSize(mTextSize);
        Rect rect = new Rect();
        mPaint.getTextBounds(Level.STRONG.mStrLevel, 0, Level.STRONG.mStrLevel.length(), rect);
        mTextWidth = rect.width();
        mTextHeight = rect.height();

        mPaint.getTextBounds("A",0,1,rect);
        mSingleCharWidth=rect.width();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth;
        int measuredHeight;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        measuredWidth = widthMode == MeasureSpec.EXACTLY ? widthSize : getMeasuredWidth();
        measuredHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : getMeasuredHeight();

        if (measuredHeight <= 0) {
            measuredHeight = (int) (getPaddingTop() + getPaddingBottom() + mTextHeight);
//            measuredHeight = (int) (getPaddingTop() + getPaddingBottom() + mTextHeight+Level.values().length*mPaddingBlock);
        }
        if (measuredWidth <getPaddingLeft() + getPaddingRight()+mSingleCharWidth) {
            measuredWidth = (int) (getPaddingLeft() + getPaddingRight()+mSingleCharWidth);
        }
        // 固定套路，保存控件宽高值
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //计算密级色块区域的宽高
        float levelAreaHeight =
                getHeight() - getPaddingTop() - getPaddingBottom() - Level.values().length*mPaddingBlock;
        int levelNum = Level.values().length;
//        float eachLevelWidth = levelAreaWidth / levelNum;
        float eachLevelHeight = levelAreaHeight / levelNum;
        int startIndexOfDefaultColor = mCurLevel == null ? 0 : mCurLevel.mIndex + 1;
        float startRectLeft = getPaddingLeft();
        float startRectBottom =getHeight()-getPaddingBottom();
        // 画密级色块
        for (int i = 0; i < levelNum; i++) {
            if (i >= startIndexOfDefaultColor) {
                mPaint.setColor(defaultColor);
            } else {
                mPaint.setColor(Level.values()[mCurLevel == null ? 0 : mCurLevel.mIndex].mLevelResColor);
            }

            canvas.drawRect(
                    startRectLeft,
                    startRectBottom - eachLevelHeight,
                    startRectLeft+mSingleCharWidth,
                    startRectBottom,
                    mPaint);
//            startRectLeft += eachLevelWidth+mPaddingBlock;
            startRectBottom=startRectBottom-(eachLevelHeight+mPaddingBlock);
        }
        // 画色块后面的字
        String strText = mCurLevel != null ? mCurLevel.mStrLevel : "";
        mPaint.setColor("".equals(strText)?Color.BLACK:mCurLevel.mLevelResColor);
        mPaint.setTextSize(mTextSize);
        // 计算text的baseline
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        // baseline思路：先设为控件的水平中心线，再调整到文本区域的水平中心线上
        // 注意：fontMetrics的top/bottom/ascent/descent属性值，是基于baseline为原点的，上方为负值，下方为正！
        float viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        float baseLine =
                getPaddingTop()
                        + viewHeight / 2
                        + ((Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent)) / 2 - Math.abs(fontMetrics.descent));
    }

    /**
     * 显示level对应等级的色块
     *
     * @param level 密码密级
     */
    public void showLevel(Level level) {
        mCurLevel = level;
        invalidate();
    }

    /**
     * 可能返回null,即无密码
     * @return
     */
    public Level getLevel(){
        return mCurLevel;
    }

    public void setPaddingText(int px){
        mPaddingText=px;
    }

    public int getPaddingText(){
        return mPaddingText;
    }

    public void setPaddingBlock(int px){
        mPaddingBlock=px;
    }

    public int getPaddingBlock(){
        return mPaddingBlock;
    }

    public void setTextSize(float px){
        // 获得转换后的px值
        float pxDimension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px,this.getResources().getDisplayMetrics());
        mTextSize=pxDimension;
    }

    public float getTextSize(){
        return mTextSize;
    }



}
