package com.sgepm.threemainpage.servlet;

import java.sql.*;
import java.util.ArrayList;

import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
public class OracleConnection {
	public static final String DRIVER="oracle.jdbc.driver.OracleDriver";
	public static final String URL="jdbc:oracle:thin:@192.168.1.100:1521:orcl";
	public static final String USER="dddmdb";
	public static final String PWD="dddmdb";
	private Connection con=null;
	private PreparedStatement ps=null;
	private ResultSet rs=null;
	public static DataSource source=null;
	
	public Connection getCon(){
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			con=DriverManager.getConnection(URL,USER,PWD);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}
	
	public void closeAll(){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(ps!=null){
			try {
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ResultSet query(String sqlStr,String []pras){
		con = getCon();
		try {
			ps=con.prepareStatement(sqlStr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(pras!=null){
			for(int i=0;i<pras.length;i++)
				try {
					ps.setString(i+1, pras[i]);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		try {
			rs = ps.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	public String printRS(){
		String str=new String();
		try {
			while(rs.next()){
				str+=rs.getString(1)+",";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	
	public static void main(String[] args) {
		OracleConnection oc = new OracleConnection();
		String sqlStr="select RQ,SJ,YG from info_data_dcyg t where DCMC='ÉòÑô¿µÆ½µç³§'AND RQ like ? ORDER BY SJ";

		String []a={"2014-12-10"};
		ResultSet rs= oc.query(sqlStr,a);
		JSONObject jo = new JSONObject();
		ArrayList<String> rq = new ArrayList<String>();
		ArrayList<String> sj = new ArrayList<String>();
		ArrayList<String> yg = new ArrayList<String>();

		try {
			while(rs.next()){
				rq.add(rs.getString(1));
				sj.add(rs.getString(2));
				yg.add(rs.getString(3));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jo.put("rq", JSONArray.fromObject(rq));
		jo.put("sj", JSONArray.fromObject(sj));
		jo.put("yg", JSONArray.fromObject(yg));
		System.out.println(jo.toString());
	}
}
