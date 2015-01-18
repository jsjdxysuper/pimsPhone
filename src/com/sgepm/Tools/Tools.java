package com.sgepm.Tools;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sgepm.threemainpage.servlet.HoleGridServlet;

public class Tools {

	public static String [] time_span      = {"ʵʱ","��","��","��"};
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
	 * �����ڽ��и�ʽ����ΪYYYY-MM-DD
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
	 * �������ڿ���������ݲ�ѯ��ͨ�����ʽ
	 * ���ӣ�change2WildcardDate("2014-12-10","��")�᷵��"2014-12-%%"
	 * @param old
	 * @param time_span
	 * @return
	 */
	public static String change2WildcardDate(String old,String time_span){
		if(old==null||(old.compareTo("")==0))
			return null;
		//log.debug("kongkong:"+old);
		char[]dateSplit = old.toCharArray();
		if(time_span.compareTo(Tools.time_span[0])==0){//ʵʱ

		}else if(time_span.compareTo(Tools.time_span[1])==0){//��
			dateSplit[5]='%';
			dateSplit[6]='%';
			dateSplit[8]='%';
			dateSplit[9]='%';
		}else if(time_span.compareTo(Tools.time_span[2])==0){//��
			dateSplit[8]='%';
			dateSplit[9]='%';
		}else if(time_span.compareTo(Tools.time_span[3])==0){//��
			
		}
		String newDate = String.copyValueOf(dateSplit);
		return newDate;
	}
	//������������λС��
	public static String float2Format(float old){
		String ret;
		DecimalFormat form2 = new DecimalFormat("##0.00");
		DecimalFormat form = new DecimalFormat("##0");
		ret = ((old==0)?form.format(old):form2.format(old));
		return ret;
	}
	/**
	 * ���ָ�����ڵ���һ���µ�����
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
	 * ���ָ�����ڵ���һ���������
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
//		if(span.compareTo("ʵʱ")==0||span.compareTo("��")==0){
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
//		if(span.compareTo("��")==0)
//			return days_all_month[month]*24;
//		if(span.compareTo("��")==0)
//			return 
//		return a;
//	}
}
