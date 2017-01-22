package com.a32.yuqu.utils;

import android.content.Context;
import android.widget.Toast;

import com.a32.yuqu.R;

/**
 * Created by 32 on 2017/1/3.
 */

public class ToastUtils {
    public static void showShort(Context context,String mstring){
        Toast.makeText(context, mstring, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context,String mstring){
        Toast.makeText(context,mstring,Toast.LENGTH_LONG).show();
    }
}
