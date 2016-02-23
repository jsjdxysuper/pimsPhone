package com.sgepm.threemainpage.action;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.sgepm.Tools.JdbcUtilProxoolImpl;
import com.sgepm.Tools.JdbcUtils_C3P0;
import com.sgepm.Tools.Yfhl_Ylyxs_Tool;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("serial")
public class Yfhl_Ylyxs extends ActionSupport{

	Connection con;
	JSONObject member;
	JSONArray members;	
	
	public JSONArray getMembers() {
		return members;
	}

	public String execute()
			throws Exception {

		String datevalue = ServletActionContext.getRequest().getParameter("datetime").toString().trim();
		String year = datevalue.split("-")[0];
		String month = datevalue.split("-")[1];
		String newdate = year+"-"+month;
		members = new JSONArray();
				
		{			
			con = JdbcUtilProxoolImpl.getConnection();	
	
			String ylyxs = "select sykp_xslyl,dlzh_xslyl,tl_xslyl,tlqh_xslyl,yk_xslyl,cyysh_xslyl from v_ylyxsdb_60w where yf = '"+newdate+"'";	
			member = Yfhl_Ylyxs_Tool.getResult(con, ylyxs, "ylyxs", "ylyxsrank", "ylyxsplant");
			members.add(member);
			System.out.println("月利用小时  sql:"+ylyxs);
			
			String nlyxs = "select sum(sykp_xslyl),sum(dlzh_xslyl),sum(tl_xslyl),sum(tlqh_xslyl),sum(yk_xslyl),sum(cyysh_xslyl) "+
					" from v_ylyxsdb_60w where yf between '"+year+"-01' and '"+newdate+"'";	
			member = Yfhl_Ylyxs_Tool.getResult(con, nlyxs, "nlyxs", "nlyxsrank", "nlyxsplant");	
			members.add(member);
			System.out.println("年利用小时 sql:"+nlyxs);
			
			String yfhl = "select t1.yfhl as sykp_yfhl,t2.yfhl as dlzh_yfhl,t3.yfhl as tl_yfhl,t4.yfhl as tlqh_yfhl,t5.yfhl as yk_yfhl,t6.yfhl as cyysh_yfhl " +
					"from (select yfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '沈阳康平电厂') t1," +
							"(select yfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '大连庄河电厂') t2," +
							"(select yfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '铁岭厂') t3," +
							"(select yfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '铁岭清河电厂') t4," +
							"(select yfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '营口电厂') t5," +
							"(select yfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '朝阳燕山湖电厂') t6";
			member = Yfhl_Ylyxs_Tool.getResult(con, yfhl, "yfhl", "yfhlrank", "yfhlplant");	
			members.add(member);
			System.out.println("月负荷率 sql:"+yfhl);
			
			String nfhl = "select t1.nfhl as sykp_nfhl,t2.nfhl as dlzh_yfhl,t3.nfhl as tl_nfhl,t4.nfhl as tlqh_nfhl,t5.nfhl as yk_nfhl,t6.nfhl as cyysh_nfhl " +
					"from (select nfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '沈阳康平电厂') t1," +
							"(select nfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '大连庄河电厂') t2," +
							"(select nfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '铁岭厂') t3," +
							"(select nfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '铁岭清河电厂') t4," +
							"(select nfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '营口电厂') t5," +
							"(select nfhl from result_yfhl where ny = '"+newdate+"' and dcmc = '朝阳燕山湖电厂') t6";
			member = Yfhl_Ylyxs_Tool.getResult(con, nfhl, "nfhl", "nfhlrank", "nfhlplant");	
			members.add(member);
			System.out.println("年负荷率 sql"+nfhl);
			JdbcUtilProxoolImpl.closeConn(con);
			//System.out.println(members);

		}
		return "success";
	}
}
