package com.sgepm.threemainpage.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sgepm.Tools.OracleConnection;
import com.sgepm.Tools.Tools;
import net.sf.json.JSONObject;

@WebServlet(name="GeneratorServlet",urlPatterns="/GeneratorServlet")
public class GeneratorServlet extends HttpServlet {

	private String date;
	private String dateWildcard;
	private String dcbm = "sykpp";
	private Logger log = LoggerFactory.getLogger(HoleGridServlet.class);
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
	 * 在此处统一处理get和post请求
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void doRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8") ;
		response.setCharacterEncoding("UTF-8") ;
		PrintWriter out = response.getWriter();
		
		
		date = request.getParameter("date");		
		date = Tools.formatDate(date);//改变日期的格式为YYYY-MM-DD
		log.debug("post机组日期查询日期:"+date);
		String returnData =  getData();
//		try {
//			Thread.currentThread().sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if(returnData!=null)
			out.write(returnData);
		out.close();
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
	 *  获取机组日电量信息,由日电量信息计算利用小时数,平均有功,负荷率
	 * @return 康平机组信息的Map
	 */
	public JSONObject getEachGenerator(){
		
		OracleConnection oc = new OracleConnection();
		JSONObject eachGeneratorData = new JSONObject();

		
		float g1Average,g1Energy,g1TimeUse;
		float g2Average,g2Energy,g2TimeUse;
		

		g1Average=g1Energy=g1TimeUse=0;
		g2Average=g2Energy=g2TimeUse=0;
		
		String generatorSqlStr="select t.jzbm,t.jzmc,t.rdl from info_dmis_zdhcjz t,base_jzbm b where t.jzbm=b.jzbm and b.ssdcbm= ? and t.rq= ? order by jzbm,rq";

		String []dataParas={dcbm,date};

		ResultSet rs = oc.query(generatorSqlStr,dataParas);

		
		try {
			while(rs.next()){
				
				String jzbm = rs.getString("jzbm");
				String jzmc = rs.getString("jzmc");
				float  rdl  = rs.getFloat("rdl");

				if(jzbm.compareTo("sykppg1")==0){
					g1Energy = rdl;
				}
				else if(jzbm.compareTo("sykppg2")==0){
					g2Energy = rdl;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		float energy  = g1Energy+g2Energy;
		float average = (energy)*10/24;
		float timeUse = energy*24/(Tools.rongLiang*2);
		eachGeneratorData.put("energy", Tools.float2Format(energy, 2));
		eachGeneratorData.put("average", Tools.float2Format(average, 2));
		eachGeneratorData.put("timeUse", Tools.float2Format(timeUse, 2));
		
		
		g1Average = g1Energy*10/24;
		g1TimeUse = g1Energy*24/Tools.rongLiang;
		
		g2Average = g2Energy*10/24;
		g2TimeUse = g2Energy*24/Tools.rongLiang;

		eachGeneratorData.put("g1Average",Tools.float2Format(g1Average,2));
		eachGeneratorData.put("g1Energy",Tools.float2Format(g1Energy,2));
		eachGeneratorData.put("g1TimeUse",Tools.float2Format(g1TimeUse,2));
		

		eachGeneratorData.put("g2Average",Tools.float2Format(g2Average,2));
		eachGeneratorData.put("g2Energy",Tools.float2Format(g2Energy,2));
		eachGeneratorData.put("g2TimeUse",Tools.float2Format(g2TimeUse,2));
		oc.closeAll();
		return eachGeneratorData;
	}
	
	

	public String getData(){
		
		JSONObject jo = new JSONObject();

		
		JSONObject generatorMap = getEachGenerator();
		

		jo.putAll(generatorMap);
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
