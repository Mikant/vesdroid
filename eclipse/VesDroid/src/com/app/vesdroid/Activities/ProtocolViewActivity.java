package com.app.vesdroid.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.ABMN;
import com.app.vesdroid.Model.ImportExport;
import com.app.vesdroid.Model.Protocol;
import com.app.vesdroid.Model.ProtocolManager;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.Toast;

public class ProtocolViewActivity extends Activity {

	  ArrayList<HashMap<String, Object>> data;
	  
	  HashMap<String, Object> currentHashMap;
	  
	  GridView gvMain;
	  SimpleAdapter adapter;

	  EditText editTextName;
	  EditText editTextAx;
	  EditText editTextBx;
	  EditText editTextMx;
	  EditText editTextNx;
	  
      Protocol currentProtocol;
      ABMN currentABMN;
      
      static final int DIALOG_NAME = 1;
      static final int DIALOG_ABMN = 2;
	  
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.protocol_view_activity);
	        
	        String id = getIntent().getExtras().getString(Stuff.PROTOCOL_ID);
	        currentProtocol = ProtocolManager.getProtocolById(this, id);
	        if (currentProtocol == null) finish();
	        
	        setTitle("Протокол: " + currentProtocol.getName());
	        
	        initListView();
	    }
	    
	    private void initListView(){
	        data = new ArrayList<HashMap<String, Object>>();
        	HashMap<String, Object> hashMap = new HashMap<String, Object>();
        	hashMap.put("Ax", "Ax");
        	hashMap.put("Bx", "Bx");
        	hashMap.put("Mx", "Mx");
        	hashMap.put("Nx", "Nx");
        	data.add(hashMap);
        	
        	int cnt = currentProtocol.getABMNs().size();
        	for (int i = 0; i < cnt; i++) {
        		ABMN abmn = currentProtocol.getABMNs().get(i);
        		
	        	hashMap = new HashMap<String, Object>();
	        	hashMap.put("Ax", abmn.getA());
	        	hashMap.put("Bx", abmn.getB());
	        	hashMap.put("Mx", abmn.getM());
	        	hashMap.put("Nx", abmn.getN());
	        	data.add(hashMap);
			}
        	
	        String[] from = {"Ax", "Bx", "Mx", "Nx"};
	        int[] to = {R.id.textViewAx, R.id.textViewBx, R.id.textViewMx, R.id.textViewNx};
	        
	        adapter = new SimpleAdapter(this, data, R.layout.ax_bx_mx_nx_row, from, to);
	        
	        ListView listView = (ListView) findViewById(R.id.listViewABMNs);
	        listView.setAdapter(adapter);
	        listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					if (position == 0) ProtocolViewActivity.this.showDialog(DIALOG_NAME);
					else {
						currentHashMap = data.get(position);
						currentABMN = currentProtocol.getABMNs().get(position-1);
						ProtocolViewActivity.this.showDialog(DIALOG_ABMN);
					}
				}
			});
	    }
	    
	    @Override
	    protected Dialog onCreateDialog(int id) {
	        View view = getLayoutInflater().inflate(R.layout.name_ax_bx_mx_nx_edit, null);
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setView(view);
	        
	        if (id == DIALOG_NAME){
	        	LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layoutName);
	        	linearLayout.setVisibility(View.VISIBLE);
	        	TableLayout tableLayout = (TableLayout) view.findViewById(R.id.layoutABMN);
	        	tableLayout.setVisibility(View.GONE);
	        	
		        adb
		        .setTitle("Редактирование названия")
		        .setPositiveButton(R.string.saveString, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						currentProtocol.setName(editTextName.getText().toString());
						ProtocolManager.saveOrUpdateProtocol(ProtocolViewActivity.this, currentProtocol, false);
						setTitle("Протокол: " + currentProtocol.getName());
				    	
						dialog.cancel();
					}
				});
	        }
	        else if (id == DIALOG_ABMN){
	        	LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layoutName);
	        	linearLayout.setVisibility(View.GONE);
	        	TableLayout tableLayout = (TableLayout) view.findViewById(R.id.layoutABMN);
	        	tableLayout.setVisibility(View.VISIBLE);
	        	
		        adb
		        //.setTitle("Редактировать значения")
		        .setPositiveButton(R.string.saveString, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						currentABMN.setA(Float.parseFloat(editTextAx.getText().toString()));
						currentABMN.setB(Float.parseFloat(editTextBx.getText().toString()));
						currentABMN.setM(Float.parseFloat(editTextMx.getText().toString()));
						currentABMN.setN(Float.parseFloat(editTextNx.getText().toString()));
						currentHashMap.put("Ax", currentABMN.getA());
						currentHashMap.put("Bx", currentABMN.getB());
						currentHashMap.put("Mx", currentABMN.getM());
						currentHashMap.put("Nx", currentABMN.getN());
						adapter.notifyDataSetChanged();
				    	
						dialog.cancel();
					}
				});
	        }
	        
			adb.setNegativeButton(R.string.cancelString, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
	        
	        AlertDialog alertDialog = adb.create();
	        alertDialog.getWindow()
	        	.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	        
	        editTextAx = (EditText) view.findViewById(R.id.editTextAx);
	        editTextBx = (EditText) view.findViewById(R.id.editTextBx);
	        editTextMx = (EditText) view.findViewById(R.id.editTextMx);
	        editTextNx = (EditText) view.findViewById(R.id.editTextNx); 
	        editTextName = (EditText) view.findViewById(R.id.editTextName);
	        
	        return alertDialog;
	    }
	    
	    @Override
	    protected void onPrepareDialog(int id, Dialog dialog) {
	    	super.onPrepareDialog(id, dialog);
	    	
	    	if (id == DIALOG_NAME) editTextName.setText(currentProtocol.getName());
	    	else if (id == DIALOG_ABMN){
		    	editTextAx.setText(currentHashMap.get("Ax").toString());
		    	editTextBx.setText(currentHashMap.get("Bx").toString());
		    	editTextMx.setText(currentHashMap.get("Mx").toString());
		    	editTextNx.setText(currentHashMap.get("Nx").toString());
	    	}
	    }
	    
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {

	    	menu.add(0, 0, 0, "Экспорт");
	    	menu.add(0, 1, 1, "Выбрать как активный протокол");
	    	
	    	return super.onCreateOptionsMenu(menu);
	    }
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	if (item.getItemId() == 0) {
	    		ImportExport.exportProtocol(this,  currentProtocol);
	    	}
	    	else if (item.getItemId() == 1) {
	    		SharedPreferences sp = getSharedPreferences(Stuff.SHARED_PREFERENCES, MODE_PRIVATE);
	    		Editor e = sp.edit();
	    		e.putString(Stuff.ACTIVE_PROTOCOL_ID, currentProtocol.getId().toString());
	    		e.commit();
	    	}
	    	
	    	return super.onOptionsItemSelected(item);
	    }
}
