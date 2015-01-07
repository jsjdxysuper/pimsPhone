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
		String sqlStr="select RQ,SJ,YG from info_data_dcyg t where DCMC='沈阳康平电厂'AND RQ like ? ORDER BY RQ,SJ";
		String para;
		float max,min,average,energy,time_use,sum,time;
		
		min = Float.MAX_VALUE;
		max = average=energy=time_use=sum=0;
		char[]temp = date.toCharArray();
		if(time_span.compareTo(Tools.time_span[0])==0){//实时

		}else if(time_span.compareTo(Tools.time_span[1])==0){//年
			temp[5]='%';
			temp[6]='%';
			temp[8]='%';
			temp[9]='%';
		}else if(time_span.compareTo(Tools.time_span[2])==0){//月
			temp[8]='%';
			temp[9]='%';
		}else if(time_span.compareTo(Tools.time_span[3])==0){//日
			
		}
		para = String.copyValueOf(temp);
		String []a={para};
		System.out.println("time_span"+a[0]);
		ResultSet rs= oc.query(sqlStr,a);
		JSONObject jo = new JSONObject();
		ArrayList<String> rq = new ArrayList<String>();
		ArrayList<String> sj = new ArrayList<String>();
		ArrayList<String> yg = new ArrayList<String>();

		
		try {
			while(rs.next()){
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
		if(yg.size()==0)
			return null;
		average = sum/yg.size();
		energy   = sum/120;
		time    = yg.size()/12;
		time_use = energy/Tools.rong_liang*10;

		//保留两位小数
		DecimalFormat form = new DecimalFormat("##0.00");
		
		jo.put("max",form.format(max));
		jo.put("min", form.format(min));
		jo.put("average", form.format(average));
		jo.put("energy",form.format(energy));
		jo.put("time_use", form.format(time_use));
		
		
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
