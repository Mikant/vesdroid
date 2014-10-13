package com.app.vesdroid.Model;

import android.app.Activity;

public final class Util {
	public static <T> T findViewByIdSafe(Activity a, int id) {
		Object o = a.findViewById(id);
		return o == null ? null : (T)o; 
	}
}
