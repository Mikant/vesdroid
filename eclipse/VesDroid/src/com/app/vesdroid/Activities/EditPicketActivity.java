package com.app.vesdroid.Activities;

import java.util.ArrayList;
import java.util.UUID;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.Picket;
import com.app.vesdroid.Model.PicketManager;
import com.app.vesdroid.Model.Record;
import com.app.vesdroid.Model.RecordManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class EditPicketActivity extends Activity {

	String projectId;
	String profileId;
	String picketId;
	
	Picket picket;
	ArrayList<Record> records;
	ArrayAdapter<Record> arrayAdapter;

	TextView textViewNameValue;
	TextView textViewCommentValue;
	ListView listViewRecord;
	EditText editText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_piket_activity); 
		
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
				return true;
			}
		});
		textViewCommentValue = (TextView) findViewById(R.id.textViewCommentValue);
		
		listViewRecord = (ListView) findViewById(R.id.listViewRecord);
		listViewRecord.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				Intent intent = new Intent(EditPicketActivity.this, WorkActivity.class);
				intent.putExtra(Stuff.PROJECT_ID, projectId);
				intent.putExtra(Stuff.PROFILE_ID, profileId);
				intent.putExtra(Stuff.PICKET_ID, picketId);
				
				Record record = records.get(pos);
				intent.putExtra(Stuff.A, record.getA());
				intent.putExtra(Stuff.B, record.getB());
				intent.putExtra(Stuff.M, record.getM());
				intent.putExtra(Stuff.N, record.getN());
				
				startActivity(intent);
			}
		});
		
		Button buttonGoToRecords = (Button) findViewById(R.id.buttonGoToRecords);
		buttonGoToRecords.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EditPicketActivity.this, WorkActivity.class);
				intent.putExtra(Stuff.PROJECT_ID, projectId);
				intent.putExtra(Stuff.PROFILE_ID, profileId);
				intent.putExtra(Stuff.PICKET_ID, picketId);
				startActivity(intent);
			}
		});
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
		
		ArrayList<Record> dbRecords = RecordManager.getAllRecordsForPicket(this, picket.getId());
		records = RecordManager.filterActualRecords(dbRecords);
		arrayAdapter = new ArrayAdapter<Record>(this, android.R.layout.simple_list_item_1, records);
		listViewRecord.setAdapter(arrayAdapter);
		
		textViewNameValue.setText(picket.getName());
		textViewCommentValue.setText(picket.getComment());
		
		setTitle("Пикет: " + picket);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		if (id ==Stuff.DIALOG_EDIT_NAME)
			editText.setText(picket.getName());
		else if (id == Stuff.DIALOG_EDIT_COMMENT)
			editText.setText(picket.getComment());
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		if (id == Stuff.DIALOG_EDIT_NAME){
			View view = getLayoutInflater().inflate(R.layout.edit_text, null);
			editText = (EditText) view.findViewById(R.id.editText);
			
			builder.setTitle("Редактировать имя")
			.setView(view)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					picket.setName(editText.getText().toString());
					PicketManager.saveOrUpdatePicket(EditPicketActivity.this, picket);
					
					textViewNameValue.setText(picket.getName());
					setTitle("Пикет: " + picket);
					
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
					picket.setComment(editText.getText().toString());
					PicketManager.saveOrUpdatePicket(EditPicketActivity.this, picket);
					
					textViewCommentValue.setText(picket.getComment());
					
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Показать график");

		return super.onCreateOptionsMenu(menu);
	}
	
	private void putPicketActivityBaseExtras(Intent intent) {
		intent.putExtra(Stuff.PROJECT_ID, projectId);
		intent.putExtra(Stuff.PROFILE_ID, profileId);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			Intent intent = new Intent(EditPicketActivity.this,  PicketViewActivity.class);
			putPicketActivityBaseExtras(intent);
			
			intent.putExtra(Stuff.PICKET_ID, picket.getId().toString());
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}
}
