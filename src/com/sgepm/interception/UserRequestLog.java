package com.sgepm.interception;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.sgepm.Tools.OracleConnection;

public class UserRequestLog  extends AbstractInterceptor{

	private Logger log = LoggerFactory.getLogger(UserRequestLog.class);
	private static long i = 0;
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		Map paramMap = invocation.getInvocationContext().getParameters();
		String[] names = (String[]) paramMap.get("yhid");
		String yhid = null;
		if(names != null && names.length>0)
			yhid = names[0];
		else
			yhid = "";
		
		String method = invocation.getInvocationContext().getName(); 
		
		String className = invocation.getAction().getClass().getSimpleName();
		
		String result = invocation.invoke();
		//insert into info_user_request_log (rq,sj,yhid,class,method,message) values('2015-04-08','09:43:00','123','class','method','message')
		OracleConnection oc = new OracleConnection();
		String sqlStr = "insert into pcadb.info_user_request_log (rq,sj,yhid,class,method,message) values(?,?,?,?,?,?)";
		
		Date now = new Date();
		
		String todyDate = String.format("%tF", now);
		String nowTime = String.format("%tT", now);
		String   paras[]  = {todyDate,nowTime,yhid,className,method,""+i};
		i++;
		
		//向数据库中插入访问日志
		oc.update(sqlStr, paras);
		return   result;
	}

}
