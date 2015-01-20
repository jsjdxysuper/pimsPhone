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
import com.sgepm.threemainpage.entity.PlantMonthAccumulateData;
import com.sgepm.threemainpage.entity.PlantMonthAccumulateDataSeries;
import com.sgepm.threemainpage.entity.PlantMonthData;


@WebServlet(name="PlantServlet",urlPatterns="/PlantServlet")
public class PlantServlet extends HttpServlet {

	private String date;
	private String dateWildcard;
	private String plantIdentity = "sykpp";
	
	private ResourceBundle properties = ResourceBundle.getBundle("pimsphone");
	//������뵽���������糧���ƵĲ�ѯ�ֵ�,Map<String,String>��һ��StringΪ������룬�ڶ���StringΪ�����糧����
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
	 * ͳһ���doGet��doPost������
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
		
		
		JSONObject joAll = new JSONObject();
		
		JSONArray ja = new JSONArray();
		date = request.getParameter("date");
		dateWildcard = Tools.change2WildcardDate(date, Tools.time_span[2]);
		
		ja = getOneMonthPowerData();
		
		JSONObject joSeries = getEveryMonthPowerData();	
		JSONObject plantProgress = getProgressData();
		joAll.accumulate("plantProgressData", plantProgress);
		joAll.accumulateAll(joSeries);
		joAll.put("columnData",ja);
		
		String ret = joAll.toString();
		ret = Tools.replacePlantName(ret, PimsTools.getPlantAbbrDic());
		ret = Tools.getAbbrNameOfPlant(ret);
		out.write(ret);
		
