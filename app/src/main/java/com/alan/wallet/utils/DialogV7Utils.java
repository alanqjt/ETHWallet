package com.alan.wallet.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alan.wallet.R;
import com.alan.wallet.dialog.NDialog;


public class DialogV7Utils {

    public interface OnNormalDialogSureListener {
        void sureCallBack();
        void cancelCallBack();
    }

    public interface OnEditTextDialogSureListener {
        void sureCallBack(String edittext);
        void cancelCallBack(String edittext);

    }

    /**
     * 这是兼容的 AlertDialog
     * 只有取消
     */
    public static void showCancelDialog(Activity activity, String message,String btnMsg) {
        showCancelDialog(activity,null,message,btnMsg);
    }

    /**
     * 这是兼容的 AlertDialog
     * 只有取消
     */
    public static void showCancelDialog(Activity activity, String title, String message,String btnMsg) {

        int color=activity.getResources().getColor(R.color.green);
        new NDialog(activity)
                .setTitle(title)
                .setMessage(message)
                .setNegativeTextColor(color)
                .setNegativeButtonText(btnMsg)
                .setPositiveButtonText(null)
                .create(null)
                .show();
        activity = null;


    }

    /**
     * 这是兼容的 AlertDialog
     * 有确定和取消
     */
    public static void showNormalDialog(Activity activity,String message, OnNormalDialogSureListener listener) {
        showNormalDialog(activity,null,message,listener);
    }


    /**
     * 这是兼容的 AlertDialog
     * 有确定和取消
     */
    public static void showNormalDialog(Activity activity, String title, String message, OnNormalDialogSureListener listener) {
        int color=activity.getResources().getColor(R.color.green);
        new NDialog(activity)

                .setTitle(title)
                .setMessage(message)
                .setOnConfirmListener(new NDialog.OnConfirmListener() {
                    @Override
                    public void onClick(int which) {
                        switch (which) {
                            case 0:
                                //N
                                listener.cancelCallBack();
                                break;
                            case 1:
                                //P
                                listener.sureCallBack();
                                break;
                        }
                    }
                })
                .setNegativeTextColor(color)
                .setPositiveTextColor(color)
                .setNegativeButtonText(activity.getResources().getString(R.string.cancel))
                .setPositiveButtonText(activity.getResources().getString(R.string.ok))
                .create(null)
                .show();
        activity = null;




    }


    public static void showEditTextDialog(Activity activity, String title, String message, OnEditTextDialogSureListener listener) {
        int color=activity.getResources().getColor(R.color.green);
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_edittext, (ViewGroup) activity.findViewById(R.id.dialog_edittext));
        EditText editText=(EditText) layout.findViewById(R.id.ed_pwd);
        new NDialog(activity)

                .setTitle(title)
                .setMessage(message)
                .setOnConfirmListener(new NDialog.OnConfirmListener() {
                    @Override
                    public void onClick(int which) {
                        String edit = editText.getText().toString();
                        switch (which) {
                            case 0:
                                //N
                                listener.cancelCallBack(edit);
                                break;
                            case 1:
                                //P
                                listener.sureCallBack(edit);
                                break;
                        }
                    }
                })
                .setNegativeTextColor(color)
                .setPositiveTextColor(color)
                .setNegativeButtonText(activity.getResources().getString(R.string.cancel))
                .setPositiveButtonText(activity.getResources().getString(R.string.ok))
                .create(layout)
                .show();
        activity = null;

    }
}
