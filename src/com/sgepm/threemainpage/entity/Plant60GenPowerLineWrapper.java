package com.sgepm.threemainpage.entity;

import java.util.Vector;

public class Plant60GenPowerLineWrapper {

	private Vector<Plant60GenPower> lineData = null;
	private double maxRealtime;
	private double minRealtime;
	
	public double getMaxRealtime() {
		return maxRealtime;
	}
	public Vector<Plant60GenPower> getLineData() {
		return lineData;
	}
	public void setLineData(Vector<Plant60GenPower> lineData) {
		this.lineData = lineData;
	}
	public void setMaxRealtime(double maxRealtime) {
		this.maxRealtime = maxRealtime;
	}
	public double getMinRealtime() {
		return minRealtime;
	}
	public void setMinRealtime(double minRealtime) {
		this.minRealtime = minRealtime;
	}
}
