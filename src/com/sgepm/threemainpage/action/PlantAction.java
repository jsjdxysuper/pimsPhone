package com.sgepm.threemainpage.action;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opensymphony.xwork2.ActionSupport;
import com.sgepm.Tools.JdbcUtilProxoolImpl;
import com.sgepm.Tools.PimsTools;
import com.sgepm.Tools.Tools;
import com.sgepm.threemainpage.entity.Plant60GenPower;
import com.sgepm.threemainpage.entity.Plant60GenPowerLineWrapper;
import com.sgepm.threemainpage.entity.PlantMonthEnergyOrRealTimeData;
import com.sgepm.threemainpage.entity.PlantYearAccumulateDataSeries;

public class PlantAction  extends ActionSupport{
	
	private Logger log = LoggerFactory.getLogger(PlantAction.class);
	private Map<String,Object>dataMap;//used to return json data
	ResourceBundle properties                  = ResourceBundle.getBundle(Tools.PROFILENAME);
    private Connection conn = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;
    
	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	
	public PlantAction(){
		dataMap = new HashMap<String,Object>();
	}
	
	/**
	 * ��ȡ��ص糧60����������������,����Ϊ��λ
	 * @return
	 */
	public String plant60GensPowerLineData(){
		log.info("����"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		//pims.plant.graph1.plant2nickname

		String plant2nicknames[] = properties.getString("pims.plant.graph1.plant2nickname")
				.split(",");
		Vector<Plant60GenPower> retData = new Vector<Plant60GenPower>();
		//60����������糧������
		final int PlantNum = plant2nicknames.length;
		for(int i=0;i<PlantNum;i++){
			Plant60GenPower one60GenPower = new Plant60GenPower();
			String plantAndNick[] = plant2nicknames[i].split(":");
			one60GenPower.setPlantName(plantAndNick[0]);
			one60GenPower.setNickName(plantAndNick[1]);
			retData.add(one60GenPower);
			
			//retData.get(i).setPlantName(DCBMs[i]);
		}
		
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
		String date = ServletActionContext.getRequest().getParameter("date");
		//����,ʱ��,�糧����,�й�
		//String sql = "select c1 rq,c2 sj,c3 dcbm,c4 yg from t001 t where c1 = ? order by  rq,dcbm,sj";
		String sql = "select a.ssdcmc,b.sj,sum(b.yg) as yg "+
				"from Base_Jzbm a,info_data_jzyg b "+
				"where a.jzbm=b.jzbm and b.rq= ? and b.jzbm in ("+inString+") "+
				"group by a.ssdcmc,b.sj order by a.ssdcmc,b.sj"; 
		//�й��������Сֵ��Ϊ������������������С
		double min = Double.MAX_VALUE;
		double max = 0;
		try {
			conn = JdbcUtilProxoolImpl.getConnection();
			st = conn.prepareStatement(sql);
			st.setString(1,date);
			rs = st.executeQuery();
		
			while(rs.next()){
				String ssdcmc = rs.getString("SSDCMC");
				String  sj  = rs.getString("SJ");
				double  yg  = rs.getDouble("YG");

				if(yg > max) max = yg;
				if(yg < min) min = yg;
				for(int i=0;i<retData.size();i++){
					if(retData.get(i).getPlantName().equals(ssdcmc)){
						
						//����й�Ϊ0,������Ϊnull,�������ϲ�����ʾ�˵�,ʹ��������ֻ��ʾ��������,����Ϊ0�Ĳ�����ʾ
						if(Math.abs(yg)<Tools.DOUBLE_MIN)
							retData.get(i).setPowers(null);
						else
							retData.get(i).setPowers(Double.valueOf(Tools.float2Format(yg)));;
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtilProxoolImpl.close(conn,rs,st);
		}
		
		//�����ѯ���Ϊ��
		if(min == Double.MAX_VALUE)min = 0;
		
		
		Plant60GenPowerLineWrapper retWrapper = new Plant60GenPowerLineWrapper();
		retWrapper.setLineData(retData);
		retWrapper.setMaxRealtime(max);
		retWrapper.setMinRealtime(min);

		dataMap.put("plantLinePower", JSONObject.fromObject(retWrapper).toString());
		return SUCCESS;
	}
	
	
	/**
	 * ��õ糧ҳ��ĳ���ۼƵ�����ͼ������
	 * @return ���ذ���PlantMonthData�����JSON����
	 * �ṹ����
	 * "columnData":[["��ƽ��",26242],["���볧",26434],["Ӫ�ڳ�",22435],["ׯ�ӳ�",25299],["��ӳ�",36604],["��ɽ��",27705]]
	 */
	public String oneMonth60GensEnergyColumnData(){
		log.info("����"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		
		String plantsStr = properties.getString("pims.plant.graph1.plants");
		
		String plantListStr[];//��ͼ��Ҫ��ʾ�ĵ糧�б�
		plantListStr = plantsStr.split(",");
		
		log.info("PlantsOneMonthEnergyData�糧�б�:"+plantsStr);
		
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
		
		log.info("inString:"+inString);
		log.info("params:");
		
		String date = ServletActionContext.getRequest().getParameter("date");
		String dateWildcard = Tools.change2WildcardDate(date, Tools.time_span[2]);
		
		String sql = "select substr(t.rq,0,7),sum(t.rdl) as rdl,b.ssdcmc from info_dmis_zdhcjz t,base_jzbm b where t.jzbm in ("
						+inString+" )"+
						" and rq like ? and t.jzbm=b.jzbm group by ssdcmc,substr(t.rq,0,7) order by ssdcmc";
		log.info("sql��ѯ:"+sql+"\n������"+dateWildcard);
		
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
		
		try {

			conn = JdbcUtilProxoolImpl.getConnection();
			st = conn.prepareStatement(sql);
			st.setString(1,dateWildcard);
			rs = st.executeQuery();
		
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
		}finally{
			JdbcUtilProxoolImpl.close(conn,rs,st);
		}
		
		JSONArray allColumnData = new JSONArray();
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
			onePlantDataInOneMonth.add(plantVectorData.get(i).getData());
			allColumnData.add(onePlantDataInOneMonth);
		}
		
		dataMap.clear();
		dataMap.put("oneMonth60GensEnergyColumnData", allColumnData);
		return SUCCESS;
		
	}
	
	/**
	 * ��ѯ��ص糧���ۼƷ�������ÿ��һ����ɫ,ÿ���糧һ������
	 * @return
	 * ����ֵ�ĸ�ʽ���£�
	 * {"seriesPlantName":[name1,name2,name3...],}
	 */
	public String year60GensAccuEnergyStackColumnData(){
		log.debug("����"+Thread.currentThread().getStackTrace()[1].getClassName()+
				":"+Thread.currentThread().getStackTrace()[1].getMethodName());
		
		//�ֶ���ͼ��Ҫ��ʾ�ĵ糧�б�
		int numPlant = 0;
		String date = ServletActionContext.getRequest().getParameter("date");
		int monthNum = Integer.parseInt(date.substring(5, 7));
		
		//�����糧��������Ϣ��Vector,����ÿ���������һ���糧
		Vector<PlantYearAccumulateDataSeries> plantVectorDataseries = new Vector<PlantYearAccumulateDataSeries>();

		//�糧�������õĻ���ļ���(������pimsphone.properties�ļ��У�
		ArrayList<String> generatorList        = new ArrayList<String>();
		
		//��õ糧�б�
		ResourceBundle properties = ResourceBundle.getBundle("pimsphone");
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
		
		log.info("YearAccumulatePlantData�糧�б�:"+plantsStr);
		generatorList = PimsTools.getGeneratorsList("pims", "plant", "graph1");
		
		//װ��sql���,�����б�,�ö��ŷָ�,in�Ӿ�������ĿΪ1000,�㹻��
		String inString="";
		for(int i=0;i<generatorList.size();i++){
		    if(i>0){
		        inString+=",";
		    }
		    inString+="'"+generatorList.get(i)+"'";
		}
		
		log.info("inString:"+inString);
		log.info("params:");
		
		String startDate = Tools.getFirstDateInYear(date);
		String sql = "select substr(t.rq,0,7) as yf,sum(t.rdl) as ydl,b.ssdcmc from info_dmis_zdhcjz t,base_jzbm b where t.jzbm in ("
						+inString+" )"+
						" and rq >= ? and rq <= ? and t.jzbm=b.jzbm group by ssdcmc,substr(t.rq,0,7) order by ssdcmc";
		log.info("sql��ѯ:"+sql+"\n������"+startDate+","+date);
		
		try {
			conn = JdbcUtilProxoolImpl.getConnection();
			st = conn.prepareStatement(sql);
			st.setString(1,startDate);
			st.setString(2,date);
			rs = st.executeQuery();

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
				//log.info("yf:"+yf+",ydl:"+ydl+",ssdcmc:"+ssdcmc);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtilProxoolImpl.close(conn,rs,st);
		}
		
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
		
		dataMap.clear();
		
		dataMap.put("seriesPlantName", plantListStr);
		dataMap.put("yearAccumulatePlantPowerSeries", plantVectorDataseries);
		
		return SUCCESS;
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
	public String plantProgressColumnData(){
		
		String date = ServletActionContext.getRequest().getParameter("date");
		String startDate = Tools.getFirstDateInYear(date);
		float yearPlan = 0;
		Vector<Float> monthPlan = new Vector<Float>();
		Vector<Float> monthFinish = new Vector<Float>();
		float yearAccumulate = 0;
		
		String sqlGetYearPlan = "select t.nf,t.njh  from info_sdlr_dcnjh t where t.dcbm = ? and t.nf = ?";
		
		//Ϊ�Ժ����ŵĽӿ�
		String jzbm = "sykpp";
		log.debug("sql��ѯ:"+sqlGetYearPlan+"\n������"+jzbm+","+date.substring(0,4));
		
		try {
			conn = JdbcUtilProxoolImpl.getConnection();
			st = conn.prepareStatement(sqlGetYearPlan);
			st.setString(1,jzbm);
			st.setString(2,date.substring(0,4));
			rs = st.executeQuery();
		
			while(rs.next())
				yearPlan = rs.getFloat("njh");
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			JdbcUtilProxoolImpl.close(conn,rs,st);
		}
		
		float oneMonthPlan = yearPlan/12/10000;
		//���ÿ���µļƻ���
		for(int i=0;i<12;i++){

			monthPlan.add(Tools.float2Format(oneMonthPlan*(i+1), 2));
		}
		
		String sqlGetMonthPower = "select substr(rq,0,7) as yf,sum(rdl) as ylj from info_dmis_zdhcdc t "+
				"where dcbm='sykpp' and rq >= ? and rq <= ? group by substr(rq,0,7) order by yf";
		log.debug("sql��ѯ:"+sqlGetMonthPower+"\n������"+startDate+","+date);
		
		try {
			
			conn = JdbcUtilProxoolImpl.getConnection();
			st = conn.prepareStatement(sqlGetYearPlan);
			st.setString(1,startDate);
			st.setString(2,date);
			rs = st.executeQuery();

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
		}finally{
			JdbcUtilProxoolImpl.close(conn,rs,st);

		}
		
		//plantIdentity
		Vector<Vector<Float>> vector = new Vector<Vector<Float>>();
		for(int i=0;i<monthFinish.size();i++){
			Vector<Float> temp = new Vector<Float>();
			temp.add(monthPlan.get(i));
			temp.add(monthFinish.get(i));
			vector.add(temp);
			
		}
		dataMap.clear();
		
		dataMap.put("plantProgressData", vector);
		
		return SUCCESS;
	}
}
