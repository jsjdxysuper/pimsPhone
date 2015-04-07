package com.sgepm.threemainpage.action;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.ActionSupport;
import com.sgepm.Tools.OracleConnection;
import com.sgepm.Tools.Tools;
import com.sgepm.threemainpage.servlet.HoleGridServlet;

public class HoleGridAction extends ActionSupport{
//	private String date;
//	private String dateWildcard;
	private Logger log                       = LoggerFactory.getLogger(HoleGridServlet.class);
	private HashMap<String,Integer> sequence = new HashMap<String,Integer>();
	private int tableRows                    = 6;
	private int tableColumns                 = 6;
	private Vector<String> tableFirstColumn  = new Vector<String>();
	
	private Map<String,Object>dataMap;//used to return json data
	
	public HoleGridAction(){
		dataMap = new HashMap<String,Object>();
		
		tableFirstColumn.add("全省发电");
		tableFirstColumn.add("直调火电");
		tableFirstColumn.add("直调水电");
		tableFirstColumn.add("直调风电");
		tableFirstColumn.add("直调核电");
		tableFirstColumn.add("联络线净受电");
		
		sequence.put("全省发电", new Integer(0));
		sequence.put("直调火电", new Integer(1));
		sequence.put("直调水电", new Integer(2));
		sequence.put("直调风电", new Integer(3));
		sequence.put("直调核电", new Integer(4));
		sequence.put("联络线净受电", new Integer(5));
	}
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	
	/**
	 * 获取全网月度发电量信息
	 * @return
	 */
	public String monthEnergyLineData(){
		dataMap.clear();
		
		log.info("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		String date = ServletActionContext.getRequest().getParameter("date");
		log.info("date:"+date);

		
		
		
		java.sql.Date nowDate;
		try {
			nowDate = java.sql.Date.valueOf(date);
		} catch (java.lang.IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			log.warn("日期输入参数为空");
			return null;
		}
		
		Vector<ArrayList<String>> allDate = new Vector<ArrayList<String>>();
		allDate.setSize(3);
		for(int i=0;i<allDate.size();i++){
			ArrayList<String> temp = new ArrayList<String>();
			allDate.set(i, temp);
		}
		Vector<ArrayList<Float>> allData = new Vector<ArrayList<Float>>();
		allData.setSize(3);
		for(int i=0;i<allDate.size();i++){
			ArrayList<Float> temp = new ArrayList<Float>();
			allData.set(i, temp);
		}
		
		//获得上个月的全省发电量数据
		String projectSqlStr="select rq,sj from info_dmis_fdqk t where xmmc='全省发电' and rq like ? order by rq";
		String lastMonthDateWildStr = Tools.getLastMonthWildStr(nowDate);
		String []dataParas = {lastMonthDateWildStr};
		log.info("sql 查询上个月的全省发电量数据:"+projectSqlStr+",参数:"+lastMonthDateWildStr);
		OracleConnection oc = new OracleConnection();
		ResultSet rs= oc.query(projectSqlStr,dataParas);
		
		try {
			while(rs.next()){
				allDate.get(1).add(rs.getString("rq"));
				allData.get(1).add(rs.getFloat("sj"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//获得这个月的全省发电量数据
		dataParas[0] = Tools.change2WildcardDate(date, Tools.time_span[2]);
		log.info("sql 查询这个月的全省发电量数据,参数:"+dataParas[0]);
		rs = null;
		rs = oc.query(projectSqlStr, dataParas);
		
		try {
			while(rs.next()){
				allDate.get(2).add(rs.getString("rq"));
				allData.get(2).add(rs.getFloat("sj"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//获得去年同期月份全省发电量数据
		String lastYearDateWildStr = Tools.getLastYearMonthWildStr(java.sql.Date.valueOf(date));
		dataParas[0] = lastYearDateWildStr;
		log.info("sql 查询去年同期月份全省发电量数据,参数:"+dataParas[0]);
		rs = null;
		rs = oc.query(projectSqlStr, dataParas);
		
		try {
			while(rs.next()){
				allDate.get(0).add(rs.getString("rq"));
				allData.get(0).add(rs.getFloat("sj"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataMap.put("lastYearDate", allDate.get(0));
		dataMap.put("lastMonthDate", allDate.get(1));
		dataMap.put("thisMonthDate", allDate.get(2));
		dataMap.put("lastYear", allData.get(0));
		dataMap.put("lastMonth", allData.get(1));
		dataMap.put("thisMonth", allData.get(2));
		
		return SUCCESS;
	}
	
	/**
	 * 获得全省日发电信息的表格数据
	 * @return
	 * 	 * "data":[["全省发电","43143.00","50.33","1344.65","4.84","6.35"],
	 * ["直调火电","31179.00","37.66","988.59","6.28","4.50"],
	 * ["直调水电","79.00","0.07","8.59","-33.87","-52.81"],
	 * ["直调风电","2568.00","2.19","48.85","70.26","4.21"],
	 * ["直调核电","2667.00","3.13","114.57","-11.65","96.13"],
	 * ["联络线净受电","16893.00","20.66","559.10","-4.19","-7.17"]]
	 */
	public String dayEnergyTableData(){
		log.info("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		
		OracleConnection oc = new OracleConnection();
		String projectSqlStr="select xmmc,sj,ylj,nlj,ytb,ntb from info_dmis_fdqk t where rq like ? order by xmmc desc";
		String date = ServletActionContext.getRequest().getParameter("date");
		String []dataParas = {date};
		log.info("sql查询:"+projectSqlStr+"\n参数："+dataParas[0]);
		ResultSet rs= oc.query(projectSqlStr,dataParas);
		
		Vector<Vector<String>> vector = new Vector<Vector<String>>();
		vector.setSize(tableRows);
		
		for(int i=0;i<tableRows;i++){
			Vector<String>temp = new Vector<String>();
			temp.setSize(tableColumns);
			temp.set(0,tableFirstColumn.get(i));
			for(int j=1;j<tableColumns;j++){
				temp.set(j,String.valueOf(Tools.float2Format(0, 2)));
			}
			vector.set(i,temp);
		}
		
		
		try {
			while(rs.next()){
				
				String xmmc = rs.getString("xmmc");
				String sj = Tools.float2Format(rs.getFloat("sj"));
				String ylj = Tools.float2Format(rs.getFloat("ylj"));
				String nlj = Tools.float2Format(rs.getFloat("nlj"));
				String ytb = Tools.float2Format(rs.getFloat("ytb"));
				String ntb = Tools.float2Format(rs.getFloat("ntb"));


				if(sequence.get(xmmc)!=null){
					
					int i = sequence.get(xmmc).intValue();
					for(int j=0;j<tableFirstColumn.size();j++){
						if(tableFirstColumn.get(j).compareTo(xmmc)==0){
							Vector<String>temp = vector.get(j);
							temp.set(0,xmmc);
							temp.set(1,sj);
							temp.set(2,ylj);
							temp.set(3,nlj);
							temp.set(4,ytb);
							temp.set(5,ntb);
						}
					}
					//jsArray.add(i,value);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dataMap.put("data", vector);
		return SUCCESS;
	}
	
}
