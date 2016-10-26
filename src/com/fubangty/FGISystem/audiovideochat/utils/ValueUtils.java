package com.fubangty.FGISystem.audiovideochat.utils;

import java.util.List;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * <font color="green">检查集合或输入的值是否为空</font>
 * @ClassName ValueUtils
 * @author Administrator
 * @date 2016年7月20日 上午11:24:02
 *
 * @version
 */
public class ValueUtils {

	public static boolean isListNotEmpty(List<?> noteList) {
		return null != noteList && noteList.size() > 0;
	}

	public static boolean isListEmpty(List<?> noteList) {
		return null == noteList || noteList.size() == 0;
	}

	public static boolean isStrEmpty(String value) {
		if (null == value || "".equals(value.trim())) {
			return true;
		}
		return false;
	}

	public static boolean isStrNotEmpty(String value) {
		return !isStrEmpty(value);
	}

	public static boolean isNotEmpty(Object object) {
		return null != object;
	}

	public static boolean isEmpty(Object object) {
		return null == object;
	}

	public static boolean isHasEmptyView(View... views) {
		for (View v : views) {
			if (!v.isShown()) {
				continue;
			}
			if (v instanceof EditText) {
				EditText et = (EditText) v;
				if (TextUtils.isEmpty(et.getText().toString().trim())) {
					return true;
				}
			} else if (v instanceof TextView) {
				TextView tv = (TextView) v;
				if (TextUtils.isEmpty(tv.getText().toString().trim())) {
					return true;
				}
			}
		}
		return false;
	}

	public static String bolean2String(boolean b) {
		return b ? "1" : "0";
	}
}
