package com.sgepm.threemainpage.entity;

import java.util.Vector;

/**
 * nameΪ�·�����
 * dataΪһ��series�����ݣ�Ϊһ���¸����糧�ķ�����
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
