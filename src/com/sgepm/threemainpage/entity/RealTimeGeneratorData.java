package com.sgepm.threemainpage.entity;

import java.util.Vector;

public class RealTimeGeneratorData {

	private String name;
	private Vector<Float> data = new Vector<Float>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Vector<Float> getData() {
		return data;
	}
	public void setData(Vector<Float> data) {
		this.data = data;
	}
	
}
