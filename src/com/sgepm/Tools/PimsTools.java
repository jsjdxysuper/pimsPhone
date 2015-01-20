package com.sgepm.Tools;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Vector;

public class PimsTools {

	private static ResourceBundle properties = ResourceBundle.getBundle("pimsphone");
	/**
	 * ����������ĵ糧�û�����Ӧҳ�桢ͼ�����ƻ�û����б�
	 * @param plant ��Ӧ�ĵ糧�û�
	 * @param page ��Ӧ��ҳ��
	 * @param graph ҳ���е�ͼ��
	 * @return
	 */
	public static ArrayList<String> getGeneratorsList(String plant,String page,String graph){
		
		String plantListStr[];//����ͼ��Ҫ��ʾ�ĵ糧�б�
		//�糧�������õĻ���ļ���(������pimsphone.properties�ļ��У�
		ArrayList<String> generatorList        = new ArrayList<String>();
		
		String plantDotStr = plant+"."+page+"."+graph;
		String plantsStr = properties.getString(plantDotStr+".plants");
		plantListStr = plantsStr.split(",");

		//���ÿ���糧�����õĻ����б�		
		for(int i=0;i<plantListStr.length;i++){
				String generators = properties.getString(plantDotStr+"."+plantListStr[i]);
				
				String gArray[] = generators.split(",");
				for(int j=0;j<gArray.length;j++){
					generatorList.add(gArray[j]);
				}
		}
		
		return generatorList;
	}
	
	public static void main(String[] args) {
		ArrayList<String> t = getGeneratorsList("pims","plant","graph1");
		System.out.println(t);
	}
}
