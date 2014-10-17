package com.app.vesdroid.Activities;

import java.util.ArrayList;

import com.app.vesdroid.Model.Record;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class DrawChart extends View {

	ArrayList<Record> records;
	ArrayList<Record> XYs;
	float minX;
	float minY;
	float maxX;
	float maxY;
	
	public DrawChart(Context context) {
		super(context);
		
		setBackgroundColor(Color.WHITE);
		
		records = new ArrayList<Record>();
		for (int i = 0; i < 10; i++) {
			Record record = new Record();
			record.setA((float) (3 * Math.pow(3, i)));
			record.setB((float) (5 * Math.pow(5, i)));
			records.add(record);
		}
		
		minX = (float) Math.log10(records.get(0).getA());
		minY = (float) Math.log10(records.get(0).getB());
		maxX = (float) Math.log10(records.get(records.size() - 1).getA());
		maxY = (float) Math.log10(records.get(records.size() - 1).getB());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		float min = Math.min(canvas.getClipBounds().right, canvas.getClipBounds().bottom);
		float max = Math.max(maxX, maxY);
		float coef = min / max;
		
		Path path = new Path();
		path.moveTo((float) Math.log10(records.get(0).getA()) * coef
				, (float) (maxY - Math.log10(records.get(0).getB())) * coef);
		path.addCircle((float) Math.log10(records.get(0).getA()) * coef
				, (float) (maxY - Math.log10(records.get(0).getB())) * coef
				, 4
				, Path.Direction.CW);
		for (int i = 1; i < records.size() - 1; i++) {
			path.lineTo((float) Math.log10(records.get(i).getA()) * coef
					, (float) (maxY - Math.log10(records.get(i).getB())) * coef);
			path.addCircle((float) Math.log10(records.get(i).getA()) * coef
					, (float) (maxY - Math.log10(records.get(i).getB())) * coef
					, 4
					, Path.Direction.CW);
		}
		
		Paint paintRed = new Paint();
		paintRed.setColor(Color.RED);
		paintRed.setStyle(Paint.Style.STROKE);
		paintRed.setStrokeWidth(2);
		
		canvas.drawPath(path, paintRed);
	}

}
