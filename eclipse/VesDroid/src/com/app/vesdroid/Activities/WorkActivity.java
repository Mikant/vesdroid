package com.app.vesdroid.Activities;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.Point;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.ABMN;
import com.app.vesdroid.Model.ChartManager;
import com.app.vesdroid.Model.PicketManager;
import com.app.vesdroid.Model.ProfileManager;
import com.app.vesdroid.Model.ProjectManager;
import com.app.vesdroid.Model.Protocol;
import com.app.vesdroid.Model.ProtocolManager;
import com.app.vesdroid.Model.Record;
import com.app.vesdroid.Model.RecordComparator;
import com.app.vesdroid.Model.RecordManager;
import com.app.vesdroid.Model.Stuff;
import com.app.vesdroid.Model.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class WorkActivity extends Activity {

	TextView textViewABValue;
	TextView textViewMNValue;
	
	TextView textViewProjectName;
	TextView textViewProfileName;
	TextView textViewSiteName;
	
	EditText editTextAx;
	EditText editTextBx;
	EditText editTextMx;
	EditText editTextNx;
	EditText editTextVoltage;
	EditText editTextAmperage;
	LinearLayout linearLayout;
	GraphicalView chartView;
	
	ArrayList<Record> dbRecords;
	Protocol protocol;
	ArrayList<Record> workingRecords;
	ArrayAdapter<Record> arrayAdapter;
	int currentRecordNumber;
	Record currentRecord;
	int selectedRecordNumber = -1;
	float startX;
	float startY;
	
	String projectId;
	String profileId;
	String picketId;
	
	ABMN intentABMN;
	
	int direction = 1; //1 - прямой ход, -1 - обратный ход 
	
	static final int DIRECTION_MENU = 0;
	static final int GO_TO_START_MENU = 1;
	static final int GO_TO_FINISH_MENU = 2;
	static final int GO_TO_POSITION = 3;
	static final int SHOW_CHART = 4;
	static final int EDIT_ABMN = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_activity);
		
		Bundle bundle = getIntent().getExtras();
		projectId = bundle.getString(Stuff.PROJECT_ID);
		profileId = bundle.getString(Stuff.PROFILE_ID);
		picketId = bundle.getString(Stuff.PICKET_ID);
		
		
		dbRecords = RecordManager.getAllRecordsForPicket(this, picketId);
		protocol = ProtocolManager.getActiveProtocol(this);

		// тут будет ХАОС из замеров
		workingRecords = new ArrayList<Record>();
		
		initData();
		
		TableRow tableRow = (TableRow) findViewById(R.id.tableRowAB);
		tableRow.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				showDialog(EDIT_ABMN);
				return true;
			}
		});
		tableRow = (TableRow) findViewById(R.id.tableRowMN);
		tableRow.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				showDialog(EDIT_ABMN);
				return true;
			}
		});
		textViewABValue = (TextView) findViewById(R.id.textViewABValue);
		textViewMNValue = (TextView) findViewById(R.id.textViewMNValue);

		textViewProjectName = Util.findViewByIdSafe(this, R.id.textViewProjectName);
		textViewProfileName = Util.findViewByIdSafe(this, R.id.textViewProfileName);
		textViewSiteName = Util.findViewByIdSafe(this, R.id.textViewPicketName);
		
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
		
		Float a = bundle.getFloat(Stuff.A);
		Float b = bundle.getFloat(Stuff.B);
		Float m = bundle.getFloat(Stuff.M);
		Float n = bundle.getFloat(Stuff.N);
		if (a != null
				&& b!= null
				&& m != null
				&& n != null) {
			currentRecordNumber = findRecord(a, b, m, n);
		}
		else
			currentRecordNumber = 0;
		
		goToRecord(currentRecordNumber);
		
		updateSiteId();
	}
	

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			startX = event.getX();
			startY = event.getY();
		}
		else if (event.getAction() == MotionEvent.ACTION_UP
				&& Stuff.mod(startX, event.getX()) > 100
				&& Stuff.mod(startY, event.getY()) < 50) {
			showDialog(SHOW_CHART);
		}

		return super.dispatchTouchEvent(event);
	}
	
	int findRecord(float a, float b, float m, float n) {
		// TODO Auto-generated method stub
		Record record = null;
		for (int i = 0; i < workingRecords.size(); i++) {
			record = workingRecords.get(i);
			if (Stuff.mod(a, record.getA()) < Stuff.EPS
					&& Stuff.mod(b, record.getB()) < Stuff.EPS
					&& Stuff.mod(m, record.getM()) < Stuff.EPS
					&& Stuff.mod(n, record.getN()) < Stuff.EPS)
				return i;
		}
		
		return 0;
	}

	private void initData() {
		workingRecords = RecordManager.filterActualRecords(dbRecords);
		// TODO при случае переписать (запилить группировку)
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
			
			if (rr == null) {
				rr = new Record();
				rr.setA(a.getA());
				rr.setB(a.getB());
				rr.setM(a.getM());
				rr.setN(a.getN());
				
				workingRecords.add(rr);
			}
		}

		Collections.sort(workingRecords, new RecordComparator());
	}
	
	private void updateSiteId() {
		if (textViewProjectName != null)
			textViewProjectName.setText(ProjectManager.getProjectById(this, projectId).getName());
		if (textViewProfileName != null)
			textViewProfileName.setText(ProfileManager.getProfileById(this, profileId, projectId).getName());
		if (textViewSiteName != null)
			textViewSiteName.setText(PicketManager.getPicketById(this, picketId, profileId).getName());
	}
	
	void goToRecord(int recordNumber){
		currentRecord = workingRecords.get(recordNumber); 
		textViewABValue.setText("" + Stuff.mod(currentRecord.getA(), currentRecord.getB()) * 0.5f);
		textViewMNValue.setText("" + Stuff.mod(currentRecord.getM(), currentRecord.getN()));
		
		editTextVoltage.setText("" + currentRecord.getDeltaU());
		
		if (currentRecord.getDateTimeMillis() > 0)
			editTextAmperage.setText("" + currentRecord.getI());
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
				direction = -direction;
				
				if (direction > 0)
					item.setTitle("Прямой ход");
				else
					item.setTitle("Обратный ход");
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
				showDialog(GO_TO_POSITION);
				break;

			default:
				break;
		}

    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	super.onPrepareDialog(id, dialog);
    	
    	if (id == EDIT_ABMN) {
    		editTextAx.setText("" + currentRecord.getA());
    		editTextBx.setText("" + currentRecord.getB());
    		editTextMx.setText("" + currentRecord.getM());
	    	editTextNx.setText("" + currentRecord.getN());
    	}
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	if (id == GO_TO_POSITION){
    		arrayAdapter = new ArrayAdapter<Record>(this, android.R.layout.select_dialog_singlechoice, workingRecords);
    		
    		builder.setTitle("Выберите позицию")
    			.setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						selectedRecordNumber = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
						
					}
				})
	    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (selectedRecordNumber > 0)
						{
							currentRecordNumber = selectedRecordNumber;
							goToRecord(currentRecordNumber);

							dialog.cancel();
						}
					}
				});
    	}
    	else if (id == SHOW_CHART) {
    		builder.setView(new ChartManager(this, workingRecords).getChartView());
    	}
    	else if (id == EDIT_ABMN) {
    		View view = getLayoutInflater().inflate(R.layout.name_ax_bx_mx_nx_edit, null);
        	LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layoutName);
        	linearLayout.setVisibility(View.GONE);
        	TableLayout tableLayout = (TableLayout) view.findViewById(R.id.layoutABMN);
        	tableLayout.setVisibility(View.VISIBLE);
        	editTextAx = (EditText) view.findViewById(R.id.editTextAx);
        	editTextBx = (EditText) view.findViewById(R.id.editTextBx);
        	editTextMx = (EditText) view.findViewById(R.id.editTextMx);
        	editTextNx = (EditText) view.findViewById(R.id.editTextNx);
        	
    		builder.setView(view)
    			.setPositiveButton(R.string.saveString, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (currentRecord.getDateTimeMillis() > 0){
							Record record = new Record();
							record.setPicketId(currentRecord.getPicketId());
							record.setDeltaU(currentRecord.getDeltaU());
							record.setI(currentRecord.getI());
							
							record.setA(Float.parseFloat(editTextAx.getText().toString()));
							record.setB(Float.parseFloat(editTextBx.getText().toString()));
							record.setM(Float.parseFloat(editTextMx.getText().toString()));
							record.setN(Float.parseFloat(editTextNx.getText().toString()));
							
							Record greater = null;
							for (int i = 0; i < workingRecords.size(); i++) {
								if (RecordManager.compareGeometry(record, workingRecords.get(i)) < 0){
									greater = workingRecords.get(i);
									break;
								}
							}
							
							if(greater == null) {
								workingRecords.add(record);
								currentRecordNumber = workingRecords.indexOf(record);
							}
							else {
								currentRecordNumber = workingRecords.indexOf(greater);
								workingRecords.add(currentRecordNumber, record);
							}
							
							currentRecord = record;
						} else {
							currentRecord.setA(Float.parseFloat(editTextAx.getText().toString()));
							currentRecord.setB(Float.parseFloat(editTextBx.getText().toString()));
							currentRecord.setM(Float.parseFloat(editTextMx.getText().toString()));
							currentRecord.setN(Float.parseFloat(editTextNx.getText().toString()));
						}
						
						textViewABValue.setText("" + Stuff.mod(currentRecord.getA(), currentRecord.getB()) * 0.5f);
						textViewMNValue.setText("" + Stuff.mod(currentRecord.getM(), currentRecord.getN()));
				    	
						dialog.cancel();
					}
				})
				.setNegativeButton(R.string.cancelString, new DialogInterface.OnClickListener() {
	    			@Override
	    			public void onClick(DialogInterface dialog, int which) {
	    				dialog.cancel();
	    			}
				});
    		
        	AlertDialog alertDialog = builder.create();
	        alertDialog.getWindow()
        	.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	        return alertDialog;
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
