package com.app.vesdroid.Activities;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.ABMN;
import com.app.vesdroid.Model.PicketManager;
import com.app.vesdroid.Model.Protocol;
import com.app.vesdroid.Model.ProtocolManager;
import com.app.vesdroid.Model.Record;
import com.app.vesdroid.Model.RecordComparator;
import com.app.vesdroid.Model.RecordManager;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class WorkActivity extends Activity {

	TextView textViewAxValue;
	TextView textViewBxValue;
	TextView textViewMxValue;
	TextView textViewNxValue;
	EditText editTextVoltage;
	EditText editTextAmperage;
	
	ArrayList<Record> dbRecords;
	Protocol protocol;
	ArrayList<Record> workingRecords;
	int currentRecordNumber;
	Record currentRecord;
	String picketId;
	int direction = 1; //1 - прямой ход, -1 - обратный ход 
	
	static final int DIRECTION_MENU = 0;
	static final int GO_TO_START_MENU = 1;
	static final int GO_TO_FINISH_MENU = 2;
	static final int GO_TO_POSITION = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_activity);
		
		picketId = getIntent().getExtras().getString(Stuff.PICKET_ID);
		dbRecords = RecordManager.getAllRecordsForPicket(this, picketId);
		protocol = ProtocolManager.getActiveProtocol(this);

		// тут будет ХАОС из замеров
		workingRecords = new ArrayList<Record>();
		
		// выбираем "последние" замеры из базы
		for (int i = 0; i < dbRecords.size(); i++) {
			Record r = dbRecords.get(i);
			
			// ищем коллизию
			Record rr = null;
			for (int j = 0; j < workingRecords.size(); j++) {
				rr = workingRecords.get(j);
				if ((Stuff.mod(r.getA(), rr.getA()) > Stuff.EPS)
						|| (Stuff.mod(r.getB(), rr.getB()) > Stuff.EPS)
						|| (Stuff.mod(r.getM(), rr.getM()) > Stuff.EPS)
						|| (Stuff.mod(r.getN(), rr.getN()) > Stuff.EPS))
						rr = null;
				else break;
			}
			
			// если коллизия есть
			if (rr != null) {
				// если r новее rr, то заменяем rr на r
				if (r.getDateTimeMillis() > rr.getDateTimeMillis()){
					workingRecords.remove(rr);
					workingRecords.add(r);
				}
				else continue;
				
			}
			else workingRecords.add(r);
		}
		
		// дополняем недостающими "замерами" из протокола 
		ArrayList<ABMN> abmns = protocol.getABMNs();
		for (int i = 0; i < abmns.size(); i++) {
			ABMN a = abmns.get(i);
			
			Record rr = null;
			for (int j = 0; j < workingRecords.size(); j++) {
				rr = workingRecords.get(j);
				if ((Stuff.mod(a.getA(), rr.getA()) > Stuff.EPS)
						|| (Stuff.mod(a.getB(), rr.getB()) > Stuff.EPS)
						|| (Stuff.mod(a.getM(), rr.getM()) > Stuff.EPS)
						|| (Stuff.mod(a.getN(), rr.getN()) > Stuff.EPS))
						rr = null;
				else break;
			}
			if (rr == null){
				rr = new Record();
				rr.setA(a.getA());
				rr.setB(a.getB());
				rr.setM(a.getM());
				rr.setN(a.getN());
				
				workingRecords.add(rr);
			}
		}

		Collections.sort(workingRecords, new RecordComparator());
		
		TextView textViewProjectName = (TextView) findViewById(R.id.textViewProjectName);
		//textViewProjectName.setText(EditProjectActivity.currentProject.getName());
		
		TextView textViewProfileName = (TextView) findViewById(R.id.textViewProfileName);
		//textViewProfileName.setText(EditProfileActivity.currentProfile.getName());
		
		TextView textViewPicketLocation = (TextView) findViewById(R.id.textViewPicketLocation);
		//textViewPicketLocation.setText(EditPicketActivity.currentPicket.getName());
		
		textViewAxValue = (TextView) findViewById(R.id.textViewAxValue);
		textViewBxValue = (TextView) findViewById(R.id.textViewBxValue);
		textViewMxValue = (TextView) findViewById(R.id.textViewMxValue);
		textViewNxValue = (TextView) findViewById(R.id.textViewNxValue);
		
		editTextVoltage = (EditText) findViewById(R.id.editTextVoltage);
		editTextVoltage.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_UP){
					nextRecord();
					return true;
				}
					
				return false;
			}
		});
		
		editTextAmperage = (EditText) findViewById(R.id.editTextAmperage);
		
		Button buttonNextRecord = (Button) findViewById(R.id.buttonNextRecord);
		buttonNextRecord.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nextRecord();
			}
		});
		
		currentRecordNumber = 0;
		goToRecord(currentRecordNumber);
	}
	
	void goToRecord(int recordNumber){
		currentRecord = workingRecords.get(recordNumber); 
		textViewAxValue.setText("" + currentRecord.getA());
		textViewBxValue.setText("" + currentRecord.getB());
		textViewMxValue.setText("" + currentRecord.getM());
		textViewNxValue.setText("" + currentRecord.getN());
		
		editTextVoltage.setText("");
	}
	
	void nextRecord(){
		try {
			currentRecord.setDeltaU(Float.parseFloat(editTextVoltage.getText().toString()));
			currentRecord.setI(Float.parseFloat(editTextAmperage.getText().toString()));
			currentRecord.setDateTimeMillis(Calendar.getInstance().getTimeInMillis());
			currentRecord.setPicketId(picketId);
			boolean res = RecordManager.saveRecord(WorkActivity.this, currentRecord, picketId);
			if (!res){
				Toast.makeText(WorkActivity.this, "Не удалось сохранить замер в БД", Toast.LENGTH_LONG).show();
				return;
			}
			
			currentRecordNumber += direction;
			if (currentRecordNumber < 0 || currentRecordNumber >= workingRecords.size())
				Toast.makeText(this, "Пикет закончен", Toast.LENGTH_LONG).show();
			else
				goToRecord(currentRecordNumber);
		} catch (Exception e) {
			Toast.makeText(this,  "Неверные значения замеров", Toast.LENGTH_SHORT).show();
		}

	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, DIRECTION_MENU, 0, "Прямой ход");
    	menu.add(0, GO_TO_START_MENU, 1, "Перейти в начало");
    	menu.add(0, GO_TO_FINISH_MENU, 2, "Перейти в конец");
    	menu.add(0, GO_TO_POSITION, 3, "Перейти на позицию");
    	
    	return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
			case DIRECTION_MENU:
				direction = -1;
				break;
				
			case GO_TO_START_MENU:
				currentRecordNumber = 0;
				goToRecord(currentRecordNumber);
				break;
				
			case GO_TO_FINISH_MENU:
				currentRecordNumber = workingRecords.size() - 1;
				goToRecord(currentRecordNumber);
				break;
				
			case GO_TO_POSITION:
				// TODO переход на позицию
				break;

			default:
				break;
		}

    	return super.onOptionsItemSelected(item);
    }
}
