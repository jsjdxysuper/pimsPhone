package com.sgepm.threemainpage.entity;

import java.util.Vector;

/**
 * ���Ա�ʾplantҳ���һ��ͼ����ͼ��ĳһ���¸����糧�����ۼƵ�����
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
