package com.sgepm.Tools;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sgepm.threemainpage.servlet.HoleGridServlet;

public class Tools {

	public static String [] time_span      = {"实时","年","月","日"};
	public static int    [] days_all_month = {31,28,31,30,31,30,31,31,30,31,30,31};
	public static Logger log = LoggerFactory.getLogger(HoleGridServlet.class);
	public static String formatDate(String date){
		if(date==null||(date.compareTo("")==0))
			return null;
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
		return df.format(java.sql.Date.valueOf(date));
	}
	public static float rongLiang = 600;
	
	/**
	 * 对日期进行格式化，为YYYY-MM-DD
	 * @param old
	 * @return
	 */
	public static String formatDate(Date old){
		if(old==null)
			return null;
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
		return df.format(old);
	}
	/**
	 * 根据日期跨度生成数据查询的通配符格式
	 * 例子：change2WildcardDate("2014-12-10","月")会返回"2014-12-%%"
	 * @param old
	 * @param time_span
	 * @return
	 */
	public static String change2WildcardDate(String old,String time_span){
		if(old==null||(old.compareTo("")==0))
			return null;
		//log.debug("kongkong:"+old);
		char[]dateSplit = old.toCharArray();
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
		String newDate = String.copyValueOf(dateSplit);
		return newDate;
	}
	//浮点数保留两位小数
	public static String float2Format(float old){
		String ret;
		DecimalFormat form2 = new DecimalFormat("##0.00");
		DecimalFormat form = new DecimalFormat("##0");
		ret = ((old==0)?form.format(old):form2.format(old));
		return ret;
	}
	/**
	 * 获得指定日期的上一个月的日期
	 * @param old
	 * @return
	 */
	public static Date getLastMonthDay(Date old){
		if(old==null)
			return null;
		Date ret ;
		Calendar cal1 = Calendar.getInstance();
		cal1.set(old.getYear()+1900, old.getMonth()-1, old.getDate());
		ret = cal1.getTime();
		return ret;
	}
	/**
	 * 获得指定日期的上一个年的日期
	 * @param old
	 * @return
	 */
	public static Date getLastYearDay(Date old){
		if(old==null)
			return null;
		Date ret ;
		Calendar cal1 = Calendar.getInstance();
		cal1.set(old.getYear()+1900-1, old.getMonth(), old.getDate());
		ret = cal1.getTime();
		return ret;
	}
	public static void main(String[] args) {
		log.debug(Tools.formatDate("2012-1-8"));
	}
	
	
//	public static int getHours(String date,String span){
//		int a = 0;
//		boolean run_year = false;
//		String []time_split = date.split("-");
//		
//		if(span.compareTo("实时")==0||span.compareTo("日")==0){
//			return 24;
//		}
//		int year = Integer.valueOf(time_split[0]);
//		int month = Integer.valueOf(time_split[1]);
//
//		if(year%100==0){
//			if(year%400==0)
//				run_year = true;
//		}else if(year%4==0){
//			run_year = true;
//		}
//		if(run_year==true)
//			days_all_month[1] = 29;
//		
//		if(span.compareTo("月")==0)
//			return days_all_month[month]*24;
//		if(span.compareTo("年")==0)
//			return 
//		return a;
//	}
}
