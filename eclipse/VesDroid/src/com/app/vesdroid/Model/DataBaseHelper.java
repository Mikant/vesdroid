package com.app.vesdroid.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DataBaseHelper extends SQLiteAssetHelper {
	public static final String PROTOCOL_TABLE = "protocols";
	public static final String ABMN_TABLE = "abmns";
	public static final String PROJECT_TABLE = "projects";
	public static final String PROFILE_TABLE = "profiles";
	public static final String PICKET_TABLE = "pickets";
	public static final String RECORD_TABLE = "records";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PROTOCOL_ID = "protocol_id";
	public static final String COLUMN_PROJECT_ID = "project_id";
	public static final String COLUMN_PROFILE_ID = "profile_id";
	public static final String COLUMN_PICKET_ID = "picket_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_COMMENT = "comment";
	public static final String COLUMN_A = "a";
	public static final String COLUMN_B = "b";
	public static final String COLUMN_M = "m";
	public static final String COLUMN_N = "n";
	public static final String COLUMN_DELTA_U = "delta_u";
	public static final String COLUMN_I = "i";
	public static final String COLUMN_DATE_TIME_MILLIS = "date_time_millis";

    private static final String DATABASE_NAME = "ves_droid.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}