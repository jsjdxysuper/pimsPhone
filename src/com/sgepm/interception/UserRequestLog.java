package com.sgepm.interception;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.sgepm.Tools.JdbcUtilProxoolImpl;
public class UserRequestLog  extends AbstractInterceptor{

	private Logger log = LoggerFactory.getLogger(UserRequestLog.class);
    private Connection conn = null;
    private PreparedStatement st = null;
	private static long i = 0;
	@Override
	public String intercept(ActionInvocation invocation) {

		Map paramMap = invocation.getInvocationContext().getParameters();
		String[] names = (String[]) paramMap.get("yhid");
		String yhid = null;
		if(names != null && names.length>0)
			yhid = names[0];
		else
			yhid = "";
		
		String method = invocation.getInvocationContext().getName(); 
		
		String className = invocation.getAction().getClass().getSimpleName();
		
		String result = null   ;
		try {
			result = invocation.invoke();

			//insert into info_user_request_log (rq,sj,yhid,class,method,message) values('2015-04-08','09:43:00','123','class','method','message')
			Date now = new Date();
			
			String todyDate = String.format("%tF", now);
			String nowTime = String.format("%tT", now);
			
			String sqlStr = "insert into pcadb.info_user_request_log (rq,sj,yhid,class,method,message) values(?,?,?,?,?,?)";
			conn = JdbcUtilProxoolImpl.getConnection();
			st = conn.prepareStatement(sqlStr);
			st.setString(1, todyDate);
			st.setString(2, nowTime);
			st.setString(3, yhid);
			st.setString(4, className);
			st.setString(5, method);
			st.setString(6, ""+i);
			int retSql = st.executeUpdate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtilProxoolImpl.closeStatement(st);
			JdbcUtilProxoolImpl.closeConn(conn);
		}
		
		

		//String   paras[]  = {todyDate,nowTime,yhid,className,method,""+i};
		i++;
		
		//向数据库中插入访问日志
		return   result;
	}

}
