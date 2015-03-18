package com.sgepm.threemainpage.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sgepm.Tools.OracleConnection;
import com.sgepm.Tools.PimsTools;
import com.sgepm.Tools.Tools;
import com.sgepm.threemainpage.entity.PlantYearAccumulateDataSeries;
import com.sgepm.threemainpage.entity.PlantMonthPowerOrRealTimeData;


@WebServlet(name="PlantServlet",urlPatterns="/PlantServlet")
public class PlantServlet extends HttpServlet {

	private String date;
	private String dateWildcard;
	private String jzbm = "sykpp";
	
	private ResourceBundle properties                  = ResourceBundle.getBundle("pimsphone");
	//机组编码到机组所属电厂名称的查询字典,Map<String,String>第一个String为机组编码，第二个String为所属电厂名称
	private HashMap<String,String> JZBM2DCMCDictionary = new HashMap<String,String>();
	
	private Logger log                                 = LoggerFactory.getLogger(HoleGridServlet.class);
	/**
	 * Constructor of the object.
	 */
	public PlantServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * 统一完成doGet和doPost的请求
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doRequest(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8") ;
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		if(request.getParameter("realtime")==null){
			date                     = request.getParameter("date");
			dateWildcard             = Tools.change2WildcardDate(date, Tools.time_span[2]);	
			JSONObject joAll         = new JSONObject();//要返回的电厂页面的全部数据	
			JSONArray ja             = new JSONArray();//各电厂月度发电量	
			ja                       = getOneMonthPowerData();
			
			JSONObject joSeries      = getYearAccumulatePowerData();//相关电厂60万机组年累计电量对比数据		
			JSONObject plantProgress = getProgressData();//本电厂日历进度曲线
			JSONObject realTimeData  = getRealTimeData();//相关电厂实时出力数据
			joAll.accumulateAll(plantProgress);
			joAll.accumulateAll(joSeries);
			joAll.put("columnData",ja);
			joAll.accumulateAll(realTimeData);
			
			String ret = joAll.toString();
			ret        = Tools.replacePlantName(ret, PimsTools.getPlantAbbrDic());
			ret        = Tools.getAbbrNameOfPlant(ret);
			out.write(ret);
		}else if(request.getParameter("realtime").compareTo("true")==0){
			log.debug("realtime request");
			JSONObject jo = getRealTimeData();
			out.write(jo.toString());
		}
		out.close();
	}
	

