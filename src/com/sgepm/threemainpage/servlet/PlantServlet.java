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
		
		
		JSONObject jo = new JSONObject();
		
		JSONArray ja = new JSONArray();
		date = request.getParameter("date");
		dateWildcard = Tools.change2WildcardDate(date, Tools.time_span[2]);
		
		ja = getPlantLineData();
		jo.put("columnData",ja);
		out.write(jo.toString());
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
	public JSONArray getPlantLineData() {
		
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
		for(int i=0;i<plantListStr.length;i++){
				String generators = properties.getString("pims.plant.graph1."+plantListStr[i]);
				
				String gArray[] = generators.split(",");
				for(int j=0;j<gArray.length;j++){
					generatorList.add(gArray[j]);
				}
		}
		
		
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
