package com.sgepm.threemainpage.servlet;

import java.awt.List;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sgepm.threemainpage.servlet.Tools;
import com.sun.xml.internal.fastinfoset.util.CharArray;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet(name="GeneratorServlet",urlPatterns="/GeneratorServlet")
public class GeneratorServlet extends HttpServlet {

	private String date;
	private String time_span;
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

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8") ;
		response.setCharacterEncoding("UTF-8") ;
		PrintWriter out = response.getWriter();
		date = request.getParameter("date");
		time_span = request.getParameter("time_span");
		System.out.println("date:"+date+",time_span:"+time_span);
		String returnData =  getData();
		out.write(returnData);
		out.close();
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

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8") ;
		response.setCharacterEncoding("UTF-8") ;
		PrintWriter out = response.getWriter();
		date = request.getParameter("date");
		//改变日期的格式为YYYY-MM-DD
		date = Tools.formatDate(date);
		time_span = request.getParameter("time_span");
		System.out.println("date:"+date+",time_span:"+time_span);
		String returnData =  getData();
		//为了避免查询数据为空
		if(returnData!=null)
			out.write(returnData);
		out.close();
	}

	
	public String getData(){
		
		OracleConnection oc = new OracleConnection();
		String plantSqlStr="select RQ,SJ,YG from info_data_dcyg t where DCMC='沈阳康平电厂'AND RQ like ? ORDER BY RQ,SJ";
		String dataPara;
		float max,min,average,energy,timeUse,sum,timeOfHours;
		
		min             = Float.MAX_VALUE;
		max             = 0;
		average         = 0;
		energy          = 0;
		timeUse        = 0;
		sum             = 0;
		timeOfHours     = 0;
		
		char[]dateSplit = date.toCharArray();
		if(time_span.compareTo(Tools.time_span[0])==0){//实时

		}else if(time_span.compareTo(Tools.time_span[1])==0){//年
			dateSplit[5]='%';
			dateSplit[6]='%';
			dateSplit[8]='%';
			dateSplit[9]='%';
		}else if(time_span.compareTo(Tools.time_span[2])==0){//月
			dateSplit[8]='%';
			dateSplit[9]='%';
		}else if(time_span.compareTo(Tools.time_span[3])==0){//日
			
		}
		dataPara = String.copyValueOf(dateSplit);
		String []dataParas={dataPara};
		System.out.println("time_span"+dataParas[0]);
		ResultSet rs= oc.query(plantSqlStr,dataParas);
		JSONObject jo = new JSONObject();
		ArrayList<String> rq = new ArrayList<String>();
		ArrayList<String> sj = new ArrayList<String>();
		ArrayList<String> yg = new ArrayList<String>();

		//recordCount与rq.size()是有区别的，某时刻有功为零不计入rqlist，单是recordCount里面会计数
		long recordCount = 0;
		try {
			while(rs.next()){
				recordCount++;
				String aa = rs.getString(1);
				String bb = rs.getString(2);
				String cc = rs.getString(3);
				if(cc==null||cc.compareTo("")==0||cc.compareTo("null")==0)
					continue;
				rq.add(aa);
				sj.add(bb);
				yg.add(cc);
				float yg_float = Float.valueOf(cc);
				if(yg_float>max)max=yg_float;
				if(yg_float<min)min=yg_float;
				sum=sum+yg_float;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//为了避免查询数据为空
		if(recordCount==0)
		{
			average     = 0;
			energy      = 0;
			timeOfHours = 0;
			timeUse     = 0;
			max			= 0;
			min         = 0;
		}else{
			average = sum/recordCount;
			energy   = sum/120;
			timeOfHours    = recordCount/12;
			timeUse = energy/Tools.rongLiang*10;
		}
		//保留两位小数
		DecimalFormat form = new DecimalFormat("##0.00");
		
		jo.put("max",form.format(max));
		jo.put("min", form.format(min));
		jo.put("average", form.format(average));
		jo.put("energy",form.format(energy));
		jo.put("timeUse", form.format(timeUse));
		jo.put("recordCount", String.valueOf(recordCount));
		jo.put("timeOfHours", form.format(timeOfHours));
		
		
		/**
		 * 以下代码为康平“机组”信息的查询
		 */
		float g1Max,g1Min,g1Average,g1Energy,g1TimeUse,g1Sum,g1TimeOfHours;
		float g2Max,g2Min,g2Average,g2Energy,g2TimeUse,g2Sum,g2TimeOfHours;
		
		g1Min = Float.MAX_VALUE;
		g2Min = Float.MAX_VALUE;
		g1Max=g1Average=g1Energy=g1TimeUse=g1Sum=g1TimeOfHours=0;
		g2Max=g2Average=g2Energy=g2TimeUse=g2Sum=g2TimeOfHours=0;
		
		String generatorSqlStr="select JZBM,YG from info_data_jzyg t WHERE JZMC IN ('沈阳康平#1机','沈阳康平#2机') AND RQ LIKE ? ORDER BY RQ,SJ";
		rs = oc.query(generatorSqlStr,dataParas);

		ArrayList<String> g1_yg = new ArrayList<String>();
		ArrayList<String> g2_yg = new ArrayList<String>();
		
		long g1RecordCount = 0, g2RecordCount = 0;
		try {
			while(rs.next()){
				
				String bb = rs.getString(1);
				String cc = rs.getString(2);
				if(cc==null||cc.compareTo("")==0||cc.compareTo("null")==0)
					continue;
				float g1_yg_float,g2_yg_float;
				if(bb.compareTo("sykppg1")==0){
					g1RecordCount++;
					g1_yg.add(cc);
					g1_yg_float                =  Float.valueOf(cc);
					if(g1_yg_float>g1Max)g1Max =  g1_yg_float;
					if(g1_yg_float<g1Min)g1Min =  g1_yg_float;
					g1Sum                      += g1_yg_float;
				}
				else if(bb.compareTo("sykppg2")==0){
					g2RecordCount++;
					g2_yg.add(cc);
					g2_yg_float                =  Float.valueOf(cc);
					if(g2_yg_float>g2Max)g2Max =  g2_yg_float;
					if(g2_yg_float<g2Min)g2Min =  g2_yg_float;
					g2Sum                      += g2_yg_float;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//为了避免查询数据为空
		if(g1RecordCount==0)
		{
			g1Min = 0;			
			g1Max=g1Average=g1Energy=g1TimeUse=g1Sum=g1TimeOfHours=0;
			
		}else
		{
			g1Average = g1Sum/recordCount;
			g1Energy   = g1Sum/120;
			g1TimeOfHours    = g1RecordCount/12;
			g1TimeUse = g1Energy/Tools.rongLiang*10;
		}
		
		if(g2RecordCount==0){
			g2Min = 0;
			g2Max=g2Average=g2Energy=g2TimeUse=g2Sum=g2TimeOfHours=0;
		}else
		{
			g2Average = g2Sum/recordCount;
			g2Energy   = g2Sum/120;
			g2TimeOfHours    = g2RecordCount/12;
			g2TimeUse = g2Energy/Tools.rongLiang*10;
		}	

		
		jo.put("g1Max",form.format(g1Max));
		jo.put("g1Min",form.format(g1Min));
		jo.put("g1Average",form.format(g1Average));
		jo.put("g1Energy",form.format(g1Energy));
		jo.put("g1TimeOfHours",form.format(g1TimeOfHours));
		jo.put("g1TimeUse",form.format(g1TimeUse));
		
		jo.put("g2Max",form.format(g2Max));
		jo.put("g2Min",form.format(g2Min));
		jo.put("g2Average",form.format(g2Average));
		jo.put("g2Energy",form.format(g2Energy));
		jo.put("g2TimeOfHours",form.format(g2TimeOfHours));
		jo.put("g2TimeUse",form.format(g2TimeUse));
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
