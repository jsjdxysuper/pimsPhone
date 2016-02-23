package com.sgepm.Tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcUtilProxoolImpl {

	public static Connection getConnection(){
		// TODO Auto-generated method stub
		Connection conn = null;
		try{
			conn = DriverManager.getConnection("proxool.oracle_dddmdb");
			
		}catch  (SQLException e) {   
	        System.out.println(e); 
	    } 
		return conn;
	}

	public static void closeConn(Connection conn){
		try {
			if(conn !=null &&!conn.isClosed()){
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if(rs !=null &&!rs.isClosed()){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeStatement(Statement stm) {
		try {
			if(stm !=null &&!stm.isClosed()){
				stm.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void close(Connection conn,ResultSet rs,Statement stm) {
		closeConn(conn);
		closeResultSet(rs);
		closeStatement(stm);
	}
	
	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		conn = getConnection();
		String sql="insert into testtest (id,name)values('11','11')";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtilProxoolImpl.closeConn(conn);
			JdbcUtilProxoolImpl.closeStatement(pstmt);
		}
		
	}

}
