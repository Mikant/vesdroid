package com.app.vesdroid.Model;

import java.util.ArrayList;
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

import com.app.vesdroid.Activities.PicketViewActivity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ChartManager {
	ArrayList<Record> records;
	HashMap<Integer, ArrayList<Point>> S2XYs;
	HashMap<Integer, ArrayList<Record>> S2Records;
	GraphicalView graphicalView;
	Context context;
	boolean isPan;
	boolean isZoom;
	
	public ChartManager(Context context, ArrayList<Record> records) {
		this.context = context;
		
		generateGraphicalView(records);
	}
	
	public void generateGraphicalView(ArrayList<Record> records) {
		this.records = records;
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
		mRenderer.setClickEnabled(true);
		mRenderer.setPanEnabled(true);
		mRenderer.setZoomEnabled(true);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setXLabels(0);
		mRenderer.setYLabels(0);
		mRenderer.setYLabelsAlign(Align.RIGHT);
		mRenderer.setShowCustomTextGrid(true);
		mRenderer.setMargins(new int[]{10, 50, 0, 10});
		
		ArrayList<Float> abs = new ArrayList<Float>();
		ArrayList<Float> rs = new ArrayList<Float>();
		ArrayList<Float> mns = new ArrayList<Float>();
		int cnt = records.size();
		for (int i = 0; i < cnt; i++) {
			Record record = records.get(i);
			float ab = Stuff.mod(record.getA(), record.getB()) * 0.5f;
			float r = Stuff.resistance(record);
			float mn = Stuff.mod(record.getM(), record.getN());
			abs.add(ab);
			rs.add(r);
			mns.add(mn);
			
			float x = (float) Math.log10(ab);
			float y = (float) Math.log10(r);
			mRenderer.addXTextLabel(x, "" + ab);
			mRenderer.addYTextLabel(y, "" + r);
		}
		
		float max = Float.MIN_VALUE;
		S2XYs = new HashMap<Integer, ArrayList<Point>>();
		S2Records = new HashMap<Integer, ArrayList<Record>>();
		HashMap<Float, Integer> MN2S = new HashMap<Float, Integer>();
		for (int i = 0; i < cnt; i++) {
			float mn = mns.get(i);
			mn = Math.round(mn * 1000) / 1000f; // округляем до мм
			
			Integer s = MN2S.get(mn);
			XYSeries xySeries;
			if (s == null){
				xySeries = new XYSeries("|MN| = " + mn);
				dataset.addSeries(xySeries);
				s = dataset.getSeriesCount() - 1;
				MN2S.put(mn, s);
				S2XYs.put(s, new ArrayList<Point>());
				S2Records.put(s, new ArrayList<Record>());
				
				XYSeriesRenderer renderer = new XYSeriesRenderer();
				renderer.setLineWidth(2);
				renderer.setColor(Color.RED);
				renderer.setDisplayBoundingPoints(true);
				renderer.setPointStyle(PointStyle.CIRCLE);
				renderer.setPointStrokeWidth(3);
				mRenderer.addSeriesRenderer(renderer);
			}
			xySeries = dataset.getSeriesAt(s);

			float x = (float) Math.log10(abs.get(i));
			float y = (float) Math.log10(rs.get(i));
			xySeries.add(x, y);
			S2XYs.get(s).add(new Point(x, y));
			S2Records.get(s).add(records.get(i));
			
			if (x > max) max = x;
			if (y > max) max = y;
		}
		
		mRenderer.setXAxisMax(max);
		mRenderer.setYAxisMax(max);
		
		graphicalView = ChartFactory.getLineChartView(context, dataset, mRenderer);
		graphicalView.setBackgroundColor(Color.WHITE);
		
		graphicalView.addPanListener(new PanListener() {
			
			@Override
			public void panApplied() {
				isPan = true;
			}
		});
		graphicalView.addZoomListener(new ZoomListener() {

			@Override
			public void zoomReset() {
				isZoom = true;
			}
			
			@Override
			public void zoomApplied(ZoomEvent arg0) {
				isZoom = true;
			}
		}, true, true);
		
		graphicalView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isPan){
					isPan = false;
					return;
				}
				else if (isZoom){
					isZoom = false;
					return;
				}
				
				SeriesSelection seriesSelection = graphicalView.getCurrentSeriesAndPoint();
                double[] xy = graphicalView.toRealPoint(0);

                if (seriesSelection != null) {
                	findPoint(seriesSelection.getSeriesIndex(), (float)seriesSelection.getXValue(), (float)seriesSelection.getValue());
                }
			}
		});
	}
	
	private void findPoint(int ind, float x, float y){
		ArrayList<Point> XYs = S2XYs.get(ind);
		for (int i = 0; i < XYs.size(); i++) {
			Point abr = XYs.get(i);
			if (Stuff.mod(x, abr.getX()) < Stuff.EPS
					&& Stuff.mod(y, abr.getY()) < Stuff.EPS) {
				Record record = S2Records.get(ind).get(i);
				float ab = Stuff.mod(record.getA(), record.getB()) * 0.5f;
				float r = Stuff.resistance(record);
				Toast.makeText(context
						, "|AB|/2 = " + ab + ",\n" 
								+ "R = " + r + ",\n"
								+ "deltaU = " + record.getDeltaU() + ",\n"
								+ "I = " + record.getI()
						, Toast.LENGTH_LONG)
						.show();
				break;
			}
		}

	}
	
	public GraphicalView getChartView() {
		return graphicalView;
	}
}
