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
import com.sgepm.threemainpage.entity.Plant60GenPower;
import com.sgepm.threemainpage.entity.PlantYearAccumulateDataSeries;
import com.sgepm.threemainpage.entity.PlantMonthEnergyOrRealTimeData;


@WebServlet(name="PlantServlet",urlPatterns="/PlantServlet")
public class PlantServlet extends HttpServlet {

	private String date;
	private String dateWildcard;
	private String jzbm = "sykpp";
	
	private ResourceBundle properties                  = ResourceBundle.getBundle("pimsphone");
	//������뵽���������糧���ƵĲ�ѯ�ֵ�,Map<String,String>��һ��StringΪ������룬�ڶ���StringΪ�����糧����
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
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {

		OracleConnection oc = new OracleConnection();
		//����������뵽���������糧���ƵĲ�ѯ�ֵ�,Map<String,String>��һ��StringΪ������룬�ڶ���StringΪ�����糧����
		String sqlGenerator2Plant = "select jzbm,ssdcmc from base_jzbm t";
		log.debug("sql��ѯ:"+sqlGenerator2Plant+"\n");
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
		
		date                     = request.getParameter("date");
		dateWildcard             = Tools.change2WildcardDate(date, Tools.time_span[2]);
		
		JSONObject plant60GenPower = getPlant60GenPower();
		
		//ҳ��ˢ�£���ʷ��ʵʱ����ȫ����ȡ
		if(request.getParameter("realtime")==null){
		
			JSONObject joAll             = new JSONObject();//Ҫ���صĵ糧ҳ���ȫ������	
			JSONArray jaPlantsMonthEnergy = new JSONArray();//���糧�¶ȷ�����	
			jaPlantsMonthEnergy           = getOneMonthEnergyData();
			
			JSONObject joPlantsYearAccEnergy      = getYearAccumulatePowerData();//��ص糧60��������ۼƵ����Ա�����		

			joAll.accumulateAll(joPlantsYearAccEnergy);
			joAll.put("columnData",jaPlantsMonthEnergy);
			
			joAll.put("plant60GenPower", plant60GenPower);
			
			String ret = joAll.toString();
			out.write(ret);
		}
		//Ϊ����Ӧÿ���ӵĶ�ʱˢ��ʵʱ��������,ֻ���س�������
		else if(request.getParameter("realtime").compareTo("true")==0){
			log.debug("realtime request");
			JSONObject realTimeRetJO = new JSONObject();
			realTimeRetJO.put("plant60GenPower", plant60GenPower);
			out.write(realTimeRetJO.toString());
		}
		
		out.close();
	}
	
