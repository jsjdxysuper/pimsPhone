package com.sgepm.Tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONObject;

public class Yfhl_Ylyxs_Tool {
	private static JSONObject member;
	private static PreparedStatement pred;
	private static ResultSet testrs;
	private static ResultSet rs;
	private static ResultSetMetaData rsmd;
	private static int column;
	private static String[] hcmc = {"K","Z","T","Q","Y","H"};
	private static ArrayList<Double>  al;
	private static ArrayList<String>  plantal;
	private static ArrayList<Integer>  rankal;
	
	/**
	 * 
	 * @param con 数据库连接
	 * @param sql 查询字符串
	 * @param alkey 数值的键值
	 * @param rankalkey 排名的键值
	 * @param plantalkey 电厂名称的键值
	 * @return
	 */
	public static JSONObject getResult(Connection con,String sql,String alkey,String rankalkey,String plantalkey){
		
		try {			
			member = new JSONObject();
			pred = con.prepareStatement(sql);
			testrs = pred.executeQuery();
			
			al = new ArrayList<Double>();
			rankal = new ArrayList<Integer>();
			plantal = new ArrayList<String>();
			
			if(!(testrs.next())){
				for(int i = 0;i<6;i++){
					al.add(Double.valueOf("0"));
				}								
			}
			
			pred = con.prepareStatement(sql);
			rs = pred.executeQuery();
			rsmd = rs.getMetaData();
			column = rsmd.getColumnCount();			
			
			while (rs.next()) {				
				for (int j = 1; j <= column; j++) {
					String columnName = rsmd.getColumnLabel(j);
					Double a = rs.getDouble(columnName);
					al.add(a);
				}			
			}
			
			for(int i = 0;i<hcmc.length;i++){
				plantal.add(hcmc[i]);
			}
			
			for(int i = 0;i<al.size();i++){
				for(int k = i+1;k<al.size();k++){
					double val = al.get(i);
					double afterval = al.get(k); 
					String plant = plantal.get(i);
					String afterplant = plantal.get(k);
					if(afterval>val){
						al.set(i, afterval);
						al.set(k, val);
						plantal.set(i, afterplant);
						plantal.set(k, plant);
					}
				}
			}
						
			for(int i = 1;i<7;i++){
				rankal.add(i);
			}
			
			member.put(alkey, al);	
			member.put(rankalkey, rankal);
			member.put(plantalkey, plantal);
							
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			JdbcUtilProxoolImpl.closeResultSet(rs);
			JdbcUtilProxoolImpl.closeResultSet(testrs);
			JdbcUtilProxoolImpl.closeStatement(pred);
		}
		return member;
	}
	
}
