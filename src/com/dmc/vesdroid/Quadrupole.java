package com.dmc.vesdroid;

public class Quadrupole {
	private final float _a;
	private final float _b;
	private final float _m;
	private final float _n;
	
	public Quadrupole(float a, float b, float m, float n) throws IllegalArgumentException {
		if (a != a || b != b || m != m || n != n || a == b || m == n)
			throw new IllegalArgumentException();
			
		_a = a;
		_b = b;
		_m = m;
		_n = n;
	}

	public float getA() {
		return _a;
	}
	public float getB() {
		return _b;
	}	
	public float getM() {
		return _m;
	}	
	public float getN() {
		return _n;
	}	
	
	public float GetCoefficient() {
		float am = Math.abs(_a - _m);
		float an = Math.abs(_a - _n);
		float bm = Math.abs(_b - _m);
		float bn = Math.abs(_b - _n);
		
		return (float)(Math.PI / (1 / am - 1 / an - 1 / bm + 1 / bn));
	}
}
