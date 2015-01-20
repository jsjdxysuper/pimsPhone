package com.sgepm.threemainpage.entity;

import java.util.Vector;

public class PlantMonthAccumulateData {

	private String plantName;
	private Vector<Float> power= new Vector<Float>();
	private int size;
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	public Vector<Float> getPower() {
		return power;
	}
	public void setPower(Vector<Float> power) {
		this.power = power;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
		power.setSize(size);
		for(int i=0;i<size;i++){
			power.set(i, new Float(0));
		}
	}


	
}
