package com.app.vesdroid.Activities;

import java.util.ArrayList;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.ABMN;
import com.app.vesdroid.Model.DataBaseHelper;
import com.app.vesdroid.Model.Project;
import com.app.vesdroid.Model.ProjectManager;
import com.app.vesdroid.Model.Protocol;
import com.app.vesdroid.Model.ProtocolManager;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;
import android.widget.Toast;

public class ProtocolsActivity extends Activity {

	ArrayAdapter<Protocol> arrayAdapter;
	DataBaseHelper dataBaseHelper;
	ArrayList<Protocol> protocols;
	Protocol currentProtocol;
	ListView listViewProtocols;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.protocols_activity);
		setTitle("Протоколы");
		
		Button buttonCreateProtocol = (Button) findViewById(R.id.buttonCreateProtocol);
		buttonCreateProtocol.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentProtocol = new Protocol();
				
				Intent intent = new Intent(ProtocolsActivity.this, EditProtocolActivity.class); 
				startActivity(intent);
			}
		});
		
		listViewProtocols = (ListView) findViewById(R.id.listViewProtocols);
		listViewProtocols.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				currentProtocol = arrayAdapter.getItem(position);
				Intent intent = new Intent(ProtocolsActivity.this, ProtocolViewActivity.class);
				intent.putExtra(Stuff.PROTOCOL_ID, currentProtocol.getId().toString());
				startActivity(intent);
			}
		});
		listViewProtocols.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				currentProtocol = arrayAdapter.getItem(pos);
				showDialog(Stuff.DIALOG_DELETE);
				return true;
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		try {
			protocols = ProtocolManager.getAllProtocols(this);
		} catch (Exception e) {
			protocols = new ArrayList<Protocol>();
			Toast.makeText(this, "Сбой при получении данных из базы", Toast.LENGTH_LONG).show();
		}
		arrayAdapter = new ArrayAdapter<Protocol>(this, android.R.layout.simple_list_item_1, protocols);
		listViewProtocols.setAdapter(arrayAdapter);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		if (id == Stuff.DIALOG_DELETE) {
			builder.setMessage("Удалить протокол?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ProtocolManager.deleteProtocolById(ProtocolsActivity.this, currentProtocol.getId());
						currentProtocol = null;
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
