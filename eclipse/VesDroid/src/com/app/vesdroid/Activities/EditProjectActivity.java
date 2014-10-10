package com.app.vesdroid.Activities;

import java.util.ArrayList;
import java.util.UUID;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.Profile;
import com.app.vesdroid.Model.ProfileManager;
import com.app.vesdroid.Model.Project;
import com.app.vesdroid.Model.ProjectManager;
import com.app.vesdroid.Model.Protocol;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class EditProjectActivity extends Activity implements OnClickListener {

	EditText editTextName;
	EditText editTextComment;
	ListView listViewProfiles;
	
	Project project;
	ArrayList<Profile> profiles;
	ArrayAdapter<Profile> arrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_project_activity);

		/*
		String id = getIntent().getExtras().getString(Stuff.PROJECT_ID);
		if (id.equals("")){
			project = new Project();
			profiles = new ArrayList<Profile>();
		}
		else {
			project = ProjectManager.getProjectById(this, id);
			profiles = ProfileManager.getAllProfilesForProject(this, id);
		}
		*/
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextComment = (EditText) findViewById(R.id.editTextComment);
		
		Button buttonCreateProfile = (Button) findViewById(R.id.buttonCreateProfile);
		buttonCreateProfile.setOnClickListener(this);
		
		Button buttonSaveProject = (Button) findViewById(R.id.buttonSaveProject);
		buttonSaveProject.setOnClickListener(this);
		
		Button buttonCancelProject = (Button) findViewById(R.id.buttonCancelProject);
		buttonCancelProject.setOnClickListener(this);
		
		//initData();
		

		listViewProfiles = (ListView) findViewById(R.id.listViewProfiles);

		listViewProfiles.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				Profile profile = profiles.get(pos);
				
				Intent intent = new Intent(EditProjectActivity.this, EditProfileActivity.class);
				intent.putExtra(Stuff.PROJECT_ID, project.getId().toString());
				intent.putExtra(Stuff.PROFILE_ID, profile.getId().toString());
				startActivity(intent);
			}
		});
	}

	void initData(){
		editTextName.setText(project.getName());
		editTextComment.setText(project.getComment());
	}
	
	boolean saveData(){
		project.setName(editTextName.getText().toString());
		project.setComment(editTextComment.getText().toString());
		
		boolean result = ProjectManager.saveOrUpdateProject(EditProjectActivity.this, project);
		if (!result) Toast.makeText(EditProjectActivity.this, "Не удалось сохранить проект", Toast.LENGTH_LONG).show();
		
		return result;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		String id = getIntent().getExtras().getString(Stuff.PROJECT_ID);
		if (id.equals("")){
			project = new Project();
			profiles = new ArrayList<Profile>();
		}
		else {
			project = ProjectManager.getProjectById(this, id);
			profiles = ProfileManager.getAllProfilesForProject(this, id);
		}
		
		arrayAdapter = new ArrayAdapter<Profile>(this, android.R.layout.simple_list_item_1, profiles);
		listViewProfiles.setAdapter(arrayAdapter);
		
		setTitle("Проект: " + project);
		editTextName.setText(project.getName());
		editTextComment.setText(project.getComment());
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.buttonCreateProfile:
				if (saveData()){
					Intent intent = new Intent(this, EditProfileActivity.class);
					intent.putExtra(Stuff.PROJECT_ID, project.getId().toString());
					startActivity(intent);
				}
				break;
				
			case R.id.buttonSaveProject:
				if (saveData()) finish();
				break;
				
			case R.id.buttonCancelProject:
				finish();
				break;
	
			default:
				break;
		}
	}
}
