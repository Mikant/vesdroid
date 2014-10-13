package com.app.vesdroid.Activities;

import java.util.UUID;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.ABMN;
import com.app.vesdroid.Model.Protocol;
import com.app.vesdroid.Model.ProtocolManager;
import com.app.vesdroid.Model.ProtocolType;
import com.app.vesdroid.Model.StepType;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class EditProtocolActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
	TextView textViewFirst;
	EditText editTextFirst;
	TextView textViewSecond;
	EditText editTextSecond;
	EditText editTextStep;
	EditText editTextSOC;
	
	RadioButton radioButtonSpacingCount;
	RadioButton radioButtonDeep;
	RadioButton radioButtonGeomStep;
	RadioButton radioButtonArithStep;
	
	Protocol currentProtocol;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_protocol_activity); 
		setTitle("Генерация протокола");
		
		initViews();
	}
	
	void initViews(){
		textViewFirst = (TextView) findViewById(R.id.textViewFirst);
		editTextFirst = (EditText)findViewById(R.id.editTextFirst);
		textViewSecond = (TextView) findViewById(R.id.textViewSecond);
		editTextSecond = (EditText) findViewById(R.id.editTextSecond);
		editTextStep = (EditText) findViewById(R.id.editTextStep);
		editTextSOC = (EditText) findViewById(R.id.editTextSOC);
		
		radioButtonSpacingCount = (RadioButton) findViewById(R.id.radioButtonSpacingCount);
		radioButtonSpacingCount.setOnCheckedChangeListener(this);
		radioButtonSpacingCount.setChecked(true);
		radioButtonDeep = (RadioButton) findViewById(R.id.radioButtonDeep);
		radioButtonDeep.setOnCheckedChangeListener(this);
		
		radioButtonGeomStep = (RadioButton) findViewById(R.id.radioButtonGeomStep);
		radioButtonGeomStep.setChecked(true);
		radioButtonArithStep = (RadioButton) findViewById(R.id.radioButtonArithStep);
		
		Button buttonMNList = (Button) findViewById(R.id.buttonMNList);
		buttonMNList.setOnClickListener(this);
		Button buttonGenerateProtocol = (Button) findViewById(R.id.buttonGenerateProtocol);
		buttonGenerateProtocol.setOnClickListener(this);
	}
	
	boolean generateProtocol(){
		//TODO генератор протока отсутствует
		try{
			float firstValue = Float.parseFloat(editTextFirst.getText().toString());
			float secondValue = Float.parseFloat(editTextSecond.getText().toString());
			float stepValue = Float.parseFloat(editTextStep.getText().toString());
			float spacingOverlapCount = Float.parseFloat(editTextSOC.getText().toString());
			float min = Float.parseFloat(editTextFirst.getText().toString());
			int cnt = Integer.parseInt(editTextSecond.getText().toString());
			float stp = Float.parseFloat(editTextStep.getText().toString());
			
			currentProtocol = new Protocol();
			for (int i = 0; i < cnt; i++) {
				float a = min + i * stp;
				a = Math.round(a * 1000) / 1000f;
				currentProtocol.getABMNs().add(new ABMN(-a,a,-1,1));
			}
			
			return true;
		}
		catch (Exception e){
			Toast.makeText(EditProtocolActivity.this, "Неверный входные параметры", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.radioButtonSpacingCount:
			if (isChecked){
				textViewFirst.setText(R.string.minSpacinString);
				textViewSecond.setText(R.string.spacingCountString);
				
				editTextFirst.setText("");
				editTextSecond.setText("");
				editTextSecond.setInputType(InputType.TYPE_CLASS_NUMBER);
			}
			break;

		case R.id.radioButtonDeep:
			if (isChecked){
				textViewFirst.setText(R.string.minDeepString);
				textViewSecond.setText(R.string.maxDeepString);
				
				editTextFirst.setText("");
				editTextSecond.setText("");
				editTextSecond.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			}
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.buttonMNList:
				// TODO где-то здесь правятся M и N
				break;
	
			case R.id.buttonGenerateProtocol:
				if (generateProtocol()){
					ProtocolManager.saveOrUpdateProtocol(EditProtocolActivity.this, currentProtocol, true);
					Intent intent = new Intent(this, ProtocolViewActivity.class);
					intent.putExtra(Stuff.PROTOCOL_ID, currentProtocol.getId().toString());
					startActivity(intent);
					finish();
				}
				break;
	
			default:
				break;
		}
		
	}
}
