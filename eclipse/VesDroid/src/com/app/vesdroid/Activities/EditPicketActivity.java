package com.app.vesdroid.Activities;

import java.util.UUID;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.Picket;
import com.app.vesdroid.Model.PicketManager;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditPicketActivity extends Activity implements OnClickListener {

	String projectId;
	String profileId;
	String picketId;
	
	Picket picket;
	
	EditText editTextLocation;
	EditText editTextComment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_piket_activity); 
		
		
		editTextLocation = (EditText) findViewById(R.id.editTextLocation);
		editTextComment = (EditText) findViewById(R.id.editTextComment);
		
		Button buttonGoToRecords = (Button) findViewById(R.id.buttonGoToRecords);
		buttonGoToRecords.setOnClickListener(this);
		
		Button buttonSavePicket = (Button) findViewById(R.id.buttonSavePicket);
		buttonSavePicket.setOnClickListener(this);
		
		Button buttonCancelPicket = (Button) findViewById(R.id.buttonCancelPicket);
		buttonCancelPicket.setOnClickListener(this);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Bundle bundle = getIntent().getExtras();
		
		projectId = bundle.getString(Stuff.PROJECT_ID);
		profileId = bundle.getString(Stuff.PROFILE_ID);
		picketId = bundle.getString(Stuff.PICKET_ID);
		
		if (picketId == null){
			picket = new Picket();
			picket.setProfileId(profileId);
		}
		else {
			picket = PicketManager.getPicketById(this, picketId, profileId);
		}

		editTextLocation.setText(picket.getName());
		editTextComment.setText(picket.getComment());
		
		setTitle("Пикет: " + picket);
	}
	
	boolean saveData(){
		
		picket.setName(editTextLocation.getText().toString());
		picket.setComment(editTextComment.getText().toString());
		
		boolean result = PicketManager.saveOrUpdatePicket(EditPicketActivity.this, picket);
		if (!result) Toast.makeText(EditPicketActivity.this, "Не удалось сохранить пикет", Toast.LENGTH_LONG);
		else picketId = picket.getId().toString();
		
		return result;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.buttonGoToRecords:
				if (saveData()){
					Intent intent = new Intent(this, WorkActivity.class);
					intent.putExtra(Stuff.PROJECT_ID, projectId);
					intent.putExtra(Stuff.PROFILE_ID, profileId);
					intent.putExtra(Stuff.PICKET_ID, picketId);
					startActivity(intent);
				}
				break;
				
			case R.id.buttonSavePicket:
				if (saveData()) finish();
				break;
				
			case R.id.buttonCancelPicket:
				finish();
				break;
	
			default:
				break;
		}
	}
}