	/**
	 * 获得电厂页面某月累计电量柱图的数据
	 * @return 返回包含PlantMonthData对象的JSON数组
	 * 结构如下
	 * "columnData":[["康平厂",26242],["铁岭厂",26434],["营口厂",22435],["庄河厂",25299],["清河厂",36604],["燕山湖",27705]]
	 */
	public JSONArray getOneMonthPowerData() {
		
		log.debug("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		OracleConnection oc = new OracleConnection();
		String plantListStr[];//柱图所要显示的电厂列表
		//包含电厂发电量信息的Vector,其中每个对象代表一个电厂
		Vector<PlantMonthPowerOrRealTimeData> plantVectorData = new Vector<PlantMonthPowerOrRealTimeData>();

		//电厂的所配置的机组的集合(配置在pimsphone.properties文件中）
		ArrayList<String> generatorList        = new ArrayList<String>();

		
		String plantsStr = properties.getString("pims.plant.graph1.plants");
		plantListStr = plantsStr.split(",");
		plantVectorData.setSize(plantListStr.length);
		//都初始化为0，当有某个电厂发电量为0时，在数据中也有所体现
		for(int i=0;i<plantVectorData.size();i++){
			PlantMonthPowerOrRealTimeData temp = new PlantMonthPowerOrRealTimeData();
			temp.setName(plantListStr[i]);
			temp.setData(new Float(0));
			plantVectorData.set(i, temp);
		}

		log.debug("PlantsOneMonthPowerData电厂列表:"+plantsStr);


		//获得每个电厂所配置的机组列表		
		generatorList = PimsTools.getGeneratorsList("pims", "plant", "graph1");

		
		
		//装配sql语句中机组列表,以逗号分隔,in子句的最大数目为1000,足够了
		String inString="";
		for(int i=0;i<generatorList.size();i++){
		    if(i>0){
		        inString+=",";
		    }
		    inString+="'"+generatorList.get(i)+"'";
		}

		
		log.debug("inString:"+inString);
		log.debug("params:");
		String params[] = {dateWildcard};
		String sql = "select substr(t.rq,0,7),sum(t.rdl) as rdl,b.ssdcmc from info_dmis_zdhcjz t,base_jzbm b where t.jzbm in ("
						+inString+" )"+
						" and rq like ? and t.jzbm=b.jzbm group by ssdcmc,substr(t.rq,0,7) order by ssdcmc";
		log.debug("sql查询:"+sql+"\n参数："+dateWildcard);
		ResultSet rs=  oc.query(sql,params);

		try {
			while(rs.next()){
				
				String ssdcmc = rs.getString("ssdcmc");
				float rdl = rs.getFloat("rdl");
				for(int i=0;i<plantVectorData.size();i++){
					PlantMonthPowerOrRealTimeData temp = plantVectorData.get(i);
					if(temp.getName().compareTo(ssdcmc)==0)
						temp.setData(rdl);
				}

				log.debug(ssdcmc+","+rdl);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("nnnnnnnnnn");
		JSONArray ja = new JSONArray();
		for(int i=0;i<plantVectorData.size();i++){
			JSONArray temp = new JSONArray();
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
			temp.add(tempName);
			System.out.println(plantVectorData.get(i).getName());
			temp.add(plantVectorData.get(i).getData());
			ja.add(temp);
		}
		oc.closeAll();
		return ja;
	}
	
	
	/**
	 * 查询相关电厂年累计发电量（每月一个颜色,每个电厂一个柱）
	 * @return
	 * 返回值的格式如下：
	 * {"seriesPlantName":[name1,name2,name3...],}
	 */
	public JSONObject getYearAccumulatePowerData(){
//		select substr(t.rq,0,7),sum(t.rdl) as rdl,b.ssdcmc from info_dmis_zdhcjz t,base_jzbm b where t.jzbm in (
//				'sykppg1','sykppg2','tlpg5','tlpg6','ykpg3','ykpg4','dlzhpg1','dlzhpg2','tlqhpg1','tlqhpg9','cyyshpg1','cyyshpg2')
//				and rq <= '2014-10-11' and rq >= '2014-01-01' and t.jzbm=b.jzbm group by ssdcmc,substr(t.rq,0,7) order by ssdcmc
		
		log.debug("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		OracleConnection oc = new OracleConnection();
		//分段柱图所要显示的电厂列表
		int numPlant = 0;
		int monthNum = Integer.parseInt(date.substring(5, 7));
		//包含电厂发电量信息的Vector,其中每个对象代表一个电厂
		Vector<PlantYearAccumulateDataSeries> plantVectorDataseries = new Vector<PlantYearAccumulateDataSeries>();

		//电厂的所配置的机组的集合(配置在pimsphone.properties文件中）
		ArrayList<String> generatorList        = new ArrayList<String>();
		
		//获得电厂列表
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

		log.debug("YearAccumulatePlantData电厂列表:"+plantsStr);
		generatorList = PimsTools.getGeneratorsList("pims", "plant", "graph1");

		
		
		//装配sql语句,机组列表,用逗号分隔,in子句的最大数目为1000,足够了
		String inString="";
		for(int i=0;i<generatorList.size();i++){
		    if(i>0){
		        inString+=",";
		    }
		    inString+="'"+generatorList.get(i)+"'";
		}

		
		log.debug("inString:"+inString);
		log.debug("params:");
		
		String startDate = Tools.getFirstDateInYear(date);
		String params[] = {startDate,date};
		String sql = "select substr(t.rq,0,7) as yf,sum(t.rdl) as ydl,b.ssdcmc from info_dmis_zdhcjz t,base_jzbm b where t.jzbm in ("
						+inString+" )"+
						" and rq >= ? and rq <= ? and t.jzbm=b.jzbm group by ssdcmc,substr(t.rq,0,7) order by ssdcmc";
		log.debug("sql查询:"+sql+"\n参数："+startDate+","+date);
		ResultSet rs=  oc.query(sql,params);
		
		try {
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
				log.debug("yf:"+yf+",ydl:"+ydl+",ssdcmc:"+ssdcmc);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("wowowowowo");
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
		
		JSONObject jo = new JSONObject();
		jo.put("seriesPlantName", plantListStr);
		jo.put("yearAccumulatePlantPowerSeries", plantVectorDataseries);
		
		oc.closeAll();
		return jo;
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
	public JSONObject getProgressData(){
		
		log.debug("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		OracleConnection oc = new OracleConnection();
		//select substr(rq,0,7),sum(rdl) from info_dmis_zdhcdc t where dcbm='sykpp' and rq>='2014-01-01' and rq <='2014-03-12' group by substr(rq,0,7)
		//select t.*, t.rowid from info_sdlr_njh t
		String startDate = Tools.getFirstDateInYear(date);
		float yearPlan = 0;
		Vector<Float> monthPlan = new Vector<Float>();
		Vector<Float> monthFinish = new Vector<Float>();
		float yearAccumulate = 0;
		
		String sqlGetYearPlan = "select t.nf,t.njh  from info_sdlr_dcnjh t where t.dcbm = ? and t.nf = ?";
		String params1[] = {jzbm,date.substring(0,4)};
		log.debug("sql查询:"+sqlGetYearPlan+"\n参数："+jzbm+","+date.substring(0,4));
		ResultSet rs=  oc.query(sqlGetYearPlan,params1);
		try {
			while(rs.next())
				yearPlan = rs.getFloat("njh");
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		float oneMonthPlan = yearPlan/12/10000;
		//获得每个月的计划量
		for(int i=0;i<12;i++){
			
			
			monthPlan.add(Tools.float2Format(oneMonthPlan*(i+1), 2));
		}
		
		
		String sqlGetMonthPower = "select substr(rq,0,7) as yf,sum(rdl) as ylj from info_dmis_zdhcdc t "+
				"where dcbm='sykpp' and rq >= ? and rq <= ? group by substr(rq,0,7) order by yf";
		String params2[] = {startDate,date};
		log.debug("sql查询:"+sqlGetMonthPower+"\n参数："+startDate+","+date);
		rs=  oc.query(sqlGetMonthPower,params2);
		try {
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
		}
		//plantIdentity
		Vector<Vector<Float>> vector = new Vector<Vector<Float>>();
		for(int i=0;i<monthFinish.size();i++){
			Vector<Float> temp = new Vector<Float>();
			temp.add(monthPlan.get(i));
			temp.add(monthFinish.get(i));
			vector.add(temp);
			
		}
		JSONObject jo = new JSONObject();
		jo.put("plantProgressData", vector);
		oc.closeAll();
		return jo;
	}
	/**
	 * 获得相关电厂实时处理值,由配置文件指定要显示哪些电厂,每个电厂包含哪些机组
	 * @return返回值的格式如下:
	 * "realTimeData":[{"data":338.57,"name":"康平厂"},{"data":269.03,"name":"铁岭厂"},
	 * 					{"data":281.2,"name":"营口厂"},{"data":296.15,"name":"庄河厂"},
	 * 					{"data":619.7,"name":"清河厂"},{"data":714.62,"name":"燕山湖"}]
	 */
	public JSONObject getRealTimeData(){
//		select a.sj,sum(a.yg),b.ssdcbm,b.ssdcmc from info_data_jzyg a,base_jzbm b where a.sj =(
//				select max(t.sj) as sj from info_data_jzyg t where rq = '2014-12-20' and t.jzbm in ('sykppg1') group by jzbm) and a.rq='2014-12-20' and a.jzbm in 
//				('sykppg1','sykppg2','tlpg5','tlpg6','ykpg3','ykpg4','dlzhpg1','dlzhpg2','tlqhpg1','tlqhpg9','cyyshpg1','cyyshpg2') and 
//				a.jzbm = b.jzbm group by b.ssdcmc,b.ssdcbm,a.sj
		log.debug("进入"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		String today = Tools.getTodayStr();
		String realtimeTime    = "";
		Vector<PlantMonthPowerOrRealTimeData> plantVectorData = new Vector<PlantMonthPowerOrRealTimeData>();
		String plantListStr[];//所要显示的电厂列表
		String plantsStr = properties.getString("pims.plant.graph1.plants");
		plantListStr = plantsStr.split(",");
		
		
		//电厂的所配置的机组的集合(配置在pimsphone.properties文件中）
		ArrayList<String> generatorList        = new ArrayList<String>();
		//获得每个电厂所配置的机组列表		
		generatorList = PimsTools.getGeneratorsList("pims", "plant", "graph1");
		
		plantVectorData.setSize(plantListStr.length);
		//都初始化为0，当有某个电厂发电量为0时，在数据中也有所体现
		for(int i=0;i<plantVectorData.size();i++){
			PlantMonthPowerOrRealTimeData temp = new PlantMonthPowerOrRealTimeData();
			temp.setName(plantListStr[i]);
			temp.setData(new Float(0));
			plantVectorData.set(i, temp);
		}
		
		//装配sql语句,in子句的最大数目为1000,足够了
		String inString="";
		for(int i=0;i<generatorList.size();i++){
		    if(i>0){
		        inString+=",";
		    }
		    inString+="'"+generatorList.get(i)+"'";
		}
		OracleConnection oc = new OracleConnection();
		String sql = "select t.C1,t.C2,t.C3,t.C4 from latest_date_power t where t.C2 in (select max(C2) from latest_date_power) order by t.C3";
		
		String params[] = {today,today};
		log.debug("sql查询:"+sql+"\n参数："+today+","+today);
		ResultSet rs =  oc.query(sql,null);

		int i = 0;
		try {
			while(rs.next()){
				String rq     = rs.getString("C1");
				String sj     = rs.getString("C2");
				String ssdcmc = rs.getString("C3");
				float  yg     = rs.getFloat("C4");
				
				realtimeTime  = rq+" "+sj;

				plantVectorData.get(i).setData(Tools.float2Format(yg, 2));
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//if(data.size()<1)return null;
		JSONObject jo = new JSONObject();
		jo.put("realTimeData", plantVectorData);
		jo.put("realtimeTime", realtimeTime);
		
		oc.closeAll();
		return jo;
	}
	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doRequest(request,response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doRequest(request,response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {

		OracleConnection oc = new OracleConnection();
		//建立机组编码到机组所属电厂名称的查询字典,Map<String,String>第一个String为机组编码，第二个String为所属电厂名称
		String sqlGenerator2Plant = "select jzbm,ssdcmc from base_jzbm t";
		log.debug("sql查询:"+sqlGenerator2Plant+"\n");
		ResultSet rs= oc.query(sqlGenerator2Plant,null);		
		try {
			while(rs.next()){
				JZBM2DCMCDictionary.put(rs.getString("JZBM"), rs.getString("SSDCMC"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		oc.closeAll();
	}

}
