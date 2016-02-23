package com.sgepm.threemainpage.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import net.sf.json.JSONArray;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opensymphony.xwork2.ActionSupport;
import com.sgepm.Tools.JdbcUtilProxoolImpl;
import com.sgepm.Tools.Tools;
import com.sgepm.threemainpage.entity.PlantMonthEnergyOrRealTimeData;

public class GeneratorAction  extends ActionSupport{
	
	private Logger log = LoggerFactory.getLogger(GeneratorAction.class);
	private Map<String,Object>dataMap;//used to return json data
    private Connection conn = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;
	
	
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	
	public GeneratorAction(){
		dataMap = new HashMap<String,Object>();
	}
	
	/**
	 *  获取机组日电量信息,由日电量信息计算利用小时数,平均有功,负荷率
	 * @return 康平机组信息的Map
	 */
	public String dayGensLoadGaugeData(){
		log.info("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		float g1Average,g1Energy,g1TimeUse;
		float g2Average,g2Energy,g2TimeUse;
		

		g1Average=g1Energy=g1TimeUse=0;
		g2Average=g2Energy=g2TimeUse=0;
		
		String generatorSqlStr="select t.jzbm,t.jzmc,t.rdl from info_dmis_zdhcjz t,base_jzbm b where t.jzbm=b.jzbm and b.ssdcbm= ? and t.rq= ? order by jzbm,rq";

		String dcbm = "sykpp";
		String date = ServletActionContext.getRequest().getParameter("date");
		String []dataParas={dcbm,date};
		log.info("sql查询:"+generatorSqlStr+"\n参数:"+dcbm+","+date);
		
		try {
			
			conn = JdbcUtilProxoolImpl.getConnection();
			st = conn.prepareStatement(generatorSqlStr);
			st.setString(1,dcbm);
			st.setString(2,date);
			rs = st.executeQuery();
			while(rs.next()){
				
				String jzbm = rs.getString("jzbm");
				String jzmc = rs.getString("jzmc");
				float  rdl  = rs.getFloat("rdl");

				if(jzbm.compareTo("sykppg1")==0){
					g1Energy = rdl;
				}
				else if(jzbm.compareTo("sykppg2")==0){
					g2Energy = rdl;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtilProxoolImpl.close(conn,rs,st);
		}
		
		float energy  = g1Energy+g2Energy;
		float average = (energy)*10/24;
		float timeUse = energy*10/(Tools.rongLiang*2);
		

		
		
		g1Average = g1Energy*10/24;
		g1TimeUse = g1Energy*10/Tools.rongLiang;
		
		g2Average = g2Energy*10/24;
		g2TimeUse = g2Energy*10/Tools.rongLiang;

		float g1Load = g1Energy*10/(24*Tools.rongLiang)*100;
		float g2Load = g2Energy*10/(24*Tools.rongLiang)*100;
		
		dataMap.clear();

		dataMap.put("g1Load", Tools.float2Format(g1Load, 2));
		dataMap.put("g2Load", Tools.float2Format(g2Load, 2));
		return SUCCESS;
	}
	
	
	
	/**
	 * 获取机组的详细表格信息
	 * @return
	 */
	public String dayGensDetailTableData(){
		log.info("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		
		float g1Average,g1Energy,g1TimeUse;
		float g2Average,g2Energy,g2TimeUse;
		

		g1Average=g1Energy=g1TimeUse=0;
		g2Average=g2Energy=g2TimeUse=0;
		
		String generatorSqlStr="select t.jzbm,t.jzmc,t.rdl from info_dmis_zdhcjz t,base_jzbm b where t.jzbm=b.jzbm and b.ssdcbm= ? and t.rq= ? order by jzbm,rq";

		String dcbm = "sykpp";
		String date = ServletActionContext.getRequest().getParameter("date");
		String []dataParas={dcbm,date};
		log.info("sql查询:"+generatorSqlStr+"\n参数:"+dcbm+","+date);
		
		
		try {
			conn = JdbcUtilProxoolImpl.getConnection();
			st = conn.prepareStatement(generatorSqlStr);
			st.setString(1,dcbm);
			st.setString(2,date);
			rs = st.executeQuery();
			while(rs.next()){
				
				String jzbm = rs.getString("jzbm");
				String jzmc = rs.getString("jzmc");
				float  rdl  = rs.getFloat("rdl");

				if(jzbm.compareTo("sykppg1")==0){
					g1Energy = rdl;
				}
				else if(jzbm.compareTo("sykppg2")==0){
					g2Energy = rdl;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtilProxoolImpl.close(conn,rs,st);
		}
		
		
		float energy  = g1Energy+g2Energy;
		float average = (energy)*10/24;
		float timeUse = energy*10/(Tools.rongLiang*2);
		
		dataMap.clear();
		dataMap.put("energy", Tools.float2Format(energy, 2));
		dataMap.put("average", Tools.float2Format(average, 2));
		dataMap.put("timeUse", Tools.float2Format(timeUse, 2));
		
		
		g1Average = g1Energy*10/24;
		g1TimeUse = g1Energy*10/Tools.rongLiang;
		
		g2Average = g2Energy*10/24;
		g2TimeUse = g2Energy*10/Tools.rongLiang;

		dataMap.put("g1Average",Tools.float2Format(g1Average,2));
		dataMap.put("g1Energy",Tools.float2Format(g1Energy,2));
		dataMap.put("g1TimeUse",Tools.float2Format(g1TimeUse,2));
		

		dataMap.put("g2Average",Tools.float2Format(g2Average,2));
		dataMap.put("g2Energy",Tools.float2Format(g2Energy,2));
		dataMap.put("g2TimeUse",Tools.float2Format(g2TimeUse,2));
		return SUCCESS;
	}
	
	/**
	 * 获取电厂内值间月度负荷率对比数据
	 * @return
	 */
	public String monthInterDutyLoadColumnData(){
		
		log.debug("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		
		Vector<Integer> eachDutyHours = new Vector<Integer>();
		Vector<Float> gEnergy = new Vector<Float>();

		eachDutyHours.setSize(5);
		gEnergy.setSize(5);

		for(int i=0;i<eachDutyHours.size();i++){
			eachDutyHours.set(i, new Integer(0));
			gEnergy.set(i,new Float(0));
		}
		String hoursSql = "select a.wz,sum(yxsj)/12 sj,sum(dl2)+sum(dl1) dl "+
						"from "+
						"pri_zbb a,pri_bcyxsj b,pri_dljh c "+ 
						"where a.bc=b.bc and a.rq=b.rq and a.wz=c.wz and a.rq=c.rq and a.rq "+ 
						"like ? group by a.wz order by a.wz";
		
		String date = ServletActionContext.getRequest().getParameter("date");
		String dateMonthWildcard = Tools.change2WildcardDate(date, Tools.time_span[2]);
		String []params = {dateMonthWildcard};
		log.debug("sql查询："+hoursSql+"\n参数："+dateMonthWildcard);
		
		try {
			conn = JdbcUtilProxoolImpl.getConnection();
			st = conn.prepareStatement(hoursSql);
			st.setString(1,dateMonthWildcard);
			rs = st.executeQuery();
			while(rs.next()){
				int wz = rs.getInt("wz");
				int sj = rs.getInt("sj");
				float dl = rs.getFloat("dl");

				eachDutyHours.set(wz-1, sj);

				gEnergy.set(wz-1, dl);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtilProxoolImpl.close(conn,rs,st);
		}
		
		Vector<PlantMonthEnergyOrRealTimeData> loadRate = new Vector<PlantMonthEnergyOrRealTimeData>();
		loadRate.setSize(5);
		for(int i=0;i<eachDutyHours.size();i++){
			float temp =0;
			PlantMonthEnergyOrRealTimeData object = new PlantMonthEnergyOrRealTimeData();
			if(Math.abs(gEnergy.get(i)-0)<Tools.FLOAT_MIN)
			{
				Float va = new Float(0);
				object.setData(Tools.float2Format(va*100, 2));
				object.setName((i+1)+"");
			}
			else
			{
				Float va = (gEnergy.get(i))/(Tools.rongLiang/10*eachDutyHours.get(i));
				object.setData(Tools.float2Format(va*100, 2));
				object.setName((i+1)+"");
			}
				
			
			loadRate.set(i,object);
		}
		JSONArray ja = new JSONArray();
		for(int i=0;i<loadRate.size();i++){
			JSONArray temp = new JSONArray();
			temp.add(loadRate.get(i).getName());
			temp.add(loadRate.get(i).getData());
			ja.add(temp);
		}
		
		dataMap.clear();
		dataMap.put("monthLoadRate", ja);
		return SUCCESS;
	}
}
