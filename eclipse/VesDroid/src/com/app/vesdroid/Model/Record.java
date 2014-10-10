package com.app.vesdroid.Model;

import java.util.UUID;

public class Record {
	private UUID picketId;
	private float deltaU;
	private float I;
	private long dateTimeMillis;
	private float a;
	private float b;
	private float m;
	private float n;
	
	public float getA(){
		return a;
	}
	public void setA(float a){
		this.a = a;
	}
	public float getB() {
		return b;
	}
	public void setB(float b) {
		this.b = b;
	}
	public float getM() {
		return m;
	}
	public void setM(float m) {
		this.m = m;
	}
	public float getN() {
		return n;
	}
	public void setN(float n) {
		this.n = n;
	}
	
	public UUID getPicketId() {
		return picketId;
	}

	public void setPicketId(UUID picketId) {
		this.picketId = picketId;
	}
	
	public void setPicketId(String picketId) {
		this.picketId = UUID.fromString(picketId);
	}

	public float getDeltaU() {
		return deltaU;
	}

	public void setDeltaU(float deltaU) {
		this.deltaU = deltaU;
	}

	public float getI() {
		return I;
	}

	public void setI(float i) {
		I = i;
	}

	public long getDateTimeMillis() {
		return dateTimeMillis;
	}

	public void setDateTimeMillis(long dateTimeMillis) {
		this.dateTimeMillis = dateTimeMillis;
	}
}
