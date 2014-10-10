package com.app.vesdroid.Activities;

import java.util.ArrayList;
import java.util.UUID;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.Picket;
import com.app.vesdroid.Model.PicketManager;
import com.app.vesdroid.Model.Profile;
import com.app.vesdroid.Model.ProfileManager;
import com.app.vesdroid.Model.Protocol;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.Toast;

public class EditProfileActivity extends Activity implements OnClickListener {

	EditText editTextName;
	EditText editTextComment;
	ListView listViewPicket;
	
	Profile profile;
	ArrayList<Picket> pickets;
	ArrayAdapter<Picket> arrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile_activity);
		
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextComment = (EditText) findViewById(R.id.editTextComment);
		
		Button buttonCreatePicket = (Button) findViewById(R.id.buttonCreatePicket);
		buttonCreatePicket.setOnClickListener(this);
		
		Button buttonSaveProfile = (Button) findViewById(R.id.buttonSaveProfile);
		buttonSaveProfile.setOnClickListener(this);
		
		Button buttonCancelProfile = (Button) findViewById(R.id.buttonCancelProfile);
		buttonCancelProfile.setOnClickListener(this);
		
		
		listViewPicket = (ListView) findViewById(R.id.listViewPicket);
		listViewPicket.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				Picket picket = pickets.get(pos);
				Intent intent = new Intent(EditProfileActivity.this, EditPicketActivity.class);
				intent.putExtra(Stuff.PROFILE_ID, profile.getId().toString());
				intent.putExtra(Stuff.PICKET_ID, picket.getId().toString());
				startActivity(intent);
			}
		});
		listViewPicket.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				Intent intent = new Intent(EditProfileActivity.this,  PicketViewActivity.class);
				Picket picket = pickets.get(pos);
				intent.putExtra(Stuff.PROFILE_ID, profile.getId().toString());
				intent.putExtra(Stuff.PICKET_ID, picket.getId().toString());
				startActivity(intent);
				return true;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Bundle bundle = getIntent().getExtras();
		String projectId = bundle.getString(Stuff.PROJECT_ID);
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
		
		editTextName.setText(profile.getName());
		editTextComment.setText(profile.getComment());
		setTitle("Профиль: " + profile.getName());
	}
	
	boolean saveData(){
		profile.setName(editTextName.getText().toString());
		profile.setComment(editTextComment.getText().toString());
		
		boolean result = ProfileManager.saveOrUpdateProfile(EditProfileActivity.this, profile);
		if (!result) Toast.makeText(EditProfileActivity.this, "Не удалось сохранить профиль", Toast.LENGTH_LONG).show();
		
		return result;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.buttonCreatePicket:
				if (saveData()){
					Intent intent = new Intent(this, EditPicketActivity.class);
					intent.putExtra(Stuff.PROFILE_ID, profile.getId().toString());
					startActivity(intent);
				}
				break;
				
			case R.id.buttonSaveProfile:
				if (saveData()) finish();
				break;
				
			case R.id.buttonCancelProfile:
				finish();
				break;
	
			default:
				break;
		}
	}
}
