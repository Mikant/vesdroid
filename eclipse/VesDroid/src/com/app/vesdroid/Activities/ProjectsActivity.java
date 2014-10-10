package com.app.vesdroid.Activities;

import java.util.ArrayList;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.Project;
import com.app.vesdroid.Model.ProjectManager;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ProjectsActivity extends Activity {

	ArrayList<Project> projects;
	ArrayAdapter<Project> arrayAdapter;
	ListView listViewProjects;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.projects_activity); 
		setTitle("Проекты");
		
		Button buttonCreateProject = (Button) findViewById(R.id.buttonCreateProject);
		buttonCreateProject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProjectsActivity.this, EditProjectActivity.class);
				intent.putExtra(Stuff.PROJECT_ID, "");
				startActivity(intent);
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
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		projects = ProjectManager.getAllProjects(this);
		arrayAdapter = new ArrayAdapter<Project>(this, android.R.layout.simple_list_item_1, projects);
		listViewProjects.setAdapter(arrayAdapter);
	}
}
