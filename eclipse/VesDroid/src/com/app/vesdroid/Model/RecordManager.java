package com.app.vesdroid.Model;

import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RecordManager {
	private static UUID currentPicketId;
	private static ArrayList<Record> records;
	
	public static ArrayList<Record> getAllRecordsForPicket(Context context, UUID picketId){
		if (currentPicketId != null
				&& currentPicketId.equals(picketId) 
				&& records != null) return records;
		
		currentPicketId = picketId;
		records = new ArrayList<Record>();
		
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query(DataBaseHelper.RECORD_TABLE, null, null, null, null, null, null);
		if (cursor.moveToFirst()){
			do{
				Record record = new Record();
				int columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_PICKET_ID);
				record.setPicketId(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_DATE_TIME_MILLIS);
				record.setDateTimeMillis(cursor.getLong(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_A);
				record.setA(cursor.getFloat(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_B);
				record.setB(cursor.getFloat(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_M);
				record.setM(cursor.getFloat(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_N);
				record.setN(cursor.getFloat(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_DELTA_U);
				record.setDeltaU(cursor.getFloat(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_I);
				record.setI(cursor.getFloat(columnIndex));
				
				records.add(record);
			} while (cursor.moveToNext());
		}
		cursor.close();
		database.close();
		dataBaseHelper.close();
		
		return records;
	}
	
	public static ArrayList<Record> getAllRecordsForPicket(Context context, String picketId){
		return getAllRecordsForPicket(context, UUID.fromString(picketId));
	}
	
	public static boolean saveRecord(Context context, Record record, UUID picketId){
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
		database.insert(DataBaseHelper.RECORD_TABLE, null, getContentValues(record));
		database.close();
		dataBaseHelper.close();
		
		ArrayList<Record> alr = getAllRecordsForPicket(context, picketId);
		alr.add(record);
		
		return true;
	}
	
	public static boolean saveRecord(Context context, Record record, String picketId){
		return saveRecord(context, record, UUID.fromString(picketId));
	}

	public static ContentValues getContentValues(Record record){
		ContentValues result = new ContentValues();
		result.put(DataBaseHelper.COLUMN_PICKET_ID, record.getPicketId().toString());
		result.put(DataBaseHelper.COLUMN_DATE_TIME_MILLIS, record.getDateTimeMillis());
		result.put(DataBaseHelper.COLUMN_A, record.getA());
		result.put(DataBaseHelper.COLUMN_B, record.getB());
		result.put(DataBaseHelper.COLUMN_M, record.getM());
		result.put(DataBaseHelper.COLUMN_N, record.getN());
		result.put(DataBaseHelper.COLUMN_DELTA_U, record.getDeltaU());
		result.put(DataBaseHelper.COLUMN_I, record.getI());
		return result;
	}
}