		out.close();
	}
	

	/**
	 * ��õ糧ҳ������ͼ������
	 * @return ���ذ���PlantMonthData�����JSON����
	 * �ṹ����
	 * [
	 * {name:"���볧",data:[21,123,213.....]},
	 * {name:"������ƽ�糧",data:[...]},
	 * {}....
	 * ]
	 */
	public JSONArray getOneMonthPowerData() {
		
		String plantListStr[];//����ͼ��Ҫ��ʾ�ĵ糧�б�
		//�����糧��������Ϣ��Vector,����ÿ���������һ���糧
		Vector<PlantMonthData> plantVectorData = new Vector<PlantMonthData>();

		//�糧�������õĻ���ļ���(������pimsphone.properties�ļ��У�
		ArrayList<String> generatorList        = new ArrayList<String>();

		
		String plantsStr = properties.getString("pims.plant.graph1.plants");
		plantListStr = plantsStr.split(",");
		plantVectorData.setSize(plantListStr.length);
		//����ʼ��Ϊ0������ĳ���糧������Ϊ0ʱ����������Ҳ��������
		for(int i=0;i<plantVectorData.size();i++){
			PlantMonthData temp = new PlantMonthData();
			temp.setName(plantListStr[i]);
			temp.setData(new Float(0));
			plantVectorData.set(i, temp);
		}

		log.debug("PlantLineData�糧�б�:"+plantsStr);


		//���ÿ���糧�����õĻ����б�		
		generatorList = PimsTools.getGeneratorsList("pims", "plant", "graph1");

		
		
		//װ��sql���,in�Ӿ�������ĿΪ1000,�㹻��
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
		ResultSet rs=  oc.query(sql,params);

		try {
			while(rs.next()){
				
				String ssdcmc = rs.getString("ssdcmc");
				float rdl = rs.getFloat("rdl");
				for(int i=0;i<plantVectorData.size();i++){
					PlantMonthData temp = plantVectorData.get(i);
					if(temp.getName().compareTo(ssdcmc)==0)
						temp.setData(rdl);
				}

				log.debug(ssdcmc+","+rdl);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONArray ja = new JSONArray();
		for(int i=0;i<plantVectorData.size();i++){
			JSONArray temp = new JSONArray();
			temp.add(plantVectorData.get(i).getName());
			temp.add(plantVectorData.get(i).getData());
			ja.add(temp);
		}
		return ja;
	}
	
	public JSONObject getEveryMonthPowerData(){
//		select substr(t.rq,0,7),sum(t.rdl) as rdl,b.ssdcmc from info_dmis_zdhcjz t,base_jzbm b where t.jzbm in (
//				'sykppg1','sykppg2','tlpg5','tlpg6','ykpg3','ykpg4','dlzhpg1','dlzhpg2','tlqhpg1','tlqhpg9','cyyshpg1','cyyshpg2')
//				and rq <= '2014-10-11' and rq >= '2014-01-01' and t.jzbm=b.jzbm group by ssdcmc,substr(t.rq,0,7) order by ssdcmc
		//�ֶ���ͼ��Ҫ��ʾ�ĵ糧�б�
		int numPlant = 0;
		int monthNum = Integer.parseInt(date.substring(5, 7));
		//�����糧��������Ϣ��Vector,����ÿ���������һ���糧
		Vector<PlantMonthAccumulateDataSeries> plantVectorDataseries = new Vector<PlantMonthAccumulateDataSeries>();

		//�糧�������õĻ���ļ���(������pimsphone.properties�ļ��У�
		ArrayList<String> generatorList        = new ArrayList<String>();
		
		String plantsStr = properties.getString("pims.plant.graph2.plants");
		String plantListStr[] = plantsStr.split(",");
		numPlant = plantListStr.length;
		plantVectorDataseries.setSize(monthNum);

		for(int i=0;i<plantVectorDataseries.size();i++){
			PlantMonthAccumulateDataSeries temp = new PlantMonthAccumulateDataSeries();
			Vector<Float> dataTemp = new Vector<Float>();
			dataTemp.setSize(numPlant);
			temp.setData(dataTemp);
			temp.setName(i+1);
			plantVectorDataseries.set(i, temp);
		}

		log.debug("PlantLineData�糧�б�:"+plantsStr);
		generatorList = PimsTools.getGeneratorsList("pims", "plant", "graph2");

		
		
		//װ��sql���,in�Ӿ�������ĿΪ1000,�㹻��
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
		ResultSet rs=  oc.query(sql,params);
		
		try {
			while(rs.next()){
				String yf = rs.getString("yf");
				int yfInt = Integer.parseInt(yf.substring(5, 7));
				float ydl = rs.getFloat("ydl");
				String ssdcmc = rs.getString("ssdcmc");
				
				PlantMonthAccumulateDataSeries temp = plantVectorDataseries.get(yfInt-1);
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
		
		JSONObject jo = new JSONObject();
		jo.put("seriesPlantName", plantListStr);
		jo.put("everyMonthPowerSeries", plantVectorDataseries);
		
		return jo;
	}
	
	/**
	 * ��õ糧������Ϣͼ������
	 * @return
	 */
	public JSONObject getProgressData(){
		//select substr(rq,0,7),sum(rdl) from info_dmis_zdhcdc t where dcbm='sykpp' and rq>='2014-01-01' and rq <='2014-03-12' group by substr(rq,0,7)
		//select t.*, t.rowid from info_sdlr_njh t
		String startDate = Tools.getFirstDateInYear(date);
		float yearPlan = 0;
		Vector<Float> monthPlan = new Vector<Float>();
		Vector<Float> monthFinish = new Vector<Float>();
		float yearAccumulate = 0;
		
		String sqlGetYearPlan = "select t.nf,t.njh  from info_sdlr_dcnjh t where t.dcbm = ? and t.nf = ?";
		String params1[] = {plantIdentity,date.substring(0,4)};
		ResultSet rs=  oc.query(sqlGetYearPlan,params1);
		try {
			while(rs.next())
				yearPlan = rs.getFloat("njh");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//���ÿ���µļƻ���
		for(int i=0;i<12;i++){
			monthPlan.add((i+1)*yearPlan/12);
		}
		
		
		String sqlGetMonthPower = "select substr(rq,0,7) as yf,sum(rdl) as ylj from info_dmis_zdhcdc t "+
				"where dcbm='sykpp' and rq >= ? and rq <= ? group by substr(rq,0,7) order by yf";
		String params2[] = {startDate,date};
		rs=  oc.query(sqlGetMonthPower,params2);
		try {
			while(rs.next()){
				String yf = rs.getString("yf");
				float ylj = rs.getFloat("ylj");
				
				yearAccumulate += ylj;
				monthFinish.add(yearAccumulate);
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

		//����������뵽���������糧���ƵĲ�ѯ�ֵ�,Map<String,String>��һ��StringΪ������룬�ڶ���StringΪ�����糧����
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
