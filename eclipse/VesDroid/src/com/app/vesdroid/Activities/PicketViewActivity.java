package com.app.vesdroid.Activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.Point;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import com.app.vesdroid.R;
import com.app.vesdroid.Model.ABMN;
import com.app.vesdroid.Model.Record;
import com.app.vesdroid.Model.RecordManager;
import com.app.vesdroid.Model.Stuff;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class PicketViewActivity extends Activity {
	boolean mIsPan;
	boolean mIsZoom;
	HashMap<Integer, ArrayList<Point>> hmABR;
	HashMap<Integer, ArrayList<Point>> hmXY;
	
	private float[] xxx1;
	private float[] yyy1;
	private float tx1[];
	private float ty1[];
	
	private float[] xxx2;
	private float[] yyy2;
	private float tx2[];
	private float ty2[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picket_view_activity);
		
		String picketId = getIntent().getExtras().getString(Stuff.PICKET_ID);
		ArrayList<Record> dbRecords = RecordManager.getAllRecordsForPicket(this, picketId);
		ArrayList<Record> actualRecords = new ArrayList<Record>();
		
		// выбираем "последние" замеры из базы
		for (int i = 0; i < dbRecords.size(); i++) {
			Record r = dbRecords.get(i);
			
			// ищем коллизию
			Record rr = null;
			for (int j = 0; j < actualRecords.size(); j++) {
				rr = actualRecords.get(j);
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
					actualRecords.remove(rr);
					actualRecords.add(r);
				}
				else continue;
				
			}
			else actualRecords.add(r);
		}
		
		// тестовые данные
		actualRecords = new ArrayList<Record>();
		for (int i = 0; i < 10; i++) {
			Record r = new Record();
			r.setA(-3 * (i + 1));
			r.setB(3 * (i + 1));
			r.setM(-1);
			r.setN(1);
			actualRecords.add(r);
		}
		for (int i = 0; i < 10; i++) {
			Record r = new Record();
			r.setA(-3 * (i + 3));
			r.setB(3 * (i + 3));
			r.setM(-1.5f);
			r.setN(1.5f);
			actualRecords.add(r);
		}
		for (int i = 0; i < 10; i++) {
			Record r = new Record();
			r.setA(-3 * (i + 5));
			r.setB(3 * (i + 5));
			r.setM(-2);
			r.setN(2);
			actualRecords.add(r);
		}
		
		HashMap<Float, ArrayList<Record>> mns = new HashMap<Float, ArrayList<Record>>();
		for (int i = 0; i < actualRecords.size(); i++) {
			Record r = actualRecords.get(i);
			float mn = Stuff.mod(r.getM(), r.getN());
			
			ArrayList<Record> alr = mns.get(mn);
			if (alr == null){
				alr = new ArrayList<Record>();
				mns.put(mn, alr);
			}
			alr.add(r);
		}
		
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		mRenderer.setShowGrid(true);
		mRenderer.setClickEnabled(true);
		mRenderer.setPanEnabled(true);
		mRenderer.setZoomEnabled(true);
		mRenderer.setZoomButtonsVisible(true);
		
		hmABR = new HashMap<Integer, ArrayList<Point>>();
		hmXY = new HashMap<Integer, ArrayList<Point>>();
		int ind = 0;
		for (Entry<Float, ArrayList<Record>> entry : mns.entrySet()) {
			XYSeries xySeries = new XYSeries("|MN| = " + entry.getKey());
			int n = entry.getValue().size();
			xxx1 = new float[n];
			yyy1 = new float[n];
			tx1 = new float[n];
			ty1 = new float[n];
			
			ArrayList<Point> abrs = new ArrayList<Point>();
			ArrayList<Point> xys = new ArrayList<Point>();
			for (int i = 0; i < n; i++) {
				Record record = entry.getValue().get(i);
				float ab = Stuff.mod(record.getA(), record.getB()) * 0.5f;
				float r = Stuff.resistance(record);
				
				float x = (float) Math.log10(ab);
				float y = (float) Math.log10(r);
				xySeries.add(x, y);
				
				abrs.add(new Point(ab, r));
				xys.add(new Point(x, y));
				
				xxx1[i] = ab;
				yyy1[i] = r;
				tx1[i] = (float) Math.log10(ab);
				ty1[i] = (float) Math.log10(r);
			}
			dataset.addSeries(xySeries);
			hmABR.put(ind, abrs);
			hmXY.put(ind, xys);
			ind++;
			
			XYSeriesRenderer renderer = new XYSeriesRenderer();
			renderer.setLineWidth(2);
			renderer.setColor(Color.RED);
			renderer.setDisplayBoundingPoints(true);
			renderer.setPointStyle(PointStyle.CIRCLE);
			renderer.setPointStrokeWidth(3);
			mRenderer.addSeriesRenderer(renderer);
		
		}

		final GraphicalView chartView = ChartFactory.getLineChartView(this, dataset, mRenderer);
		chartView.addPanListener(new PanListener() {
			
			@Override
			public void panApplied() {
				mIsPan = true;
			}
		});
		chartView.addZoomListener(new ZoomListener() {
			
			@Override
			public void zoomReset() {
				mIsZoom = true;
			}
			
			@Override
			public void zoomApplied(ZoomEvent arg0) {
				mIsZoom = true;
			}
		}, true, true);
		
		chartView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mIsPan){
					mIsPan = false;
					return;
				}
				else if (mIsZoom){
					mIsZoom = false;
					return;
				}
				
				SeriesSelection seriesSelection = chartView.getCurrentSeriesAndPoint();
                double[] xy = chartView.toRealPoint(0);

                if (seriesSelection == null) {
                  Toast.makeText(PicketViewActivity.this, "No chart element was clicked", Toast.LENGTH_LONG)
                      .show();
                } else {
                	int i = seriesSelection.getPointIndex();
                  /*Toast.makeText(
                		  MainActivity.this,
                      "Chart element in series index " + seriesSelection.getSeriesIndex()
                          + " data point index " + seriesSelection.getPointIndex() + " was clicked"
                          + " closest point value X=" + seriesSelection.getXValue() + ", Y=" + seriesSelection.getValue()
                          + " clicked point value X=" + (float) xy[0] + ", Y=" + (float) xy[1], Toast.LENGTH_LONG).show();
                       */   
                  
                  //findPoint((float)xy[0], (float)xy[1], xxx, yyy);
                	findPoint(seriesSelection.getSeriesIndex(), (float)xy[0], (float)xy[1]);
                }
			}
		});
		
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		linearLayout.addView(chartView);
	}
	
	private void findPoint(int ind, float xx, float yy){
		ArrayList<Point> abrs = hmABR.get(ind);
		float x = (float) Math.pow(10, xx);
		float y = (float) Math.pow(10, yy);
		float minDX = Float.MAX_VALUE;
		float minDY = Float.MAX_VALUE;
		float resX = Float.NaN;
		float resY = Float.NaN;
		int resI = 0;
		for (int i = 0; i < abrs.size(); i++) {
			float dX = Math.abs(x - abrs.get(i).getX());
			float dY = Math.abs(y - abrs.get(i).getY());
			if (dX < minDX
					&& dY < minDY){
				resX = abrs.get(i).getX();
				resY = abrs.get(i).getY();
				minDX = dX;
				minDY = dY;
				resI = i;
			}
		}
		
		Toast.makeText(PicketViewActivity.this
				, "|AB|/2 = " + resX + ",\n" 
						+ "R = " + resY + ",\n"
						+ "U = " + 111 + ",\n"
						+ "I = " + 222
				, Toast.LENGTH_LONG)
				.show();
	}

}
