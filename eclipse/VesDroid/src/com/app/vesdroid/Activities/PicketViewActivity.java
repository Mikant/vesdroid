package com.app.vesdroid.Activities;

import java.util.ArrayList;

import org.achartengine.GraphicalView;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.ChartManager;
import com.app.vesdroid.Model.Record;
import com.app.vesdroid.Model.RecordManager;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class PicketViewActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picket_view_activity);
		
		String picketId = getIntent().getExtras().getString(Stuff.PICKET_ID);
		
		ArrayList<Record> dbRecords = RecordManager.getAllRecordsForPicket(this, picketId);
		ArrayList<Record> actualRecords = RecordManager.filterActualRecords(dbRecords);
		GraphicalView graphicalView = new ChartManager(this, actualRecords).getChartView();
		
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		
		linearLayout.addView(graphicalView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Изменить цвет");

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0){
			// TODO edit color
		}

		return super.onOptionsItemSelected(item);
	}

}
