package com.app.vesdroid.Activities;

import java.util.ArrayList;
import java.util.UUID;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.ImportExport;
import com.app.vesdroid.Model.Picket;
import com.app.vesdroid.Model.PicketManager;
import com.app.vesdroid.Model.Profile;
import com.app.vesdroid.Model.ProfileManager;
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
import android.view.View.OnFocusChangeListener;
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

public class EditProfileActivity extends Activity {

	TextView textViewNameValue;
	TextView textViewCommentValue;
	EditText editText;
	ListView listViewPicket;
	
	String projectId;
	
	Profile profile;
	ArrayList<Picket> pickets;
	ArrayAdapter<Picket> arrayAdapter;
	Picket currentPicket;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile_activity);
		
		TableRow tableRow = (TableRow) findViewById(R.id.tableRowName);
		tableRow.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				showDialog(Stuff.DIALOG_EDIT_NAME);
				return true;
			}
		});
		textViewNameValue = (TextView) findViewById(R.id.textViewNameValue);
		
		tableRow = (TableRow) findViewById(R.id.tableRowComment);
		tableRow.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				showDialog(Stuff.DIALOG_EDIT_COMMENT);
				return false;
			}
		});
		textViewCommentValue = (TextView) findViewById(R.id.textViewCommentValue);
		
		Button buttonCreatePicket = (Button) findViewById(R.id.buttonCreatePicket);
		buttonCreatePicket.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showDialog(Stuff.DIALOG_CREATE);
			}
		});
		
		listViewPicket = (ListView) findViewById(R.id.listViewPicket);
		listViewPicket.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				Picket picket = pickets.get(pos);
				Intent intent = new Intent(EditProfileActivity.this, EditPicketActivity.class);
				putPicketActivityBaseExtras(intent);
				intent.putExtra(Stuff.PICKET_ID, picket.getId().toString());
				startActivity(intent);
			}
		});
		listViewPicket.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				currentPicket = pickets.get(pos);
				showDialog(Stuff.DIALOG_DELETE);
				
				return true;
			}
		});
	}
	
	private void putPicketActivityBaseExtras(Intent intent) {
		intent.putExtra(Stuff.PROJECT_ID, projectId);
		intent.putExtra(Stuff.PROFILE_ID, profile.getId().toString());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Bundle bundle = getIntent().getExtras();
		
		projectId = bundle.getString(Stuff.PROJECT_ID);
		
		String profileId = bundle.getString(Stuff.PROFILE_ID);
		
		if (profileId == null){
			profile = new Profile();
			profile.setProjectId(projectId);
			pickets  = new ArrayList<Picket>();
		}
		else {
			profile = ProfileManager.getProfileById(this, profileId, projectId);
			pickets = PicketManager.getAllPicketsForProfile(this, profileId);
		}
		
		arrayAdapter = new ArrayAdapter<Picket>(this, android.R.layout.simple_list_item_1, pickets);
		listViewPicket.setAdapter(arrayAdapter);
		
		textViewNameValue.setText(profile.getName());
		textViewCommentValue.setText(profile.getComment());
		updateTitle();
	}
	
	private void updateTitle() {
		setTitle(getResources().getString(R.string.profile) + ": " + profile.getName());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Экспорт");

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0){
			ImportExport.exportProfile(this, profile);
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		if (id == Stuff.DIALOG_CREATE)
			editText.setText("Название пикета");
		else if (id ==Stuff.DIALOG_EDIT_NAME)
			editText.setText(profile.getName());
		else if (id == Stuff.DIALOG_EDIT_COMMENT)
			editText.setText(profile.getComment());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		if (id == Stuff.DIALOG_CREATE){
			View view = getLayoutInflater().inflate(R.layout.edit_text, null);
			editText = (EditText) view.findViewById(R.id.editText);
			
			builder.setTitle("Создать пикет")
				.setView(view)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Picket picket = new Picket();
						picket.setProfileId(profile.getId());
						picket.setName(editText.getText().toString());
						PicketManager.saveOrUpdatePicket(EditProfileActivity.this, picket);
						
						Intent intent = new Intent(EditProfileActivity.this, EditPicketActivity.class);
						intent.putExtra(Stuff.PROJECT_ID, projectId.toString());
						intent.putExtra(Stuff.PROFILE_ID, profile.getId().toString());
						intent.putExtra(Stuff.PICKET_ID, picket.getId().toString());
						startActivity(intent);
						
						dialog.cancel();
					}
				});
			
		}
		else if (id == Stuff.DIALOG_DELETE) {
			builder.setMessage("Удалить пикет?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						PicketManager.deletePicketById(EditProfileActivity.this, currentPicket.getId());
						currentPicket = null;
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
					profile.setName(editText.getText().toString());
					ProfileManager.saveOrUpdateProfile(EditProfileActivity.this, profile);
					
					textViewNameValue.setText(profile.getName());
					updateTitle();
					
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
					profile.setName(editText.getText().toString());
					ProfileManager.saveOrUpdateProfile(EditProfileActivity.this, profile);
					
					textViewCommentValue.setText(profile.getComment());
					
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
