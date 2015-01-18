package com.sgepm.threemainpage.servlet;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sgepm.Tools.Tools;


@WebServlet(name="PlantServlet",urlPatterns="/PlantServlet")
public class PlantServlet extends HttpServlet {

	private String date;
	private String dateWildcard;
	private String plantListStr[];
	private ResourceBundle properties = ResourceBundle.getBundle("pimsphone");;
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
		date = request.getParameter("date");
		dateWildcard = Tools.change2WildcardDate(date, Tools.time_span[2]);
		HashMap<String,Vector<Float>> plantLineData;
		plantLineData = getPlantLineData();
		jo.putAll(plantLineData);
		out.write(jo.toString());
		out.close();
	}
	

	public HashMap<String,Vector<Float>> getPlantLineData() {
		
		HashMap<String,Vector<Float>> retData = new HashMap<String,Vector<Float>>();
		ArrayList<String> generatorList = new ArrayList<String>();

		InputStream in;
		
		String plantsStr = properties.getString("pims.plant.graph1.plants");
		plantListStr = plantsStr.split(",");
		
		//对存储电厂发电量的Vector进行初始化
		Vector<Float> tempVector = new Vector<Float>();
		int dayNum = Tools.getMonthDayNum(date);
		tempVector.setSize(dayNum);

		//由一个电厂Vector克隆其他电厂Vector,那就全部初始化了
		for(int i=0;i<plantListStr.length;i++){			
			retData.put(plantListStr[i],(Vector<Float>) tempVector.clone());
		}
		log.debug("PlantLineData电厂列表:"+plantsStr);


		//获得每个电厂的机组列表		
		for(int i=0;i<plantListStr.length;i++){
				String generators = properties.getString("pims.plant.graph1."+plantListStr[i]);
				
				String gArray[] = generators.split(",");
				for(int j=0;j<gArray.length;j++){
					generatorList.add(gArray[j]);
				}
		}
		
		
		
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
		
		return retData;
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
		// Put your code here
		//JZBM2DCMCDictionary
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
