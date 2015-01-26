package com.sgepm.threemainpage.entity;

import java.util.Vector;

/**
 * name为月份名字
 * data为一个series的数据，为一个月各个电厂的发电量
 * @author Administrator
 *
 */
public class PlantYearAccumulateDataSeries {

	private String name;
	private Vector<Float>data;

	
	public Vector<Float> getData() {
		return data;
	}
	public void setData(Vector<Float> data) {
		this.data = data;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	
}
