package core.utils

import android.content.Context
import android.widget.Toast


// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL
// Created by SINGHA80 on 2/9/2024.
/**
 * ToastUtils provides static functionalities of Toast api
 */
object ToastUtils {
    /**
     * @param context of the target class
     * @param msg string msg
     */
    fun showToast(context: Context?, msg: String) {
        context.apply {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}