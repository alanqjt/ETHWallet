package com.alan.wallet.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.alan.wallet.R;


public class NDialog {

    private Context mContext;
    private View customView;

    public static final int CONFIRM = 100;//确认提示框

    private String positiveButtonText = "确定";
    private String negativeButtonText = "取消";

    private int positiveTextColor = Color.parseColor("#333333");
    private int negativeTextColor = Color.parseColor("#333333");
    private int buttonSize = 14;//button字体大小
    private boolean isButtonCenter;//button居中

    private String message;
    private int messageSize = 15;
    private int messageColor = Color.BLACK;
    private boolean isMessageCenter;//信息居中

    private String title;
    private int titleSize = 16;
    private int titleColor = Color.BLACK;
    private boolean isTitleCenter;//标题是否居中

    private boolean cancleable = true;



    public NDialog(Context context) {
        this.mContext = context;
    }

    private OnConfirmListener onConfirmListener;
    private OnInputListener onInputListener;

    private OnChoiceListener onChoiceListener;

    public AlertDialog create(View view) {
        int type;
            type=CONFIRM;

        customView=view;

        if (type == CONFIRM) {
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setTitle(title)
                .setView(customView)
                .setCancelable(cancleable)
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setButtonClick(type, 0);
                    }
                })
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setButtonClick(type, 1);
                    }
                }).create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                setDetails(alertDialog, type);
            }
        });

        return alertDialog;
    }

    private void setButtonClick(int type, int which) {
        if (type == CONFIRM && onConfirmListener != null) {
            onConfirmListener.onClick(which);
        }
    }


    private void setDetails(AlertDialog alertDialog, int type) {
        switch (type) {
            case CONFIRM:
                setButtonStyle(alertDialog);
                setTitleStyle(alertDialog);
                setMessageStyle(alertDialog);
                break;
        }

    }


    private void setButtonStyle(AlertDialog alertDialog) {
        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(negativeTextColor);
        positiveButton.setTextColor(positiveTextColor);

        negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonSize);
        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonSize);

        if (isButtonCenter) {
            Window window = alertDialog.getWindow();
            Button button3 = (Button) window.findViewById(android.R.id.button3);
            Space space = (Space) window.findViewById(R.id.spacer);

            button3.setVisibility(View.GONE);
            space.setVisibility(View.GONE);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            negativeButton.setLayoutParams(lp);
            positiveButton.setLayoutParams(lp);
        }
    }

    private void setTitleStyle(AlertDialog alertDialog) {
        Window window = alertDialog.getWindow();
        TextView titleView = (TextView) window.findViewById(R.id.alertTitle);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);
        titleView.setTextColor(titleColor);

        if (isTitleCenter) {
            ImageView imageView = (ImageView) window.findViewById(android.R.id.icon);
            imageView.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                titleView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    private void setMessageStyle(AlertDialog alertDialog) {
        Window window = alertDialog.getWindow();
        TextView messageView = (TextView) window.findViewById(android.R.id.message);

        messageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, messageSize);
        messageView.setTextColor(messageColor);

        if (isMessageCenter) {
            messageView.setGravity(Gravity.CENTER);
        }
    }

    private void popupSoftInput(final Context context, final EditText editText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, 0);
            }
        }, 10);
    }


    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface OnConfirmListener {
        void onClick(int which);//whichButton:0,1

    }

    public interface OnInputListener {
        void onClick(String inputText, int which);
    }

    public interface OnChoiceListener {
        void onClick(String item, int which);
    }


    /****************参数statr*****************/


    public NDialog setCancleable(boolean cancleable) {
        this.cancleable = cancleable;
        return this;
    }


    public NDialog setButtonCenter(boolean buttonCenter) {
        isButtonCenter = buttonCenter;
        return this;
    }


    public NDialog setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
        return this;
    }

    public NDialog setOnInputListener(OnInputListener onInputListener) {
        this.onInputListener = onInputListener;
        return this;
    }

    public NDialog setOnChoiceListener(OnChoiceListener onChoiceListener) {
        this.onChoiceListener = onChoiceListener;
        return this;
    }


    public NDialog setMessageCenter(boolean messageCenter) {
        isMessageCenter = messageCenter;
        return this;
    }


    public NDialog setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
        return this;
    }

    public NDialog setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
        return this;
    }

    public NDialog setPositiveTextColor(int positiveTextColor) {
        this.positiveTextColor = positiveTextColor;
        return this;
    }

    public NDialog setNegativeTextColor(int negativeTextColor) {
        this.negativeTextColor = negativeTextColor;
        return this;
    }

    public NDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public NDialog setMessageSize(int sp) {
        this.messageSize = sp;
        return this;
    }

    public NDialog setMessageColor(int messageColor) {
        this.messageColor = messageColor;
        return this;
    }

    public NDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public NDialog setTitleSize(int sp) {
        this.titleSize = sp;
        return this;
    }

    public NDialog setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public NDialog setTitleCenter(boolean titleCenter) {
        isTitleCenter = titleCenter;
        return this;
    }

    public NDialog setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
        return this;
    }


}
