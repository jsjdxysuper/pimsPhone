package com.sgepm.threemainpage.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import net.sf.json.JSONArray;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sgepm.Tools.JdbcUtils_C3P0;
import com.opensymphony.xwork2.ActionSupport;
import com.sgepm.Tools.PimsTools;
import com.sgepm.Tools.Tools;
import com.sgepm.threemainpage.entity.Plant60GenPower;
import com.sgepm.threemainpage.entity.PlantMonthEnergyOrRealTimeData;
import com.sgepm.threemainpage.entity.PlantYearAccumulateDataSeries;

public class PlantAction  extends ActionSupport{
	
	private Logger log = LoggerFactory.getLogger(PlantAction.class);
	private Map<String,Object>dataMap;//used to return json data
	
    private Connection conn = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;
    
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	
	public PlantAction(){
		dataMap = new HashMap<String,Object>();
	}
	
	/**
	 * 获取相关电厂60万机组出力曲线数据,以天为单位
	 * @return
	 */
	public String plant60GensPowerLineData(){
		log.info("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		
		//完成电厂编码和序号之间的相互转换
		String DCBMs[] = {"a0","b0","c0","d0","e0","f0"};
		HashMap<String,Integer>str2index = new HashMap<String,Integer>();
		str2index.put(DCBMs[0], 0);
		str2index.put(DCBMs[1], 1);
		str2index.put(DCBMs[2], 2);
		str2index.put(DCBMs[3], 3);
		str2index.put(DCBMs[4], 4);
		str2index.put(DCBMs[5], 5);
		
		Vector<Plant60GenPower> retData = new Vector<Plant60GenPower>();
		//60万机组所属电厂的数量
		final int PlantNum = 6;
		for(int i=0;i<PlantNum;i++){
			retData.add(new Plant60GenPower());
			retData.get(i).setPlantName(DCBMs[i]);
		}
		
		String date = ServletActionContext.getRequest().getParameter("date");
		//日期,时间,电厂编码,有功
		String sql = "select c1 rq,c2 sj,c3 dcbm,c4 yg from t001 t where c1 = ? order by  rq,dcbm,sj";
		
		//有功的最大最小值，为了设置坐标轴的最大最小
		double min = Double.MAX_VALUE;
		double max = 0;
		try {
			conn = JdbcUtils_C3P0.getConnection();
			st = conn.prepareStatement(sql);
			st.setString(1,date);
			rs = st.executeQuery();
		
			while(rs.next()){
				String dcbm = rs.getString("dcbm");
				String  rq  = rs.getString("rq");
				String  sj  = rs.getString("sj");
				double  yg  = rs.getDouble("yg");

				if(yg > max) max = yg;
				if(yg < min) min = yg;
				retData.get(str2index.get(dcbm)).setTimes(sj);
				
				//如果有功为0,能设置为null,在曲线上不会显示此点,使得坐标轴只显示有数部分,后面为0的不会显示
				if(Math.abs(yg)<Tools.DOUBLE_MIN)
					retData.get(str2index.get(dcbm)).setPowers(null);
				else
					retData.get(str2index.get(dcbm)).setPowers(yg);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtils_C3P0.release(conn, st, rs);
		}
		
		//如果查询结果为空
		if(min == Double.MAX_VALUE)min = 0;
		
		//数据库中的电厂代码与电厂首字母的对应
		HashMap<String,String>str2str = new HashMap<String,String>();
		str2str.put(DCBMs[0], "H");
		str2str.put(DCBMs[1], "Z");
		str2str.put(DCBMs[2], "K");
		str2str.put(DCBMs[3], "T");
		str2str.put(DCBMs[4], "Q");
		str2str.put(DCBMs[5], "Y");
		
		for(int i=0;i<retData.size();i++){
			String tempPlantName = retData.get(i).getPlantName();
			retData.get(i).setPlantName(str2str.get(tempPlantName));
		}
		
		dataMap.clear();
		for(int i=0;i<PlantNum;i++){
			dataMap.put(retData.get(i).getPlantName(), retData.get(i).getPowers());
			dataMap.put(retData.get(i).getPlantName()+"Times", retData.get(i).getTimes());
		}
		dataMap.put("maxRealtime", max);
		dataMap.put("minRealtime", min);
		
		return SUCCESS;
	}
	
	
	/**
	 * 获得电厂页面某月累计电量柱图的数据
	 * @return 返回包含PlantMonthData对象的JSON数组
	 * 结构如下
	 * "columnData":[["康平厂",26242],["铁岭厂",26434],["营口厂",22435],["庄河厂",25299],["清河厂",36604],["燕山湖",27705]]
	 */
	public String oneMonth60GensEnergyColumnData(){
		log.info("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		
		ResourceBundle properties                  = ResourceBundle.getBundle("pimsphone");
		String plantsStr = properties.getString("pims.plant.graph1.plants");
		
		String plantListStr[];//柱图所要显示的电厂列表
		plantListStr = plantsStr.split(",");
		
		log.info("PlantsOneMonthEnergyData电厂列表:"+plantsStr);
		
		//电厂的所配置的机组的集合(配置在pimsphone.properties文件中）
		ArrayList<String> generatorList        = new ArrayList<String>();
		//获得每个电厂所配置的机组列表		
		generatorList = PimsTools.getGeneratorsList("pims", "plant", "graph1");
		
		//装配sql语句中机组列表,以逗号分隔,in子句的最大数目为1000,足够了
		assert generatorList.size()<1000:"sql inSubStr len is "+generatorList.size();
		
		String inString="";
		for(int i=0;i<generatorList.size();i++){
		    if(i>0){
		        inString+=",";
		    }
		    inString+="'"+generatorList.get(i)+"'";
		}
		
		log.info("inString:"+inString);
		log.info("params:");
		
		String date = ServletActionContext.getRequest().getParameter("date");
		String dateWildcard = Tools.change2WildcardDate(date, Tools.time_span[2]);
		
		String sql = "select substr(t.rq,0,7),sum(t.rdl) as rdl,b.ssdcmc from info_dmis_zdhcjz t,base_jzbm b where t.jzbm in ("
						+inString+" )"+
						" and rq like ? and t.jzbm=b.jzbm group by ssdcmc,substr(t.rq,0,7) order by ssdcmc";
		log.info("sql查询:"+sql+"\n参数："+dateWildcard);
		
		//包含电厂发电量信息的Vector,其中每个对象代表一个电厂
		Vector<PlantMonthEnergyOrRealTimeData> plantVectorData = new Vector<PlantMonthEnergyOrRealTimeData>();
		plantVectorData.setSize(plantListStr.length);
		//都初始化为0，当有某个电厂发电量为0时，在数据中也有所体现
		for(int i=0;i<plantVectorData.size();i++){
			PlantMonthEnergyOrRealTimeData onePlantMonthEnergy = new PlantMonthEnergyOrRealTimeData();
			onePlantMonthEnergy.setName(plantListStr[i]);
			onePlantMonthEnergy.setData(new Float(0));
			plantVectorData.set(i, onePlantMonthEnergy);
		}
		
		try {

			conn = JdbcUtils_C3P0.getConnection();
			st = conn.prepareStatement(sql);
			st.setString(1,dateWildcard);
			rs = st.executeQuery();
		
			while(rs.next()){
				
				String ssdcmc = rs.getString("ssdcmc");
				float rdl = rs.getFloat("rdl");
				for(int i=0;i<plantVectorData.size();i++){
					PlantMonthEnergyOrRealTimeData onePlantMonthEnergy = plantVectorData.get(i);
					if(onePlantMonthEnergy.getName().compareTo(ssdcmc)==0)
						onePlantMonthEnergy.setData(rdl);
				}

				log.debug(ssdcmc+","+rdl);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtils_C3P0.release(conn, st, rs);
		}
		
		JSONArray allColumnData = new JSONArray();
		for(int i=0;i<plantVectorData.size();i++){
			JSONArray onePlantDataInOneMonth = new JSONArray();
			String tempName= plantVectorData.get(i).getName();
			if(tempName.compareTo("沈阳康平电厂")==0)
				tempName="K";
			if(tempName.compareTo("铁岭厂")==0)
				tempName="T";
			if(tempName.compareTo("营口电厂")==0)
				tempName="Y";
			if(tempName.compareTo("大连庄河电厂")==0)
				tempName="Z";
			if(tempName.compareTo("铁岭清河电厂")==0)
				tempName="Q";
			if(tempName.compareTo("朝阳燕山湖电厂")==0)
				tempName="H";
			onePlantDataInOneMonth.add(tempName);
			onePlantDataInOneMonth.add(plantVectorData.get(i).getData());
			allColumnData.add(onePlantDataInOneMonth);
		}
		
		dataMap.clear();
		dataMap.put("oneMonth60GensEnergyColumnData", allColumnData);
		return SUCCESS;
		
	}
	
	/**
	 * 查询相关电厂年累计发电量（每月一个颜色,每个电厂一个柱）
	 * @return
	 * 返回值的格式如下：
	 * {"seriesPlantName":[name1,name2,name3...],}
	 */
	public String year60GensAccuEnergyStackColumnData(){
		log.debug("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		
		//分段柱图所要显示的电厂列表
		int numPlant = 0;
		String date = ServletActionContext.getRequest().getParameter("date");
		int monthNum = Integer.parseInt(date.substring(5, 7));
		
		//包含电厂发电量信息的Vector,其中每个对象代表一个电厂
		Vector<PlantYearAccumulateDataSeries> plantVectorDataseries = new Vector<PlantYearAccumulateDataSeries>();

		//电厂的所配置的机组的集合(配置在pimsphone.properties文件中）
		ArrayList<String> generatorList        = new ArrayList<String>();
		
		//获得电厂列表
		ResourceBundle properties = ResourceBundle.getBundle("pimsphone");
		String plantsStr = properties.getString("pims.plant.graph1.plants");
		String plantListStr[] = plantsStr.split(",");
		numPlant = plantListStr.length;
		plantVectorDataseries.setSize(monthNum);

		for(int i=0;i<plantVectorDataseries.size();i++){
			PlantYearAccumulateDataSeries temp = new PlantYearAccumulateDataSeries();
			Vector<Float> dataTemp = new Vector<Float>();
			dataTemp.setSize(numPlant);
			temp.setData(dataTemp);
			temp.setName((i+1)+"月");
			plantVectorDataseries.set(i, temp);
		}
		
		log.info("YearAccumulatePlantData电厂列表:"+plantsStr);
		generatorList = PimsTools.getGeneratorsList("pims", "plant", "graph1");
		
		//装配sql语句,机组列表,用逗号分隔,in子句的最大数目为1000,足够了
		String inString="";
		for(int i=0;i<generatorList.size();i++){
		    if(i>0){
		        inString+=",";
		    }
		    inString+="'"+generatorList.get(i)+"'";
		}
		
		log.info("inString:"+inString);
		log.info("params:");
		
		String startDate = Tools.getFirstDateInYear(date);
		String sql = "select substr(t.rq,0,7) as yf,sum(t.rdl) as ydl,b.ssdcmc from info_dmis_zdhcjz t,base_jzbm b where t.jzbm in ("
						+inString+" )"+
						" and rq >= ? and rq <= ? and t.jzbm=b.jzbm group by ssdcmc,substr(t.rq,0,7) order by ssdcmc";
		log.info("sql查询:"+sql+"\n参数："+startDate+","+date);
		
		try {
			conn = JdbcUtils_C3P0.getConnection();
			st = conn.prepareStatement(sql);
			st.setString(1,startDate);
			st.setString(2,date);
			rs = st.executeQuery();

			while(rs.next()){
				String yf = rs.getString("yf");
				int yfInt = Integer.parseInt(yf.substring(5, 7));
				float ydl = rs.getFloat("ydl");
				String ssdcmc = rs.getString("ssdcmc");
				
				PlantYearAccumulateDataSeries temp = plantVectorDataseries.get(yfInt-1);
				for(int i=0;i<numPlant;i++){
					if(plantListStr[i].compareTo(ssdcmc)==0){
						temp.getData().set(i, ydl);
					}
				}
				//log.info("yf:"+yf+",ydl:"+ydl+",ssdcmc:"+ssdcmc);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtils_C3P0.release(conn, st, rs);
		}
		
		for(int i=0;i<6;i++){
			System.out.println(plantListStr[i]);
			String tempName= plantListStr[i];
			if(tempName.compareTo("沈阳康平电厂")==0)
				tempName="K";
			if(tempName.compareTo("铁岭厂")==0)
				tempName="T";
			if(tempName.compareTo("营口电厂")==0)
				tempName="Y";
			if(tempName.compareTo("大连庄河电厂")==0)
				tempName="Z";
			if(tempName.compareTo("铁岭清河电厂")==0)
				tempName="Q";
			if(tempName.compareTo("朝阳燕山湖电厂")==0)
				tempName="H";
			plantListStr[i] = tempName;
		}
		
		dataMap.clear();
		
		dataMap.put("seriesPlantName", plantListStr);
		dataMap.put("yearAccumulatePlantPowerSeries", plantVectorDataseries);
		
		return SUCCESS;
	}
	
	
	/**
	 * 获得电厂进度信息图的数据
	 * @return返回数据结构如下
	 * "plantProgressData":[[3.98,2.76],[7.97,5.15],[11.95,8.1],[15.93,11.01],
	 * 						[19.92,17.03],[23.9,23.15],[27.88,27.18],[31.87,32.31],
	 * 						[35.85,37.92],[39.83,41.6],[43.82,43.9],[47.8,44.9]]
	 * 数组的第一个数为计划值，第二个值为完成值，传到前端
	 * 在前端,因为范围图约束(第一个值必须比第二个值小),所以要把计划值>完成值的交换,统一成[小值,大值]格式,为了区分,交换后的柱为红色,没有交换的为蓝色
	 */
	public String plantProgressColumnData(){
		
		String date = ServletActionContext.getRequest().getParameter("date");
		String startDate = Tools.getFirstDateInYear(date);
		float yearPlan = 0;
		Vector<Float> monthPlan = new Vector<Float>();
		Vector<Float> monthFinish = new Vector<Float>();
		float yearAccumulate = 0;
		
		String sqlGetYearPlan = "select t.nf,t.njh  from info_sdlr_dcnjh t where t.dcbm = ? and t.nf = ?";
		
		//为以后留着的接口
		String jzbm = "sykpp";
		log.debug("sql查询:"+sqlGetYearPlan+"\n参数："+jzbm+","+date.substring(0,4));
		
		try {
			conn = JdbcUtils_C3P0.getConnection();
			st = conn.prepareStatement(sqlGetYearPlan);
			st.setString(1,jzbm);
			st.setString(2,date.substring(0,4));
			rs = st.executeQuery();
		
			while(rs.next())
				yearPlan = rs.getFloat("njh");
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtils_C3P0.release(conn, st, rs);
		}
		
		float oneMonthPlan = yearPlan/12/10000;
		//获得每个月的计划量
		for(int i=0;i<12;i++){

			monthPlan.add(Tools.float2Format(oneMonthPlan*(i+1), 2));
		}
		
		String sqlGetMonthPower = "select substr(rq,0,7) as yf,sum(rdl) as ylj from info_dmis_zdhcdc t "+
				"where dcbm='sykpp' and rq >= ? and rq <= ? group by substr(rq,0,7) order by yf";
		log.debug("sql查询:"+sqlGetMonthPower+"\n参数："+startDate+","+date);
		
		try {
			
			conn = JdbcUtils_C3P0.getConnection();
			st = conn.prepareStatement(sqlGetYearPlan);
			st.setString(1,startDate);
			st.setString(2,date);
			rs = st.executeQuery();

			while(rs.next()){
				String yf = rs.getString("yf");
				float ylj = rs.getFloat("ylj");
				ylj = ylj/10000;//由万千瓦时改为亿千瓦时
				yearAccumulate += ylj;
				monthFinish.add(Tools.float2Format(yearAccumulate, 2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtils_C3P0.release(conn, st, rs);
		}
		
		//plantIdentity
		Vector<Vector<Float>> vector = new Vector<Vector<Float>>();
		for(int i=0;i<monthFinish.size();i++){
			Vector<Float> temp = new Vector<Float>();
			temp.add(monthPlan.get(i));
			temp.add(monthFinish.get(i));
			vector.add(temp);
			
		}
		dataMap.clear();
		
		dataMap.put("plantProgressData", vector);
		
		return SUCCESS;
	}
}
