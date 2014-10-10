package com.app.vesdroid.Model;

import java.util.UUID;

public class ABMN {
	private UUID _id;
	private UUID protocolId;
	private float a;
	private float b;
	private float m;
	private float n;
	
	public UUID getId() {
		return _id;
	}
	public void setId(UUID id) {
		this._id = id;
	}
	public void setId(String id) {
		this._id = UUID.fromString(id);
	}
	
	public UUID getProtocolId() {
		return protocolId;
	}
	public void setProtocolId(UUID protocolId) {
		this.protocolId = protocolId;
	}
	
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
	
	public ABMN(){
	}
	
	public ABMN(float a, float b, float m, float n){
		this.a = a;
		this.b = b;
		this.m = m;
		this.n = n;
	}
}
