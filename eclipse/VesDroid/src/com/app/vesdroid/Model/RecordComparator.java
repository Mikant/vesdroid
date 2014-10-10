package com.app.vesdroid.Model;

import java.util.Comparator;

public class RecordComparator implements Comparator<Record>{

	@Override
	public int compare(Record lhs, Record rhs) {
		if (lhs.getA() - rhs.getA() < 0) return 1;
		if (lhs.getA() - rhs.getA() > Stuff.EPS) return -1;
		
		if (lhs.getB() - rhs.getB() < 0) return -1;
		if (lhs.getB() - rhs.getB() > Stuff.EPS) return 1;
		
		if (lhs.getM() - rhs.getM() < 0) return 1;
		if (lhs.getM() - rhs.getM() > Stuff.EPS) return -1;
		
		if (lhs.getN() - rhs.getN() < 0) return -1;
		if (lhs.getN() - rhs.getN() > Stuff.EPS) return 1;
		
		return 0;
	}
	
}
