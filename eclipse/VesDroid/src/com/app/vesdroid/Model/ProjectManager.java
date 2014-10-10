package com.app.vesdroid.Model;

import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProjectManager {
	private static ArrayList<Project> projects;
	
	public static ArrayList<Project> getAllProjects(Context context){
		if (projects != null) return projects;
		
		projects = new ArrayList<Project>();
		
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
		Cursor cursor = database.query(DataBaseHelper.PROJECT_TABLE, null, null, null, null, null, null);
		if (cursor.moveToFirst()){
			do{
				Project project = new Project();
				int columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_ID);
				project.setId(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_NAME);
				project.setName(cursor.getString(columnIndex));
				columnIndex = cursor.getColumnIndex(DataBaseHelper.COLUMN_COMMENT);
				project.setComment(cursor.getString(columnIndex));
				
				projects.add(project);
			} while (cursor.moveToNext());
		}
		cursor.close();
		database.close();
		dataBaseHelper.close();
		
		return projects;
	}
	
	public static Project getProjectById(Context context, String id){
		return getProjectById(context, UUID.fromString(id));
	}
	
	public static Project getProjectById(Context context, UUID id){
		ArrayList<Project> alp = getAllProjects(context);
		for (Project p : alp) {
			if (id.equals(p.getId())) return p;
		}
		
		return null;
	}
	
	public static boolean saveOrUpdateProject(Context context, Project project){
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
		SQLiteDatabase database = dataBaseHelper.getWritableDatabase();

		if (project.getId() == null){
			project.setId(UUID.randomUUID());
			database.insert(DataBaseHelper.PROJECT_TABLE, null, getContentValues(project));
		}
		else {
			database.update(DataBaseHelper.PROJECT_TABLE, getContentValues(project), "_id = ?", new String[]{project.getId().toString()});

		}
		database.close();
		dataBaseHelper.close();
		
		return true;
	}
	
	public static ContentValues getContentValues(Project project){
		ContentValues result = new ContentValues();
		result.put(DataBaseHelper.COLUMN_ID, project.getId().toString());
		result.put(DataBaseHelper.COLUMN_NAME, project.getName());
		result.put(DataBaseHelper.COLUMN_COMMENT, project.getComment());
		return result;
	}
}
