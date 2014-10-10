package com.app.vesdroid.Activities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.ImportExport;
import com.app.vesdroid.Model.Protocol;
import com.app.vesdroid.Model.ProtocolManager;
import com.app.vesdroid.Model.Stuff;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity); 
		
		Button buttonProjects = (Button) findViewById(R.id.buttonProjects);
		buttonProjects.setOnClickListener(this);
		
		Button buttonProtocols = (Button) findViewById(R.id.buttonProtocols);
		buttonProtocols.setOnClickListener(this);
		
		
		Button buttonExit = (Button) findViewById(R.id.buttonExit);
		buttonExit.setOnClickListener(this);
		
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action == Intent.ACTION_VIEW)
			ImportExport.importData(this, intent.getData());
		
		Protocol protocol = ProtocolManager.getActiveProtocol(this);
		if (protocol == null) Toast.makeText(this, "Выберите активный протокол", Toast.LENGTH_LONG).show();
		else {
			String title = getResources().getString(R.string.app_name) + "  Активный протокол: " + protocol.getName();
			setTitle(title);
		}
	}

	@Override
	public void onClick(View v) {
	    switch (v.getId()) {
		    case R.id.buttonProjects:
		      Intent intent = new Intent(this, ProjectsActivity.class);
		      startActivity(intent);
		      break;
		      
		    case R.id.buttonProtocols:
			      intent = new Intent(this, ProtocolsActivity.class);
			      startActivity(intent);
			      break;
			      
		    case R.id.buttonExit:
			      finish();
			      break;	
			      
		    default:
		      break;
	    }
	}

}