	public JSONObject getPlant60GenPower(){
		
		log.debug("����"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		
		//��ɵ糧��������֮����໥ת��
		String DCBMs[] = {"a0","b0","c0","d0","e0","f0"};
		HashMap<String,Integer>str2index = new HashMap<String,Integer>();
		str2index.put(DCBMs[0], 0);
		str2index.put(DCBMs[1], 1);
		str2index.put(DCBMs[2], 2);
		str2index.put(DCBMs[3], 3);
		str2index.put(DCBMs[4], 4);
		str2index.put(DCBMs[5], 5);
		
		Vector<Plant60GenPower> retData = new Vector<Plant60GenPower>();
		//60����������糧������
		final int PlantNum = 6;
		for(int i=0;i<PlantNum;i++){
			retData.add(new Plant60GenPower());
			retData.get(i).setPlantName(DCBMs[i]);
		}
		
		
		
		String sql = "select c1 rq,c2 sj,c3 dcbm,c4 yg from t001 t where c1 = ? order by  rq,dcbm,sj";
		String params[] = {date};
		
		OracleConnection oc = new OracleConnection();
		ResultSet rs = oc.query(sql, params);
		
		
		//�й��������Сֵ��Ϊ������������������С
		double min = Double.MAX_VALUE;
		double max = 0;
		try {
			while(rs.next()){
				String dcbm = rs.getString("dcbm");
				String  rq  = rs.getString("rq");
				String  sj  = rs.getString("sj");
				double  yg  = rs.getDouble("yg");

				if(yg > max) max = yg;
				if(yg < min) min = yg;
				retData.get(str2index.get(dcbm)).setTimes(sj);
				if(Math.abs(yg)<Tools.DOUBLE_MIN)
					retData.get(str2index.get(dcbm)).setPowers(null);
				else
					retData.get(str2index.get(dcbm)).setPowers(yg);
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//�����ѯ���Ϊ��
		if(min == Double.MAX_VALUE)min = 0;
		
		oc.closeAll();
		
		//���ݿ��еĵ糧������糧����ĸ�Ķ�Ӧ
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
		
		JSONObject jo = new JSONObject();
		for(int i=0;i<PlantNum;i++){
			jo.put(retData.get(i).getPlantName(), retData.get(i).getPowers());
			jo.put(retData.get(i).getPlantName()+"Times", retData.get(i).getTimes());
		}
		jo.put("maxRealtime", max);
		jo.put("minRealtime", min);
		return jo;
	}
	
	
	
	/**
	 * ��õ糧ҳ��ĳ���ۼƵ�����ͼ������
	 * @return ���ذ���PlantMonthData�����JSON����
	 * �ṹ����
	 * "columnData":[["��ƽ��",26242],["���볧",26434],["Ӫ�ڳ�",22435],["ׯ�ӳ�",25299],["��ӳ�",36604],["��ɽ��",27705]]
	 */
	public JSONArray getOneMonthEnergyData() {
		
		log.debug("����"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
			
		String plantsStr = properties.getString("pims.plant.graph1.plants");
		
		String plantListStr[];//��ͼ��Ҫ��ʾ�ĵ糧�б�
		plantListStr = plantsStr.split(",");
		


		log.debug("PlantsOneMonthEnergyData�糧�б�:"+plantsStr);

		//�糧�������õĻ���ļ���(������pimsphone.properties�ļ��У�
		ArrayList<String> generatorList        = new ArrayList<String>();
		//���ÿ���糧�����õĻ����б�		
		generatorList = PimsTools.getGeneratorsList("pims", "plant", "graph1");

		//װ��sql����л����б�,�Զ��ŷָ�,in�Ӿ�������ĿΪ1000,�㹻��
		assert generatorList.size()<1000:"sql inSubStr len is "+generatorList.size();
		
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
		log.debug("sql��ѯ:"+sql+"\n������"+dateWildcard);
		
		
		//�����糧��������Ϣ��Vector,����ÿ���������һ���糧
		Vector<PlantMonthEnergyOrRealTimeData> plantVectorData = new Vector<PlantMonthEnergyOrRealTimeData>();
		plantVectorData.setSize(plantListStr.length);
		//����ʼ��Ϊ0������ĳ���糧������Ϊ0ʱ����������Ҳ��������
		for(int i=0;i<plantVectorData.size();i++){
			PlantMonthEnergyOrRealTimeData onePlantMonthEnergy = new PlantMonthEnergyOrRealTimeData();
			onePlantMonthEnergy.setName(plantListStr[i]);
			onePlantMonthEnergy.setData(new Float(0));
			plantVectorData.set(i, onePlantMonthEnergy);
		}
		OracleConnection oc = new OracleConnection();
		ResultSet rs=  oc.query(sql,params);

		try {
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
		}

		JSONArray ja = new JSONArray();
		for(int i=0;i<plantVectorData.size();i++){
			JSONArray onePlantDataInOneMonth = new JSONArray();
			String tempName= plantVectorData.get(i).getName();
			if(tempName.compareTo("������ƽ�糧")==0)
				tempName="K";
			if(tempName.compareTo("���볧")==0)
				tempName="T";
			if(tempName.compareTo("Ӫ�ڵ糧")==0)
				tempName="Y";
			if(tempName.compareTo("����ׯ�ӵ糧")==0)
				tempName="Z";
			if(tempName.compareTo("������ӵ糧")==0)
				tempName="Q";
			if(tempName.compareTo("������ɽ���糧")==0)
				tempName="H";
			onePlantDataInOneMonth.add(tempName);
			System.out.println(plantVectorData.get(i).getName());
			onePlantDataInOneMonth.add(plantVectorData.get(i).getData());
			ja.add(onePlantDataInOneMonth);
		}
		oc.closeAll();
		return ja;
	}
	
	
	/**
	 * ��ѯ��ص糧���ۼƷ�������ÿ��һ����ɫ,ÿ���糧һ������
	 * @return
	 * ����ֵ�ĸ�ʽ���£�
	 * {"seriesPlantName":[name1,name2,name3...],}
	 */
	public JSONObject getYearAccumulatePowerData(){
//		select substr(t.rq,0,7),sum(t.rdl) as rdl,b.ssdcmc from info_dmis_zdhcjz t,base_jzbm b where t.jzbm in (
//				'sykppg1','sykppg2','tlpg5','tlpg6','ykpg3','ykpg4','dlzhpg1','dlzhpg2','tlqhpg1','tlqhpg9','cyyshpg1','cyyshpg2')
//				and rq <= '2014-10-11' and rq >= '2014-01-01' and t.jzbm=b.jzbm group by ssdcmc,substr(t.rq,0,7) order by ssdcmc
		
		log.debug("����"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		OracleConnection oc = new OracleConnection();
		//�ֶ���ͼ��Ҫ��ʾ�ĵ糧�б�
		int numPlant = 0;
		int monthNum = Integer.parseInt(date.substring(5, 7));
		//�����糧��������Ϣ��Vector,����ÿ���������һ���糧
		Vector<PlantYearAccumulateDataSeries> plantVectorDataseries = new Vector<PlantYearAccumulateDataSeries>();

		//�糧�������õĻ���ļ���(������pimsphone.properties�ļ��У�
		ArrayList<String> generatorList        = new ArrayList<String>();
		
		//��õ糧�б�
		String plantsStr = properties.getString("pims.plant.graph1.plants");
		String plantListStr[] = plantsStr.split(",");
		numPlant = plantListStr.length;
		plantVectorDataseries.setSize(monthNum);

		for(int i=0;i<plantVectorDataseries.size();i++){
			PlantYearAccumulateDataSeries temp = new PlantYearAccumulateDataSeries();
			Vector<Float> dataTemp = new Vector<Float>();
			dataTemp.setSize(numPlant);
			temp.setData(dataTemp);
			temp.setName((i+1)+"��");
			plantVectorDataseries.set(i, temp);
		}

		log.debug("YearAccumulatePlantData�糧�б�:"+plantsStr);
		generatorList = PimsTools.getGeneratorsList("pims", "plant", "graph1");

		
		
		//װ��sql���,�����б�,�ö��ŷָ�,in�Ӿ�������ĿΪ1000,�㹻��
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
		log.debug("sql��ѯ:"+sql+"\n������"+startDate+","+date);
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
			if(tempName.compareTo("������ƽ�糧")==0)
				tempName="K";
			if(tempName.compareTo("���볧")==0)
				tempName="T";
			if(tempName.compareTo("Ӫ�ڵ糧")==0)
				tempName="Y";
			if(tempName.compareTo("����ׯ�ӵ糧")==0)
				tempName="Z";
			if(tempName.compareTo("������ӵ糧")==0)
				tempName="Q";
			if(tempName.compareTo("������ɽ���糧")==0)
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
	 * ��õ糧������Ϣͼ������
	 * @return�������ݽṹ����
	 * "plantProgressData":[[3.98,2.76],[7.97,5.15],[11.95,8.1],[15.93,11.01],
	 * 						[19.92,17.03],[23.9,23.15],[27.88,27.18],[31.87,32.31],
	 * 						[35.85,37.92],[39.83,41.6],[43.82,43.9],[47.8,44.9]]
	 * ����ĵ�һ����Ϊ�ƻ�ֵ���ڶ���ֵΪ���ֵ������ǰ��
	 * ��ǰ��,��Ϊ��ΧͼԼ��(��һ��ֵ����ȵڶ���ֵС),����Ҫ�Ѽƻ�ֵ>���ֵ�Ľ���,ͳһ��[Сֵ,��ֵ]��ʽ,Ϊ������,���������Ϊ��ɫ,û�н�����Ϊ��ɫ
	 */
	public JSONObject getProgressData(){
		
		log.debug("����"+Thread.currentThread().getStackTrace()[1].getClassName()+
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
		log.debug("sql��ѯ:"+sqlGetYearPlan+"\n������"+jzbm+","+date.substring(0,4));
		ResultSet rs=  oc.query(sqlGetYearPlan,params1);
		try {
			while(rs.next())
				yearPlan = rs.getFloat("njh");
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		float oneMonthPlan = yearPlan/12/10000;
		//���ÿ���µļƻ���
		for(int i=0;i<12;i++){
			
			
			monthPlan.add(Tools.float2Format(oneMonthPlan*(i+1), 2));
		}
		
		
		String sqlGetMonthPower = "select substr(rq,0,7) as yf,sum(rdl) as ylj from info_dmis_zdhcdc t "+
				"where dcbm='sykpp' and rq >= ? and rq <= ? group by substr(rq,0,7) order by yf";
		String params2[] = {startDate,date};
		log.debug("sql��ѯ:"+sqlGetMonthPower+"\n������"+startDate+","+date);
		rs=  oc.query(sqlGetMonthPower,params2);
		try {
			while(rs.next()){
				String yf = rs.getString("yf");
				float ylj = rs.getFloat("ylj");
				ylj = ylj/10000;//����ǧ��ʱ��Ϊ��ǧ��ʱ
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



}
