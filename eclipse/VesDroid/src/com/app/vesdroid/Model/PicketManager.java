package com.app.vesdroid.Model;

import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PicketManager {
	private static UUID currentProfileId;
	private static ArrayList<Picket> pickets;
	
	public static ArrayList<Picket> getAllPicketsForProfile(Context context, UUID profiletId){
		if (currentProfileId != null
				&& currentProfileId.equals(profiletId) 
				&& pickets != null) 
			return pickets;
		
		currentProfileId = profiletId;
		pickets = new ArrayList<Picket>();
		
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query(DataBaseHelper.PICKET_TABLE, null, DataBaseHelper.COLUMN_PROFILE_ID + " = ?", new String[]{ profiletId.toString() }, null, null, null);
		if (cursor.moveToFirst()){
			do{
				Picket picket = new Picket();
				int columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ID);
				picket.setId(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_NAME);
				picket.setName(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_COMMENT);
				picket.setComment(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_PROFILE_ID);
				picket.setProfileId(cursor.getString(columnIndex));
				
				pickets.add(picket);
			} while (cursor.moveToNext());
		}
		cursor.close();
		database.close();
		dataBaseHelper.close();
		
		return pickets;
	}
	
	public static ArrayList<Picket> getAllPicketsForProfile(Context context, String profileId){
		return getAllPicketsForProfile(context, UUID.fromString(profileId));
	}
	
	public static Picket getPicketById(Context context, UUID picketId, UUID profileId){
		ArrayList<Picket> alp = getAllPicketsForProfile(context, profileId);
		for (Picket p : alp) {
			if (picketId.equals(p.getId())) return p;
		}
		
		return null;
	}
	
	public static Picket getPicketById(Context context, String picketId, String profileId){
		return getPicketById(context, UUID.fromString(picketId), UUID.fromString(profileId));
	}
	
	public static boolean saveOrUpdatePicket(Context context, Picket picket){
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getWritableDatabase();

		if (picket.getId() == null){
			picket.setId(UUID.randomUUID());
			database.insert(DataBaseHelper.PICKET_TABLE, null, getContentValues(picket));
			
			if (currentProfileId != null && currentProfileId.equals(picket.getProfileId()))
				pickets.add(picket);
		}
		else {
			database.update(DataBaseHelper.PICKET_TABLE, getContentValues(picket), "_id = ?", new String[]{picket.getId().toString()});

		}
		database.close();
		dataBaseHelper.close();
		
		return true;
	}
	
	public static ContentValues getContentValues(Picket picket){
		ContentValues result = new ContentValues();
		result.put(DataBaseHelper.COLUMN_ID, picket.getId().toString());
		result.put(DataBaseHelper.COLUMN_NAME, picket.getName());
		result.put(DataBaseHelper.COLUMN_COMMENT, picket.getComment());
		result.put(DataBaseHelper.COLUMN_PROFILE_ID, picket.getProfileId().toString());
		return result;
	}
}
