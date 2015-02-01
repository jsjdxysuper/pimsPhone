package com.sgepm.threemainpage.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.sgepm.Tools.OracleConnection;
import com.sgepm.Tools.Tools;

@WebServlet(name="HoleGridServlet",urlPatterns="/HoleGridServlet")
public class HoleGridServlet extends HttpServlet {

	private String date;
	private String dateWildcard;
	private Logger log                       = LoggerFactory.getLogger(HoleGridServlet.class);
	private HashMap<String,Integer> sequence = new HashMap<String,Integer>();
	private int tableRows                    = 6;
	private int tableColumns                 = 6;
	private Vector<String> tableFirstColumn  = new Vector<String>();
	/**
	 * Constructor of the object.
	 */
	public HoleGridServlet() {
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
	 * 用此函数来统一对get和post请求进行处理
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void doRequest(HttpServletRequest request, HttpServletResponse response) 
			throws IOException{
		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8") ;
		response.setCharacterEncoding("UTF-8") ;
		PrintWriter out = response.getWriter();

		date = request.getParameter("date");
		if(date==null || (date.compareTo("")==0))return;
		dateWildcard = Tools.change2WildcardDate(date, Tools.time_span[2]);
		log.debug("全网信息查询日期:"+date);
		String returnData =  getData();
		out.write(returnData);

		out.close();
	}
	
	
	/**
	 * 获取全网月度发电量信息
	 * @return
	 */
	public JSONObject getHoleGridLineData(){
		
		
		OracleConnection oc = new OracleConnection();
		JSONObject jo = new JSONObject();
		
		//获得上个月的全省发电量数据
		ArrayList<Float> lastMonthAL = new ArrayList<Float>();
		String projectSqlStr="select rq,sj from info_dmis_fdqk t where xmmc='全省发电' and rq like ? order by rq";
		java.sql.Date nowDate;
		try {
			nowDate = java.sql.Date.valueOf(date);
		} catch (java.lang.IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			log.warn("日期输入参数为空");
			return null;
		}
		Date lastMonthDate = Tools.getLastMonthDay(nowDate);
		String lastMonthDateStr = Tools.formatDate(lastMonthDate);
		String lastMonthDateWildStr = Tools.change2WildcardDate(lastMonthDateStr, Tools.time_span[2]);
		String []dataParas = {lastMonthDateWildStr};
		ResultSet rs= oc.query(projectSqlStr,dataParas);
		log.debug("查询上个月的全省发电信息,日期:"+lastMonthDateWildStr);
		try {
			while(rs.next()){
				lastMonthAL.add(rs.getFloat("sj"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jo.put("lastMonth",lastMonthAL);
		
		//获得这个月的全省发电量数据
		ArrayList<Float> thisMonthAl = new ArrayList<Float>();
		dataParas[0] = Tools.change2WildcardDate(date, Tools.time_span[2]);
		rs = oc.query(projectSqlStr, dataParas);
		log.debug("查询本月的全省发电信息,日期:"+dataParas[0]);
		try {
			while(rs.next()){
				thisMonthAl.add(rs.getFloat("sj"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jo.put("thisMonth",thisMonthAl);
		
		//获得去年同期月份全省发电量数据
		ArrayList<Float> lastYearAl = new ArrayList<Float>();
		
		Date lastYearDate = Tools.getLastYearDay(java.sql.Date.valueOf(date));
		String lastYearDateStr = Tools.formatDate(lastYearDate);
		String lastYearDateWildStr = Tools.change2WildcardDate(lastYearDateStr, Tools.time_span[2]);
		dataParas[0] = lastYearDateWildStr;
		rs = oc.query(projectSqlStr, dataParas);
		log.debug("查询去年同期的全省发电信息,日期:"+lastYearDateWildStr);
		try {
			while(rs.next()){
				lastYearAl.add(rs.getFloat("sj"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jo.put("lastYear",lastYearAl);
		
		oc.closeAll();
		return jo;
	}
	
	/**
	 * 获得全省发电信息的表格数据
	 * @return返回值格式如下:
	 * "data":[["全省发电","43143.00","50.33","1344.65","4.84","6.35"],
	 * ["直调火电","31179.00","37.66","988.59","6.28","4.50"],
	 * ["直调水电","79.00","0.07","8.59","-33.87","-52.81"],
	 * ["直调风电","2568.00","2.19","48.85","70.26","4.21"],
	 * ["直调核电","2667.00","3.13","114.57","-11.65","96.13"],
	 * ["联络线净受电","16893.00","20.66","559.10","-4.19","-7.17"]]
	 */
	public JSONObject getHoleGridTableData(){
		OracleConnection oc = new OracleConnection();
		String projectSqlStr="select xmmc,sj,ylj,nlj,ytb,ntb from info_dmis_fdqk t where rq like ? order by xmmc desc";
		HashMap<String,JSONArray> projectData = new HashMap<String,JSONArray>();
		JSONArray jsArray = new JSONArray();
		JSONObject jo = new JSONObject();
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
		
		
		String []dataParas = {date};
		ResultSet rs= oc.query(projectSqlStr,dataParas);
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

		jsArray.addAll(vector);
		jo.put("data", jsArray);
		
		oc.closeAll();
		return jo;
	}

	/**
	 * 获得需要返回客户端的字符串
	 * @return
	 */
	public String getData(){

		JSONObject jo = new JSONObject();
		
		JSONObject lineJO = getHoleGridLineData();
		JSONObject tableJO = getHoleGridTableData();
		
		
		jo.putAll(lineJO);
		jo.putAll(tableJO);
		return jo.toString();
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

		log.debug("通过doGet方法");
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

		log.debug("通过doPost方法");
		doRequest(request,response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		//= {"全省发电","直调火电","直调水电","直调风电","直调核电","联络线净受"};

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

}
