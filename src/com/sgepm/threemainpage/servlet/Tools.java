package com.sgepm.threemainpage.servlet;

public class Tools {

	public static String [] time_span      = {"ʵʱ","��","��","��"};
	public static int    [] days_all_month = {31,28,31,30,31,30,31,31,30,31,30,31};
	public static String formatDate(String date){
		
		String []a = date.split("-");
		if(a[1].length()==1){
			a[1] = "0"+a[1];
		}
		if(a[2].length()==1){
			a[2] = "0"+a[2];
		}
		return a[0]+"-"+a[1]+"-"+a[2];
	}
	public static float rongLiang = 600;
	
	public static String change2WildcardDate(String old,String time_span){
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
