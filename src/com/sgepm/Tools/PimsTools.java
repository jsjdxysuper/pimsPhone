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
	
	private static OracleConnection oc = new OracleConnection();
	public static HashMap<String,String>plantAbbrDic = null;
	
	public static void setPlantAbbrDic(){
		plantAbbrDic = new HashMap<String,String>();
		String sql = "select dcmc,yxtfbm from base_dcbm t";
		ResultSet rs=  oc.query(sql,null);
		try {
			while(rs.next()){
				plantAbbrDic.put(rs.getString("dcmc"), rs.getString("yxtfbm"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static HashMap<String,String> getPlantAbbrDic(){
		if(plantAbbrDic==null)
			setPlantAbbrDic();
		return plantAbbrDic;
	}
	/**
	 * 根据所传入的电厂用户、对应页面、图表名称获得机组列表
	 * @param plant 对应的电厂用户
	 * @param page 对应的页面
	 * @param graph 页面中的图表
	 * @return
	 */
	public static ArrayList<String> getGeneratorsList(String plant,String page,String graph){
		
		String plantListStr[];//折线图所要显示的电厂列表
		//电厂的所配置的机组的集合(配置在pimsphone.properties文件中）
		ArrayList<String> generatorList        = new ArrayList<String>();
		
		String plantDotStr = plant+"."+page+"."+graph;
		String plantsStr = properties.getString(plantDotStr+".plants");
		plantListStr = plantsStr.split(",");

		//获得每个电厂所配置的机组列表		
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
