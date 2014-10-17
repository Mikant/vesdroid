package com.app.vesdroid.Activities;

import java.util.ArrayList;
import java.util.Currency;
import java.util.zip.Inflater;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.Project;
import com.app.vesdroid.Model.ProjectManager;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ProjectsActivity extends Activity {

	ArrayList<Project> projects;
	Project currentProject;
	ArrayAdapter<Project> arrayAdapter;
	
	ListView listViewProjects;
	EditText editText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.projects_activity); 
		setTitle("Проекты");
		
		Button buttonCreateProject = (Button) findViewById(R.id.buttonCreateProject);
		buttonCreateProject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(Stuff.DIALOG_CREATE);
			}
		});
		
		
		listViewProjects = (ListView) findViewById(R.id.listViewProjects);
		
		listViewProjects.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				
				Project project = arrayAdapter.getItem(position);
				
				Intent intent = new Intent(ProjectsActivity.this, EditProjectActivity.class);
				intent.putExtra(Stuff.PROJECT_ID, project.getId().toString());
				startActivity(intent);
			}
		});
		listViewProjects.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				
				currentProject = projects.get(pos);
				showDialog(Stuff.DIALOG_DELETE);
				
				return true;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		projects = ProjectManager.getAllProjects(this);
		arrayAdapter = new ArrayAdapter<Project>(this, android.R.layout.simple_list_item_1, projects);
		listViewProjects.setAdapter(arrayAdapter);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		if (id == Stuff.DIALOG_CREATE){
			View view = getLayoutInflater().inflate(R.layout.edit_text, null);
			editText = (EditText) view.findViewById(R.id.editText);
			editText.setText("Название проекта");
			
			builder.setTitle("Создать проект")
				.setView(view)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Project project = new Project();
						project.setName(editText.getText().toString());
						ProjectManager.saveOrUpdateProject(ProjectsActivity.this, project);
						
						Intent intent = new Intent(ProjectsActivity.this, EditProjectActivity.class);
						intent.putExtra(Stuff.PROJECT_ID, project.getId().toString());
						startActivity(intent);
						
						dialog.cancel();
					}
				});
			
		}
		else if (id == Stuff.DIALOG_DELETE) {
			builder.setMessage("Удалить проект?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ProjectManager.deleteProjectById(ProjectsActivity.this, currentProject.getId());
						currentProject = null;
						arrayAdapter.notifyDataSetChanged();
						dialog.cancel();
					}
				});
		}
		
		builder.setNegativeButton(R.string.cancelString, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		return builder.create();
	}
}
