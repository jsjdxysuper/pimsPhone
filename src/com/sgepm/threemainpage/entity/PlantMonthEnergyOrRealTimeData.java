package com.sgepm.threemainpage.entity;

import java.util.Vector;

/**
 * 用以表示plant页面显示一个月各个电厂发电量柱图（柱图：某一个月各个电厂的月累计电量，一个电厂一个柱）
 * 也用于表示电厂的实时有功
 * 以上两个图都在plant页面中
 * name为电厂名称
 * data为电厂的月度发电量或者实时有功
 * @author Administrator
 *
 */
public class PlantMonthEnergyOrRealTimeData {

	String name;
	Float data;
	
	public PlantMonthEnergyOrRealTimeData(String name, Float data) {
		super();
		this.name = name;
		this.data = data;
	}

	public PlantMonthEnergyOrRealTimeData() {
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
