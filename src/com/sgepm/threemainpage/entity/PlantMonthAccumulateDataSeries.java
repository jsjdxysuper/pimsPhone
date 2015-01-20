package com.sgepm.threemainpage.entity;

import java.util.Vector;

public class PlantMonthAccumulateDataSeries {

	private int name;
	private Vector<Float>data;

	
	public Vector<Float> getData() {
		return data;
	}
	public void setData(Vector<Float> data) {
		this.data = data;
	}
	public int getName() {
		return name;
	}
	public void setName(int name) {
		this.name = name;
	}

	
}
