package com.app.vesdroid.Model;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NameCommentManager {
	private static UUID currentParentId;
	private static ArrayList<NameComment> nameComments;
	protected static String tableName;
	
	protected static ArrayList<NameComment> getAllObjects(Context context, UUID parentId){
		if (nameComments != null 
				&& (parentId == null || currentParentId.equals(parentId))) return nameComments;

		if (parentId != null) currentParentId = parentId;
		nameComments = new ArrayList<NameComment>();
		
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query(tableName, null, null, null, null, null, null);
		if (cursor.moveToFirst()){
			do{
				Project project = new Project();
				int columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ID);
				project.setId(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_NAME);
				project.setName(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_COMMENT);
				project.setComment(cursor.getString(columnIndex));
				attachData(cursor);
				
				//projects.add(project);
			} while (cursor.moveToNext());
		}
		cursor.close();
		database.close();
		dataBaseHelper.close();
		
		return null;//projects;
	}
	
	protected static void attachData(Cursor Cursor){
		
	}
	
	public static Project getProjectById(Context context, String id){
		return getProjectById(context, UUID.fromString(id));
	}
	
	public static Project getProjectById(Context context, UUID id){
		ArrayList<Project> alp = null;//getAllProjects(context);
		for (Project p : alp) {
			if (id.equals(p.getId())) return p;
		}
		
		return null;
	}
}
