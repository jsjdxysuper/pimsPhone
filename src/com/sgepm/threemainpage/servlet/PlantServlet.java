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
import com.sgepm.Tools.Tools;
import com.sgepm.threemainpage.entity.PlantMonthData;


@WebServlet(name="PlantServlet",urlPatterns="/PlantServlet")
public class PlantServlet extends HttpServlet {

	private String date;
	private String dateWildcard;
	
	private ResourceBundle properties = ResourceBundle.getBundle("pimsphone");
	//机组编码到机组所属电厂名称的查询字典,Map<String,String>第一个String为机组编码，第二个String为所属电厂名称
	private HashMap<String,String> JZBM2DCMCDictionary = new HashMap<String,String>();
	
	
	private OracleConnection oc = new OracleConnection();
	private Logger log = LoggerFactory.getLogger(HoleGridServlet.class);
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
		
		
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		date = request.getParameter("date");
		dateWildcard = Tools.change2WildcardDate(date, Tools.time_span[2]);
		
		ja = getPlantLineData();
		jo.put("lineData",ja);
		out.write(jo.toString());
		out.close();
	}
	

	/**
	 * 获得电厂页面折线图的数据
	 * @return 返回包含PlantMonthData对象的JSON数组
	 * 结构如下
	 * [
	 * {name:"铁岭厂",data:[21,123,213.....]},
	 * {name:"沈阳康平电厂",data:[...]},
	 * {}....
	 * ]
	 */
	public JSONArray getPlantLineData() {
		
		String plantListStr[];//折线图所要显示的电厂列表
		//包含电厂发电量信息的Vector,其中每个对象代表一个电厂
		Vector<PlantMonthData> plantVectorData = new Vector<PlantMonthData>();
		//包含电厂发电量信息的Map,String为电厂名称,Vector为电厂发电量信息,其索引为所在月度的日期
		HashMap<String,Vector<Float>> retData  = new HashMap<String,Vector<Float>>();
		//电厂的所配置的机组的集合(配置在pimsphone.properties文件中）
		ArrayList<String> generatorList        = new ArrayList<String>();

		
		String plantsStr = properties.getString("pims.plant.graph1.plants");
		plantListStr = plantsStr.split(",");
		
		//根据所查日期的月份包含的天数，初始化Vector的size
		Vector<Float> tempVector = new Vector<Float>();
		int dayNum               = Tools.getMonthDayNum(date);
		tempVector.setSize(dayNum);

		//因为是同一个月度不同电厂的发电量的对比，所以每个Vector（电厂）的size（天数）是相同的
		for(int i=0;i<plantListStr.length;i++){			
			retData.put(plantListStr[i],(Vector<Float>) tempVector.clone());
		}
		log.debug("PlantLineData电厂列表:"+plantsStr);


		//获得每个电厂所配置的机组列表		
		for(int i=0;i<plantListStr.length;i++){
				String generators = properties.getString("pims.plant.graph1."+plantListStr[i]);
				
				String gArray[] = generators.split(",");
				for(int j=0;j<gArray.length;j++){
					generatorList.add(gArray[j]);
				}
		}
		
		
		//装配sql语句,in子句的最大数目为1000,足够了
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
		String sql = "select t.rq,sum(t.rdl) as rdl,b.ssdcmc from info_dmis_zdhcjz t,base_jzbm b where t.jzbm in ("
						+inString+" )"+
						" and rq like ? and t.jzbm=b.jzbm group by ssdcmc,rq order by ssdcmc,rq";
		ResultSet rs=  oc.query(sql,params);

		Vector<Float> temp = new Vector<Float>();
		try {
			while(rs.next()){

				String rq = rs.getString("rq");
				String ssdcmc = rs.getString("ssdcmc");
				float rdl = rs.getFloat("rdl");
				
				temp = retData.get(ssdcmc);
				int index = Integer.parseInt(rq.substring(8))-1;
				temp.set(index, rdl);
				//retData
				log.debug(rq+","+ssdcmc+","+rdl);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String key : retData.keySet()) {

			plantVectorData.add(new PlantMonthData(key,retData.get(key)));
		}
		JSONArray ja = new JSONArray();
		ja.addAll(plantVectorData);
		return ja;
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

		//建立机组编码到机组所属电厂名称的查询字典,Map<String,String>第一个String为机组编码，第二个String为所属电厂名称
		String sqlGenerator2Plant = "select jzbm,ssdcmc from base_jzbm t";
		ResultSet rs= oc.query(sqlGenerator2Plant,null);		
		try {
			while(rs.next()){
				JZBM2DCMCDictionary.put(rs.getString("JZBM"), rs.getString("SSDCMC"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
