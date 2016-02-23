package com.sgepm.Tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface JdbcUtil {
	public Connection getConnection() throws SQLException;
	public void closeConn(Connection conn);
	public void closeResultSet(ResultSet rs);
	public void clossStatement(Statement stm);
}
