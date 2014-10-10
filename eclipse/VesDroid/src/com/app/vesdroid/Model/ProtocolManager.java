package com.app.vesdroid.Model;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProtocolManager {
	private static ArrayList<Protocol> protocols;
	
	public static ArrayList<Protocol> getAllProtocols(Context context){
		if (protocols != null ) return protocols;
		
		protocols = new ArrayList<Protocol>();
		
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
		Cursor cursor =  database.query(DataBaseHelper.PROTOCOL_TABLE, null, null, null, null, null, null);
		if (cursor.moveToFirst()){
			do {
				Protocol protocol = new Protocol();
				int columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ID);
				protocol.setId(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(dataBaseHelper.COLUMN_NAME);
				protocol.setName(cursor.getString(columnIndex));
				
				Cursor c = database.query(DataBaseHelper.ABMN_TABLE, null, "protocol_id = ?", new String[]{protocol.getId().toString()}, null, null, null);
				if (c.moveToFirst()){
					do{
						ABMN abmn = new ABMN(0,0,0,0);
						abmn.setProtocolId(protocol.getId());
						columnIndex = c.getColumnIndex(DataBaseHelper.COLUMN_ID);
						abmn.setId(c.getString(columnIndex));
						columnIndex = c.getColumnIndex(DataBaseHelper.COLUMN_A);
						abmn.setA(c.getFloat(columnIndex));
						columnIndex = c.getColumnIndex(DataBaseHelper.COLUMN_B);
						abmn.setB(c.getFloat(columnIndex));
						columnIndex = c.getColumnIndex(DataBaseHelper.COLUMN_M);
						abmn.setM(c.getFloat(columnIndex));
						columnIndex = c.getColumnIndex(DataBaseHelper.COLUMN_N);
						abmn.setN(c.getFloat(columnIndex));
						protocol.getABMNs().add(abmn);
					} while(c.moveToNext());
				}
				c.close();
				
				protocols.add(protocol);
			} while (cursor.moveToNext());
		}
		cursor.close();
		database.close();
		dataBaseHelper.close();
		
		return protocols;
	}
	
	public static Protocol getProtocolById(Context context, UUID id){
		ArrayList<Protocol> alp = getAllProtocols(context);
		for (Protocol protocol : alp) {
			if (id.equals(protocol.getId())) return protocol;
		}
		
		return null;
	}
	
	public static Protocol getProtocolById(Context context, String id){
		return getProtocolById(context, UUID.fromString(id));
	}
	
	public static boolean saveOrUpdateProtocol(Context context, Protocol protocol, boolean includeABMNs){
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getWritableDatabase();

		if (protocol.getId() == null){
			protocol.setId(UUID.randomUUID());
			database.insert(DataBaseHelper.PROTOCOL_TABLE, null, getContentValues(protocol));
			
			
			for (ABMN abmn : protocol.getABMNs()) {
				if (abmn.getId() == null){
					abmn.setId(UUID.randomUUID());
					abmn.setProtocolId(protocol.getId());
					database.insert(DataBaseHelper.ABMN_TABLE, null, getContentValues(abmn));
				}
				else{
					database.update(DataBaseHelper.ABMN_TABLE, getContentValues(abmn), "_id = ?", new String[]{abmn.getId().toString()});
				}
			}
			
			if (protocols != null)
				protocols.add(protocol);
		}
		else {
			database.update(DataBaseHelper.PROTOCOL_TABLE, getContentValues(protocol), "_id = ?", new String[]{protocol.getId().toString()});
			
			for (ABMN abmn : protocol.getABMNs()) {
				if (abmn.getId() == null){
					abmn.setId(UUID.randomUUID());
					abmn.setProtocolId(protocol.getId());
					database.insert(DataBaseHelper.ABMN_TABLE, null, getContentValues(abmn));
				}
				else{
					database.update(DataBaseHelper.ABMN_TABLE, getContentValues(abmn), "_id = ?", new String[]{abmn.getId().toString()});
				}
			}
		}
		database.close();
		dataBaseHelper.close();
		
		return true;
	}
	
	public static boolean updateABMNOnly(Context context, ABMN abmn){
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
		database.update(DataBaseHelper.ABMN_TABLE, getContentValues(abmn), "_id = ?", new String[]{abmn.getId().toString()});
		database.close();
		dataBaseHelper.close();

		return true;
	}
	
	public static ContentValues getContentValues(Protocol protocol){
		ContentValues result = new ContentValues();
		result.put(DataBaseHelper.COLUMN_ID, protocol.getId().toString());
		result.put(DataBaseHelper.COLUMN_NAME, protocol.getName());
		return result;
	}
	
	public static ContentValues getContentValues(ABMN abmn){
		ContentValues result = new ContentValues();
		result.put(DataBaseHelper.COLUMN_ID, abmn.getId().toString());
		result.put(DataBaseHelper.COLUMN_PROTOCOL_ID, abmn.getProtocolId().toString());
		result.put(DataBaseHelper.COLUMN_A, abmn.getA());
		result.put(DataBaseHelper.COLUMN_B, abmn.getB());
		result.put(DataBaseHelper.COLUMN_M, abmn.getM());
		result.put(DataBaseHelper.COLUMN_N, abmn.getN());
		return result;
	}
	
	public static Protocol getActiveProtocol(Activity activity){
		SharedPreferences sp = activity.getSharedPreferences(Stuff.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
		String id = sp.getString(Stuff.ACTIVE_PROTOCOL_ID, null);
		
		if (id == null) return null;;

		Protocol protocol = null;
		DataBaseHelper dataBaseHelper = new DataBaseHelper(activity);
		SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
		Cursor cursor =  database.query(DataBaseHelper.PROTOCOL_TABLE, null, "_id = ?", new String[]{id}, null, null, null);
		if (cursor.moveToFirst()){
			protocol = new Protocol();
			int columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ID);
			protocol.setId(cursor.getString(columnIndex));
			columnIndex = cursor.getColumnIndex(dataBaseHelper.COLUMN_NAME);
			protocol.setName(cursor.getString(columnIndex));
			
			Cursor c = database.query(DataBaseHelper.ABMN_TABLE, null, "protocol_id = ?", new String[]{protocol.getId().toString()}, null, null, null);
			if (c.moveToFirst()){
				do{
					ABMN abmn = new ABMN();
					abmn.setProtocolId(protocol.getId());
					columnIndex = c.getColumnIndex(DataBaseHelper.COLUMN_ID);
					abmn.setId(c.getString(columnIndex));
					columnIndex = c.getColumnIndex(DataBaseHelper.COLUMN_A);
					abmn.setA(c.getFloat(columnIndex));
					columnIndex = c.getColumnIndex(DataBaseHelper.COLUMN_B);
					abmn.setB(c.getFloat(columnIndex));
					columnIndex = c.getColumnIndex(DataBaseHelper.COLUMN_M);
					abmn.setM(c.getFloat(columnIndex));
					columnIndex = c.getColumnIndex(DataBaseHelper.COLUMN_N);
					abmn.setN(c.getFloat(columnIndex));
					protocol.getABMNs().add(abmn);
				} while(c.moveToNext());
			}
			c.close();
		}
		database.close();
		dataBaseHelper.close();
		
		return protocol;
	}
}
