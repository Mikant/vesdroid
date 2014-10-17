package com.app.vesdroid.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		Cursor cursor = database.query(DataBaseHelper.RECORD_TABLE, null, DataBaseHelper.COLUMN_PICKET_ID + " = ?", new String[]{ picketId.toString() }, null, null, null);
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
	
	public static boolean deleteRecordsByPicketId(Context context, UUID id, SQLiteDatabase database){
		DataBaseHelper dataBaseHelper = null; 
		
		if (database == null) {
			dataBaseHelper = new DataBaseHelper(context);
			database = dataBaseHelper.getWritableDatabase();
		}
		
		database.delete(DataBaseHelper.RECORD_TABLE, DataBaseHelper.COLUMN_PICKET_ID + " = ?", new String[] {id.toString()});
		
		if (dataBaseHelper != null) {
			database.close();
			dataBaseHelper.close();
		}

		if (currentPicketId != null && currentPicketId.equals(id))
		{
			currentPicketId = null;
			records = null;
		}
		
		return true;
	}
	
	public static boolean deleteRecordsByPicketId(Context context, String id, SQLiteDatabase database){
		return deleteRecordsByPicketId(context, UUID.fromString(id), database);
	}
	
	public static int compareGeometry(Record o1, Record o2) {
    	Float ab21 = Stuff.mod(o1.getA(), o1.getB());
    	Float ab22 = Stuff.mod(o2.getA(), o2.getB());
    	
    	Float mn1 = Stuff.mod(o1.getM(), o1.getN()); 
    	Float mn2 = Stuff.mod(o2.getM(), o2.getN()); 
    	
    	return
    		Stuff.mod(ab21, ab22) > Stuff.EPS ? ab21.compareTo(ab22) :
    		Stuff.mod(mn1, mn2) < Stuff.EPS ? 0 : mn1.compareTo(mn2);
    }
	
	public static ArrayList<Record> filterActualRecords(ArrayList<Record> dbRecords){
		ArrayList<Record> actualRecords = new ArrayList<Record>();
		
		class RecordComparer implements Comparator<Record> {
		    @Override
		    public int compare(Record o1, Record o2) {
		    	Long l1 = o1.getDateTimeMillis(); 
		    	Long l2 = o2.getDateTimeMillis();
		    	
		    	int r = compareGeometry(o1, o2);
		    	
		        return r != 0 ? r :	l1.compareTo(l2);
		    }
		}

		int dbRecordsCount = dbRecords.size();
		if (dbRecordsCount > 0) {
			RecordComparer comparer = new RecordComparer();
			Collections.sort(dbRecords, comparer);
			
			Record prev = dbRecords.get(dbRecordsCount - 1);
			actualRecords.add(prev);
			for (int i = dbRecordsCount - 2; i >= 0; i--) {
				Record current = dbRecords.get(i);
				
				if (compareGeometry(prev, current) != 0)
					actualRecords.add(current);
					
				prev = current;
			}
			
			Collections.reverse(actualRecords);
		}
		
		return actualRecords;
	}
	
	public static void clear() {
		currentPicketId = null;
		records = null;
	}
}
