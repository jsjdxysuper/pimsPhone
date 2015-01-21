package com.sgepm.threemainpage.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	private OracleConnection oc = new OracleConnection();
	private Logger log = LoggerFactory.getLogger(HoleGridServlet.class);
	private HashMap<String,Integer> sequence =new HashMap<String,Integer>();
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
	 * �ô˺�����ͳһ��get��post������д���
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
		log.debug("ȫ����Ϣ��ѯ����:"+date);
		String returnData =  getData();
		out.write(returnData);

		out.close();
	}
	
	public JSONObject getHoleGridLineData(){
		JSONObject jo = new JSONObject();
		
		//����ϸ��µ�ȫʡ����������
		ArrayList<Float> lastMonthal = new ArrayList<Float>();
		String projectSqlStr="select rq,sj from info_dmis_fdqk t where xmmc='ȫʡ����' and rq like ? order by rq";
		java.sql.Date nowDate;
		try {
			nowDate = java.sql.Date.valueOf(date);
		} catch (java.lang.IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			log.warn("�����������Ϊ��");
			return null;
		}
		Date lastMonthDate = Tools.getLastMonthDay(nowDate);
		String lastMonthDateStr = Tools.formatDate(lastMonthDate);
		String lastMonthDateWildStr = Tools.change2WildcardDate(lastMonthDateStr, Tools.time_span[2]);
		String []dataParas = {lastMonthDateWildStr};
		ResultSet rs= oc.query(projectSqlStr,dataParas);
		log.debug("��ѯ�ϸ��µ�ȫʡ������Ϣ,����:"+lastMonthDateWildStr);
		try {
			while(rs.next()){
				lastMonthal.add(rs.getFloat("sj"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jo.put("lastMonth",lastMonthal);
		
		//�������µ�ȫʡ����������
		ArrayList<Float> thisMonthAl = new ArrayList<Float>();
		dataParas[0] = Tools.change2WildcardDate(date, Tools.time_span[2]);
		rs = oc.query(projectSqlStr, dataParas);
		log.debug("��ѯ���µ�ȫʡ������Ϣ,����:"+dataParas[0]);
		try {
			while(rs.next()){
				thisMonthAl.add(rs.getFloat("sj"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jo.put("thisMonth",thisMonthAl);
		
		//���ȥ��ͬ���·�ȫʡ����������
		ArrayList<Float> lastYearAl = new ArrayList<Float>();
		
		Date lastYearDate = Tools.getLastYearDay(java.sql.Date.valueOf(date));
		String lastYearDateStr = Tools.formatDate(lastYearDate);
		String lastYearDateWildStr = Tools.change2WildcardDate(lastYearDateStr, Tools.time_span[2]);
		dataParas[0] = lastYearDateWildStr;
		rs = oc.query(projectSqlStr, dataParas);
		log.debug("��ѯȥ��ͬ�ڵ�ȫʡ������Ϣ,����:"+lastYearDateWildStr);
		try {
			while(rs.next()){
				lastYearAl.add(rs.getFloat("sj"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jo.put("lastYear",lastYearAl);
		
		return jo;
	}
	
	/**
	 * ���ȫʡ������Ϣ�ı������
	 * @return
	 */
	public JSONObject getHoleGridTableData(){
		String projectSqlStr="select xmmc,sj,ylj,nlj,ytb,ntb from info_dmis_fdqk t where rq like ? order by xmmc desc";
		HashMap<String,JSONArray> projectData = new HashMap<String,JSONArray>();
		JSONArray jsArray = new JSONArray();
		JSONObject jo = new JSONObject();
		Vector<ArrayList<String>> vector = new Vector<ArrayList<String>>();
		vector.setSize(6);
		String []dataParas = {date};
		ResultSet rs= oc.query(projectSqlStr,dataParas);
		try {
			while(rs.next()){
				ArrayList<String>temp = new ArrayList<String>();
				String xmmc = rs.getString("xmmc");
				String sj = Tools.float2Format(rs.getFloat("sj"));
				String ylj = Tools.float2Format(rs.getFloat("ylj"));
				String nlj = Tools.float2Format(rs.getFloat("nlj"));
				String ytb = Tools.float2Format(rs.getFloat("ytb"));
				String ntb = Tools.float2Format(rs.getFloat("ntb"));

				temp.add(xmmc);
				temp.add(sj);
				temp.add(ylj);
				temp.add(nlj);
				temp.add(ytb);
				temp.add(ntb);
				if(sequence.get(xmmc)!=null){
					int i = sequence.get(xmmc).intValue();
					vector.set(i,temp);
					//jsArray.add(i,value);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		jsArray.addAll(vector);
		jo.put("data", jsArray);
		return jo;
	}

	/**
	 * �����Ҫ���ؿͻ��˵��ַ���
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

		log.debug("ͨ��doGet����");
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

		log.debug("ͨ��doPost����");
		doRequest(request,response);
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		//= {"ȫʡ����","ֱ�����","ֱ��ˮ��","ֱ�����","ֱ���˵�","�����߾���"};
		sequence.put("ȫʡ����", new Integer(0));
		sequence.put("ֱ�����", new Integer(1));
		sequence.put("ֱ��ˮ��", new Integer(2));
		sequence.put("ֱ�����", new Integer(3));
		sequence.put("ֱ���˵�", new Integer(4));
		sequence.put("�����߾��ܵ�", new Integer(5));
	}

}
