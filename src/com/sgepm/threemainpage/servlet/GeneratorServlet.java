package com.sgepm.threemainpage.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sgepm.Tools.OracleConnection;
import com.sgepm.Tools.Tools;
import com.sgepm.threemainpage.entity.PlantMonthPowerOrRealTimeData;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet(name="GeneratorServlet",urlPatterns="/GeneratorServlet")
public class GeneratorServlet extends HttpServlet {

	private String date;
	private String dateWildcard;
	private String dcbm = "sykpp";
	private ResourceBundle properties = ResourceBundle.getBundle("pimsphone");
	private Logger log                = LoggerFactory.getLogger(GeneratorServlet.class);
	/**
	 * Constructor of the object.
	 */
	public GeneratorServlet() {
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
	 * 在此处统一处理get和post请求
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void doRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8") ;
		response.setCharacterEncoding("UTF-8") ;
		PrintWriter out = response.getWriter();
		
		
		date = request.getParameter("date");		
		date = Tools.formatDate(date);//改变日期的格式为YYYY-MM-DD
		log.debug("post机组日期查询日期:"+date);
		String returnData =  getData();
//		try {
//			Thread.currentThread().sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if(returnData!=null)
			out.write(returnData);
		out.close();
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
	 *  获取机组日电量信息,由日电量信息计算利用小时数,平均有功,负荷率
	 * @return 康平机组信息的Map
	 */
	public JSONObject getEachGenerator(){
		
		OracleConnection oc = new OracleConnection();
		JSONObject eachGeneratorData = new JSONObject();

		
		float g1Average,g1Energy,g1TimeUse;
		float g2Average,g2Energy,g2TimeUse;
		

		g1Average=g1Energy=g1TimeUse=0;
		g2Average=g2Energy=g2TimeUse=0;
		
		String generatorSqlStr="select t.jzbm,t.jzmc,t.rdl from info_dmis_zdhcjz t,base_jzbm b where t.jzbm=b.jzbm and b.ssdcbm= ? and t.rq= ? order by jzbm,rq";

		String []dataParas={dcbm,date};

		ResultSet rs = oc.query(generatorSqlStr,dataParas);

		
		try {
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
		}
		
		float energy  = g1Energy+g2Energy;
		float average = (energy)*10/24;
		float timeUse = energy*24/(Tools.rongLiang*2);
		eachGeneratorData.put("energy", Tools.float2Format(energy, 2));
		eachGeneratorData.put("average", Tools.float2Format(average, 2));
		eachGeneratorData.put("timeUse", Tools.float2Format(timeUse, 2));
		
		
		g1Average = g1Energy*10/24;
		g1TimeUse = g1Energy*24/Tools.rongLiang;
		
		g2Average = g2Energy*10/24;
		g2TimeUse = g2Energy*24/Tools.rongLiang;

		eachGeneratorData.put("g1Average",Tools.float2Format(g1Average,2));
		eachGeneratorData.put("g1Energy",Tools.float2Format(g1Energy,2));
		eachGeneratorData.put("g1TimeUse",Tools.float2Format(g1TimeUse,2));
		

		eachGeneratorData.put("g2Average",Tools.float2Format(g2Average,2));
		eachGeneratorData.put("g2Energy",Tools.float2Format(g2Energy,2));
		eachGeneratorData.put("g2TimeUse",Tools.float2Format(g2TimeUse,2));
		oc.closeAll();
		return eachGeneratorData;
	}
	
	
	public JSONObject getMonthLoadRate(){
		int dayDutyHour = Integer.parseInt(properties.getString("pims.plant.dutyhour.白"));
		int foreNightDutyHour = Integer.parseInt(properties.getString("pims.plant.dutyhour.前"));
		int laterNightDutyHour = Integer.parseInt(properties.getString("pims.plant.dutyhour.后"));
		OracleConnection oc = new OracleConnection();
//		select sum(decode(wz1,'白','9','前','8','后','7','0')) wz1,
//		sum(decode(wz2,'白','9','前','8','后','7','0')) wz2,
//		sum(decode(wz3,'白','9','前','8','后','7','0')) wz3,
//		sum(decode(wz4,'白','9','前','8','后','7','0')) wz4,
//		sum(decode(wz5,'白','9','前','8','后','7','0')) wz5 from pri_zbb t where rq like '2015-01-%%'
//		select wz,(sum(dl1)+sum(dl2)) dl from pri_dljh t where rq like '2015-01-%%' group by wz
		Vector<Integer> eachDutyHours = new Vector<Integer>();
		eachDutyHours.setSize(5);
		for(int i=0;i<eachDutyHours.size();i++)
			eachDutyHours.set(i, new Integer(0));
		String hoursSql = 		"select sum(decode(wz1,'白','9','前','8','后','7','0')) wz1, "+
				"sum(decode(wz2,'白','9','前','8','后','7','0')) wz2, "+
				"sum(decode(wz3,'白','9','前','8','后','7','0')) wz3, "+
				"sum(decode(wz4,'白','9','前','8','后','7','0')) wz4, "+
				"sum(decode(wz5,'白','9','前','8','后','7','0')) wz5 from pri_zbb t where rq like ? ";
		
		String dateMonthWildcard = Tools.change2WildcardDate(date, Tools.time_span[2]);
		String []params = {dateMonthWildcard};
		ResultSet rs = oc.query(hoursSql,params);
		
		try {
			while(rs.next()){
				int wz1 = rs.getInt("wz1");
				int wz2 = rs.getInt("wz2");
				int wz3 = rs.getInt("wz3");
				int wz4 = rs.getInt("wz4");
				int wz5 = rs.getInt("wz5");
				eachDutyHours.set(0, wz1);
				eachDutyHours.set(1, wz2);
				eachDutyHours.set(2, wz3);
				eachDutyHours.set(3, wz4);
				eachDutyHours.set(4, wz5);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Vector<Float> eachDutyEnergy = new Vector<Float>();
		eachDutyEnergy.setSize(5);
		for(int i=0;i<eachDutyEnergy.size();i++)
			eachDutyEnergy.set(i, new Float(0));
		String energySql = "select wz,(sum(dl1)+sum(dl2)) as dl from pri_dljh t where rq like ? group by wz";
		rs = oc.query(energySql,params);
		try {
			while(rs.next()){
				eachDutyEnergy.set(rs.getInt("wz")-1, rs.getFloat("dl"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Vector<PlantMonthPowerOrRealTimeData> loadRate = new Vector<PlantMonthPowerOrRealTimeData>();
		loadRate.setSize(5);
		for(int i=0;i<eachDutyHours.size();i++){
			float temp =0;
			if(eachDutyHours.get(i).compareTo(new Integer(0))!=0)
				temp = eachDutyEnergy.get(i)/(eachDutyHours.get(i)*Tools.rongLiang/10*2);

			PlantMonthPowerOrRealTimeData object = new PlantMonthPowerOrRealTimeData();
			object.setName((i+1)+"值");
			object.setData(Tools.float2Format(temp*100, 2));
			
			loadRate.set(i,object);
		}
		JSONArray ja = new JSONArray();
		for(int i=0;i<loadRate.size();i++){
			JSONArray temp = new JSONArray();
			temp.add(loadRate.get(i).getName());
			temp.add(loadRate.get(i).getData());
			ja.add(temp);
		}
		
		
		JSONObject jo = new JSONObject();
		jo.put("monthLoadRate", ja);
		return jo;
	}
	

	public String getData(){
		
		JSONObject jo = new JSONObject();

		
		JSONObject generatorMap = getEachGenerator();
		JSONObject monthLoadRate  = getMonthLoadRate();

		jo.putAll(monthLoadRate);
		jo.putAll(generatorMap);
		return jo.toString();
		
		
		
		
		
	}
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
