package com.app.vesdroid.Activities;

import java.util.ArrayList;
import java.util.UUID;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.ImportExport;
import com.app.vesdroid.Model.Profile;
import com.app.vesdroid.Model.ProfileManager;
import com.app.vesdroid.Model.Project;
import com.app.vesdroid.Model.ProjectManager;
import com.app.vesdroid.Model.Protocol;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class EditProjectActivity extends Activity {

	ListView listViewProfiles;
	TextView textViewNameValue;
	TextView textViewCommentValue;
	EditText editText;
	
	Project project;
	ArrayList<Profile> profiles;
	ArrayAdapter<Profile> arrayAdapter;
	Profile currentProfile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_project_activity);
		
		TableRow tableRow =(TableRow) findViewById(R.id.tableRowName);
		tableRow.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				showDialog(Stuff.DIALOG_EDIT_NAME);
				return true;
			}
		});
		textViewNameValue = (TextView) findViewById(R.id.textViewNameValue);
		
		tableRow =(TableRow) findViewById(R.id.tableRowComment);
		tableRow.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				showDialog(Stuff.DIALOG_EDIT_COMMENT);
				return true;
			}
		});
		textViewCommentValue = (TextView) findViewById(R.id.textViewCommentValue);
		
		Button buttonCreateProfile = (Button) findViewById(R.id.buttonCreateProfile);
		buttonCreateProfile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showDialog(Stuff.DIALOG_CREATE);
			}
		});
		
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
		listViewProfiles.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				currentProfile = profiles.get(pos);
				showDialog(Stuff.DIALOG_DELETE);
				
				return true;
			}
		});
	}

	void initData(){
		textViewNameValue.setText(project.getName());
		textViewCommentValue.setText(project.getComment());
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
		textViewNameValue.setText(project.getName());
		textViewCommentValue.setText(project.getComment());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Экспорт");

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0){
			ImportExport.exportProject(this, project);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		if (id == Stuff.DIALOG_CREATE)
			editText.setText("Название профиля");
		else if (id ==Stuff.DIALOG_EDIT_NAME)
			editText.setText(project.getName());
		else if (id == Stuff.DIALOG_EDIT_COMMENT)
			editText.setText(project.getComment());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		if (id == Stuff.DIALOG_CREATE){
			View view = getLayoutInflater().inflate(R.layout.edit_text, null);
			editText = (EditText) view.findViewById(R.id.editText);
			
			builder.setTitle("Создать профиль")
				.setView(view)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Profile profile = new Profile();
						profile.setProjectId(project.getId());
						profile.setName(editText.getText().toString());
						ProfileManager.saveOrUpdateProfile(EditProjectActivity.this, profile);
						
						Intent intent = new Intent(EditProjectActivity.this, EditProfileActivity.class);
						intent.putExtra(Stuff.PROJECT_ID, project.getId().toString());
						intent.putExtra(Stuff.PROFILE_ID, profile.getId().toString());
						startActivity(intent);
						
						dialog.cancel();
					}
				});
			
		}
		else if (id == Stuff.DIALOG_DELETE) {
			builder.setMessage("Удалить профиль?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ProfileManager.deleteProfileById(EditProjectActivity.this, currentProfile.getId());
						currentProfile = null;
						arrayAdapter.notifyDataSetChanged();
						dialog.cancel();
					}
				});
		}
		else if (id == Stuff.DIALOG_EDIT_NAME){
			View view = getLayoutInflater().inflate(R.layout.edit_text, null);
			editText = (EditText) view.findViewById(R.id.editText);
			
			builder.setTitle("Редактировать имя")
			.setView(view)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					project.setName(editText.getText().toString());
					ProjectManager.saveOrUpdateProject(EditProjectActivity.this, project);
					
					setTitle("Проект: " + project);
					textViewNameValue.setText(project.getName());
					
					dialog.cancel();
				}
			});
		}
		else if (id == Stuff.DIALOG_EDIT_COMMENT){
			View view = getLayoutInflater().inflate(R.layout.edit_text, null);
			editText = (EditText) view.findViewById(R.id.editText);
			
			builder.setTitle("Редактировать комментарий")
			.setView(view)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					project.setComment(editText.getText().toString());
					ProjectManager.saveOrUpdateProject(EditProjectActivity.this, project);
					
					textViewCommentValue.setText(project.getComment());
					
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
