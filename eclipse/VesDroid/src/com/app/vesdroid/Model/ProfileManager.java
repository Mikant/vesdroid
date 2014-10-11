package com.app.vesdroid.Model;

import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProfileManager {
	private static UUID currentProjectId;
	private static ArrayList<Profile> profiles;
	
	public static ArrayList<Profile> getAllProfilesForProject(Context context, UUID projectId){
		if (currentProjectId != null 
				&& currentProjectId.equals(projectId) 
				&& profiles != null) 
			return profiles;
		
		currentProjectId = projectId;
		profiles = new ArrayList<Profile>();
		
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query(DataBaseHelper.PROFILE_TABLE, null, DataBaseHelper.COLUMN_PROJECT_ID +  " = ?", new String[]{ projectId.toString() }, null, null, null);
		if (cursor.moveToFirst()){
			do{
				Profile profile = new Profile();
				int columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ID);
				profile.setId(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_NAME);
				profile.setName(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_COMMENT);
				profile.setComment(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_PROJECT_ID);
				profile.setProjectId(cursor.getString(columnIndex));
				
				profiles.add(profile);
			} while (cursor.moveToNext());
		}
		cursor.close();
		database.close();
		dataBaseHelper.close();
		
		return profiles;
	}
	
	public static ArrayList<Profile> getAllProfilesForProject(Context context, String projectId){
		return getAllProfilesForProject(context, UUID.fromString(projectId));
	}
	
	public static Profile getProfileById(Context context, UUID profileId, UUID projectId){
		ArrayList<Profile> alp = getAllProfilesForProject(context, projectId);
		for (Profile p : alp) {
			if (profileId.equals(p.getId())) return p;
		}
		
		return null;
	}
	
	public static Profile getProfileById(Context context, String profileId, String projectId){
		return getProfileById(context, UUID.fromString(profileId), UUID.fromString(projectId));
	}
	
	public static boolean saveOrUpdateProfile(Context context, Profile profile){
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getWritableDatabase();

		if (profile.getId() == null){
			profile.setId(UUID.randomUUID());
			database.insert(DataBaseHelper.PROFILE_TABLE, null, getContentValues(profile));
			
			if (currentProjectId != null && currentProjectId.equals(profile.getProjectId()))
				profiles.add(profile);
		}
		else {
			database.update(DataBaseHelper.PROFILE_TABLE, getContentValues(profile), "_id = ?", new String[]{profile.getId().toString()});

		}
		database.close();
		dataBaseHelper.close();
		
		return true;
	}
	
	public static ContentValues getContentValues(Profile profile){
		ContentValues result = new ContentValues();
		result.put(DataBaseHelper.COLUMN_ID, profile.getId().toString());
		result.put(DataBaseHelper.COLUMN_NAME, profile.getName());
		result.put(DataBaseHelper.COLUMN_COMMENT, profile.getComment());
		result.put(DataBaseHelper.COLUMN_PROJECT_ID, profile.getProjectId().toString());
		return result;
	}
}
