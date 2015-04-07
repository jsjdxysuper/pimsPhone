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
		
		tableFirstColumn.add("ȫʡ����");
		tableFirstColumn.add("ֱ�����");
		tableFirstColumn.add("ֱ��ˮ��");
		tableFirstColumn.add("ֱ�����");
		tableFirstColumn.add("ֱ���˵�");
		tableFirstColumn.add("�����߾��ܵ�");
		
		sequence.put("ȫʡ����", new Integer(0));
		sequence.put("ֱ�����", new Integer(1));
		sequence.put("ֱ��ˮ��", new Integer(2));
		sequence.put("ֱ�����", new Integer(3));
		sequence.put("ֱ���˵�", new Integer(4));
		sequence.put("�����߾��ܵ�", new Integer(5));
	}
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	
	/**
	 * ��ȡȫ���¶ȷ�������Ϣ
	 * @return
	 */
	public String monthEnergyLineData(){
		dataMap.clear();
		
		log.info("����"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		String date = ServletActionContext.getRequest().getParameter("date");
		log.info("date:"+date);

		
		
		
		java.sql.Date nowDate;
		try {
			nowDate = java.sql.Date.valueOf(date);
		} catch (java.lang.IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			log.warn("�����������Ϊ��");
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
		
		//����ϸ��µ�ȫʡ����������
		String projectSqlStr="select rq,sj from info_dmis_fdqk t where xmmc='ȫʡ����' and rq like ? order by rq";
		String lastMonthDateWildStr = Tools.getLastMonthWildStr(nowDate);
		String []dataParas = {lastMonthDateWildStr};
		log.info("sql ��ѯ�ϸ��µ�ȫʡ����������:"+projectSqlStr+",����:"+lastMonthDateWildStr);
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
		
		//�������µ�ȫʡ����������
		dataParas[0] = Tools.change2WildcardDate(date, Tools.time_span[2]);
		log.info("sql ��ѯ����µ�ȫʡ����������,����:"+dataParas[0]);
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
		
		
		//���ȥ��ͬ���·�ȫʡ����������
		String lastYearDateWildStr = Tools.getLastYearMonthWildStr(java.sql.Date.valueOf(date));
		dataParas[0] = lastYearDateWildStr;
		log.info("sql ��ѯȥ��ͬ���·�ȫʡ����������,����:"+dataParas[0]);
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
	 * ���ȫʡ�շ�����Ϣ�ı������
	 * @return
	 * 	 * "data":[["ȫʡ����","43143.00","50.33","1344.65","4.84","6.35"],
	 * ["ֱ�����","31179.00","37.66","988.59","6.28","4.50"],
	 * ["ֱ��ˮ��","79.00","0.07","8.59","-33.87","-52.81"],
	 * ["ֱ�����","2568.00","2.19","48.85","70.26","4.21"],
	 * ["ֱ���˵�","2667.00","3.13","114.57","-11.65","96.13"],
	 * ["�����߾��ܵ�","16893.00","20.66","559.10","-4.19","-7.17"]]
	 */
	public String dayEnergyTableData(){
		log.info("����"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		
		OracleConnection oc = new OracleConnection();
		String projectSqlStr="select xmmc,sj,ylj,nlj,ytb,ntb from info_dmis_fdqk t where rq like ? order by xmmc desc";
		String date = ServletActionContext.getRequest().getParameter("date");
		String []dataParas = {date};
		log.info("sql��ѯ:"+projectSqlStr+"\n������"+dataParas[0]);
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
