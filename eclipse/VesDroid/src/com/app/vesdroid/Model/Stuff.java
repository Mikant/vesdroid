package com.app.vesdroid.Model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

public final class Stuff {
	public static final int REQUEST_CODE_CREATE = 1;
	public static final int REQUEST_CODE_EDIT = 2;
	public static final int REQUEST_CODE_CHOOSE = 3;
	
	public static final int DIALOG_CREATE = 1;
	public static final int DIALOG_DELETE = 2;
	public static final int DIALOG_EDIT_NAME = 3;
	public static final int DIALOG_EDIT_COMMENT = 4;
	
	public static final String REQUEST_CODE = "REQUEST_CODE";
	public static final String PROTOCOL_ID = "PROTOCOL_ID";
	public static final String PROJECT_ID = "PROJECT_ID";
	public static final String PROFILE_ID = "PROFILE_ID";
	public static final String PICKET_ID = "PICKET_ID";
	public static final String ACTIVE_PROTOCOL_ID = "ACTIVE_PROTOCOL_ID";
	public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
	public static final String A = "A";
	public static final String B = "B";
	public static final String M = "M";
	public static final String N = "N";
	
	public static final float EPS = 1E-3f;
	
	public static float resistance(Record record){
		return resistance(record.getA(), record.getB(), record.getM(), record.getN());
	}
	
	public static float resistance(float A, float B, float M, float N){
		return (float) (2 * Math.PI / (1 / mod(A, M) - 1 / mod(A, N) - 1 / mod(B, M) + 1 / mod(B, N)));
	}
	
	public static float mod(float x, float y){
		return Math.abs(x - y);
	}
	
	public static float round(double value, int decimalPlaces) {
		int pow = (int)Math.pow(10, decimalPlaces);
		return Math.round(value * pow) / (float)pow;
	}
	
	public static boolean isFinite(float value) {
		return !Float.isNaN(value) && !Float.isInfinite(value);
	}
}
