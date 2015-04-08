package com.sgepm.Tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sgepm.threemainpage.servlet.HoleGridServlet;

public class Tools {

	public static float FLOAT_MIN = 1e-6f;
	public static final int RealTimePointADay = 288;
	public static String [] time_span      = {"ʵʱ","��","��","��"};
	public static int    [] days_all_month = {31,28,31,30,31,30,31,31,30,31,30,31};
	public static float rongLiang = 600;
	public static final double DOUBLE_MIN = 1e-6d;
	/**
	 * �����ڽ��и�ʽ��
	 * @param date
	 * @return
	 */
	public static String formatDate(String date){
		if(date==null||(date.compareTo("")==0))
			return null;
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
		return df.format(java.sql.Date.valueOf(date));
	}
	
	
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
	/**
	 * ������������λС��
	 * @param old
	 * @return
	 */
	public static String float2Format(float old){
		String ret;
		DecimalFormat form2 = new DecimalFormat("##0.00");
		DecimalFormat form = new DecimalFormat("##0");
		ret = ((old==0)?form.format(old):form2.format(old));
		return ret;
	}
	
	public static float float2Format(float old,int dotNum){
		BigDecimal b = new BigDecimal(old);
		float ret ;
		if(Math.abs(old-0)<FLOAT_MIN)			
			ret= b.setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
		else
			ret= b.setScale(dotNum, BigDecimal.ROUND_HALF_UP).floatValue();
		return ret;
	}
	
	/**
	 * ���ϵͳ��ǰ����
	 * @return
	 */
	public static Date getTodayDate(){
		Date dt = new Date();
//		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		return new Date();
	}
	
	public static String getTodayStr(){
		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		return matter.format(dt);
	}
	
	/**
	 * ��ø�������ǰһ��
	 * @param theDay
	 * @return
	 */
	public static Date getForeDay(Date theDay){
		if(theDay == null)
			return null;
		Date ret = null;
		Calendar cal1 = Calendar.getInstance();
		cal1.set(theDay.getYear()+1900, theDay.getMonth(), theDay.getDate()-1);
		ret = cal1.getTime();
		return ret;
	}
	public static String getForeDay(String theDay){
		if(theDay == null)
			return null;
		String ret = null;
		ret = formatDate(getForeDay(java.sql.Date.valueOf(theDay)));
		return ret;
	}
	/**
	 * ���ָ�����ڵ���һ���µ�����
	 * ������Ϊ03��30�գ����µ�30���ǲ����ڵ�
	 * @param old
	 * @return
	 */
	public static Date getLastMonthDay(Date oldDate){
		if(oldDate==null)
			return null;
		Date ret ;
		Calendar cal1 = Calendar.getInstance();
		cal1.set(oldDate.getYear()+1900, oldDate.getMonth()-1, oldDate.getDate());
		ret = cal1.getTime();
		return ret;
	}
	public static String getLastMonthWildStr(Date oldDate)
	{
		String lastMonthDateStr;
		
		if(oldDate == null)
			return null;
		Calendar cal1 = Calendar.getInstance();
		cal1.set(oldDate.getYear()+1900, oldDate.getMonth()-1, 1);
		Date lastMonthDate = cal1.getTime();
		lastMonthDateStr = formatDate(lastMonthDate);
		String ret = change2WildcardDate(lastMonthDateStr, Tools.time_span[2]);
		return ret;
	}
	/**
	 * ���ָ�����ڵ���һ���������
	 * ʹ��ʱ��ע�⣬��������02-29�ŵ�ǰһ����û�ж�Ӧ�����
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
	
	public static String getLastYearMonthWildStr(Date oldDate){
		String lastMonthDateStr;
		
		if(oldDate == null)
			return null;
		Calendar cal1 = Calendar.getInstance();
		cal1.set(oldDate.getYear()+1900-1, oldDate.getMonth(), 1);
		Date lastMonthDate = cal1.getTime();
		lastMonthDateStr = formatDate(lastMonthDate);
		String ret = change2WildcardDate(lastMonthDateStr, Tools.time_span[2]);
		return ret;
	}
	/**
	 * ���ĳһ�����ڣ�������������·ݵ�����
	 * @param in
	 * @return
	 */
	public static int getMonthDayNum(String in){
		String dyear = in.substring(0, 4);
		String dmonth = in.substring(5, 7);

		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM");
	    Calendar rightNow = Calendar.getInstance();

	    try {
			rightNow.setTime(simpleDate.parse(dyear+"/"+dmonth));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);//�������� ��ȡ�·�����

	}
	
	/**
	 * ��ø�����ݵĵ�һ��
	 * @param theDay
	 * @return
	 */
	public static java.sql.Date getFirstDateInYear(java.sql.Date theDay){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
		String theDate = df.format(theDay);
		String year = theDate.substring(0, 4);
		String monthAndDay = "01-01";
		String dateStr = year+"-"+monthAndDay;
		return java.sql.Date.valueOf(dateStr);
	}
	/**
	 * ��ø�����ݵĵ�һ��
	 * @param theDay
	 * @return
	 */
	public static String getFirstDateInYear(String theDayStr){
		java.sql.Date theDay = getFirstDateInYear(java.sql.Date.valueOf(theDayStr));
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
		df.format(theDay);
		return df.format(theDay);
	}
	
	/**
	 * ��õ糧�ļ�д�����磺
	 * ��ɽ���糧����ɽ��
	 * ��ƽ������ƽ
	 * @param longName
	 * @return
	 */
	public static String getAbbrNameOfPlant(String longName){
		String newName = longName;
        Pattern pat = Pattern.compile("\"[\\u4e00-\\u9fa5]{3,}�糧\"");
        Matcher mat = pat.matcher(longName);
        while (mat.find()) {
        	String bingo = mat.group();
        	String replaceStr = bingo.replaceAll("�糧", "");
        	//System.out.println(bingo);
            newName = longName.replace(bingo, replaceStr);
            
        }
        
//        String newName1 = newName;
//        Pattern pat1 = Pattern.compile("\"[\\u4e00-\\u9fa5]{4,}��\"");
//        Matcher mat1 = pat1.matcher(newName);
//        while(mat1.find()){
//        	String bingo = mat1.group();
//        	String replaceStr = bingo.replaceAll("��", "");
//        	newName1 = newName.replace(bingo, replaceStr);
//        }
		return newName;
	}
	/**
	 * ���ò��ֵ�ķ�����д�糧
	 * @param oldStr
	 * @param dic
	 * @return
	 */
	public static String replacePlantName(String oldStr,Map<String,String> dic){
		String newStr = oldStr;
		for(String key:dic.keySet()){
			newStr = newStr.replaceAll(key, dic.get(key));
		}
		return newStr;
	}
	
	public static void main(String[] args) {

		Calendar now      = Calendar.getInstance();
		int      year     = now.get(Calendar.YEAR);
		int      month    = now.get(Calendar.MONTH);
		int      day      = now.get(Calendar.DATE);
		int      hour     = now.get(Calendar.HOUR_OF_DAY);
		int      min      = now.get(Calendar.MINUTE);
		int      second   = now.get(Calendar.SECOND);
		
		String todyDate = String.format("%04d-%02d-%02d", year,month+1,day);
		String nowTime = String.format("%02d:%02d:%02d", hour,min,second);
		System.out.println(todyDate+" "+nowTime);
		
		Date nowDate = new Date();
		System.out.printf("%tF%n",nowDate);
		System.out.printf("%tT%n",nowDate);
		
		System.out.printf(String.format("%tF%n", nowDate));
		System.out.printf(String.format("%tT%n",nowDate));
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
