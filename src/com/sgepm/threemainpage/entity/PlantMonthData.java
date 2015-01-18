package com.sgepm.threemainpage.entity;

import java.util.Vector;

public class PlantMonthData {

	String name;
	Vector<Float> data;
	
	public PlantMonthData(String name, Vector<Float> data) {
		super();
		this.name = name;
		this.data = data;
	}
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
