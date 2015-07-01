package com.sgepm.threemainpage.entity;

import java.util.Vector;

public class Plant60GenPower {

	String plantName = null;
	Vector<String> times = new Vector<String>();;
	Vector<Double> powers = new Vector<Double>();;
	String nickName = null;

	
	public Plant60GenPower(){}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	
	public Vector<String> getTimes() {
		return times;
	}
	public void setTimes(String times) {
		this.times.add(times);
	}

	public Vector<Double> getPowers() {
		return powers;
	}
	public void setPowers(Double powers) {

		this.powers.add(powers);
	}
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
