package com.sgepm.threemainpage.entity;

import java.util.Vector;

/**
 * ���Ա�ʾplantҳ����ʾһ���¸����糧��������ͼ����ͼ��ĳһ���¸����糧�����ۼƵ�����һ���糧һ������
 * Ҳ���ڱ�ʾ�糧��ʵʱ�й�
 * ��������ͼ����plantҳ����
 * nameΪ�糧����
 * dataΪ�糧���¶ȷ���������ʵʱ�й�
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
