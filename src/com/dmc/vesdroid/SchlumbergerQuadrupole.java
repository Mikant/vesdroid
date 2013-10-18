package com.dmc.vesdroid;

public class SchlumbergerQuadrupole extends Quadrupole {

	public SchlumbergerQuadrupole(float ab, float mn) throws Exception {
		super(-ab / 2, ab / 2, -mn / 2, mn / 2);
	}

	public float getAB() {
		return Math.abs(getA() - getB());
	}
	
	public float getMN() {
		return Math.abs(getM() - getN());
	}
	
	public float getABHalved() {
		return getAB() / 2;
	}
}
