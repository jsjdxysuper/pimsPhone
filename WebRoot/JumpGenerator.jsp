<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html manifest="./support/cache.manifest">
  <head>
  	<link rel="stylesheet" type="text/css" href="support/bootstrap/css/bootstrap.min.css" />
	
	<link rel="stylesheet" type="text/css" href="support/datatable/media/css/jquery.dataTables.min.css" />
	
	<script type="text/javascript" src="support/jquery-1.11.2.min.js"></script>
	<script type="text/javascript" src="support/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="support/datatable/media/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript" src="support/datepicker/js/mobiscroll.core-2.5.2.js"></script>
	<script type="text/javascript" src="support/datepicker/js/mobiscroll.core-2.5.2-zh.js"></script>
	<link rel="stylesheet" type="text/css" href="support/datepicker/css/mobiscroll.core-2.5.2.css" />
	<link rel="stylesheet" type="text/css" href="support/datepicker/css/mobiscroll.animation-2.5.2.css" />
	<link rel="stylesheet" type="text/css" href="support/datepicker/css/mobiscroll.android-ics-2.5.2.css" />
	<script type="text/javascript" src="support/datepicker/js/mobiscroll.android-ics-2.5.2.js"></script>
	<script type="text/javascript" src="support/datepicker/js/mobiscroll.datetime-2.5.1.js"></script>

	<script type="text/javascript" src="support/datepicker/js/mobiscroll.datetime-2.5.1-zh.js"></script>
	<script type="text/javascript" src="support/jquery.json.min.js"></script>
	<script type="text/javascript" src="support/highcharts/js/highcharts.js"></script>
	<script type="text/javascript" src="support/highcharts/js/highcharts-more.js"></script>
	
	<script type="text/javascript" src="support/highcharts/js/modules/solid-gauge.js"></script>
	<script type="text/javascript" src="support/highcharts/js/modules/exporting.js"></script>
	<script type="text/javascript" src="support/threeMainPage.js"></script>
	<link rel="stylesheet" type="text/css" href="support/threeMainPage.css" />
	<script type="text/javascript" src="support/generator.js"></script>
    <base href="<%=basePath%>">
    <%request.getRequestDispatcher("generator.jsp").forward(request, response); %>
  </head>
  
  <body>
    This is my JSP page. <br>
  </body>
</html>
