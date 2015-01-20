package com.sgepm.threemainpage.entity;

import java.util.Vector;

/**
 * 用以表示plant页面第一个图（柱图：某一个月各个电厂的月累计电量）
 * @author Administrator
 *
 */
public class PlantMonthData {

	String name;
	Float data;
	
	public PlantMonthData(String name, Float data) {
		super();
		this.name = name;
		this.data = data;
	}

	public PlantMonthData() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getData() {
		return data;
	}

	public void setData(Float data) {
		this.data = data;
	}


}
