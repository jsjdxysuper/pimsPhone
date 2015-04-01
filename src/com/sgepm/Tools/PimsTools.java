package com.sgepm.Tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

public class PimsTools {

	private static ResourceBundle properties = ResourceBundle.getBundle("pimsphone");
	
	
	public static HashMap<String,String>plantAbbrDic = null;
	
	public static void setPlantAbbrDic(){
		plantAbbrDic = new HashMap<String,String>();
		String sql = "select dcmc,yxtfbm from base_dcbm t";
		OracleConnection oc = new OracleConnection();
		ResultSet rs=  oc.query(sql,null);
		try {
			while(rs.next()){
				plantAbbrDic.put(rs.getString("dcmc"), rs.getString("yxtfbm"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		oc.closeAll();
	}
	
	public static HashMap<String,String> getPlantAbbrDic(){
		if(plantAbbrDic==null)
			setPlantAbbrDic();
		return plantAbbrDic;
	}
	/**
	 * ����������ĵ糧�û�����Ӧҳ�桢ͼ�����ƻ�û����б�
	 * @param plant ��Ӧ�ĵ糧�û�
	 * @param page ��Ӧ��ҳ��
	 * @param graph ҳ���е�ͼ��
	 * @return
	 */
	public static ArrayList<String> getGeneratorsList(String plantName,String page,String graph){
		
		String plantListStr[];//����ͼ��Ҫ��ʾ�ĵ糧�б�
		//�糧�������õĻ���ļ���(������pimsphone.properties�ļ��У�
		ArrayList<String> generatorList        = new ArrayList<String>();
		
		String plantDotStr = plantName+"."+page+"."+graph;
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
	
	
	//select ssdc from pcadb.base_yhbm where yhid = '1'
	/**
	 * ��ȡ�糧�û���nickName
	 * nickName = yhid+ssdc
	 * @param yhid
	 * @return
	 */
	public static String getNickName(String yhid){

		String sql = "select ssdc from pcadb.base_yhbm where yhid = ?";
		OracleConnection oc = new OracleConnection();
		String paras[] = {yhid};
		ResultSet rs=  oc.query(sql,paras);
		try {
			while(rs.next()){
				yhid += rs.getString("ssdc");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		oc.closeAll();
		return yhid;
	}
	public static void main(String[] args) {
		ArrayList<String> t = getGeneratorsList("pims","plant","graph1");
		System.out.println(t);
	}
}
