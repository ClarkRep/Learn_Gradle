package com.example.lib_a;

import android.content.Context;
import android.widget.Toast;

/**
 * Description
 * Company Beijing icourt
 *
 * @author zhaodanyang  E-mail:zhaodanyang@icourt.cc
 *         date createTime：2018/2/8
 *         version
 */
public class ALibUtils {

    public static void showToast(Context context) {
        Toast.makeText(context, "我是 A 包中的方法", Toast.LENGTH_SHORT).show();
    }

}
