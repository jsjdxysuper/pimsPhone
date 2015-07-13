<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
			
%>
<!DOCTYPE HTML>
<!-- <html manifest="generator.manifest">  -->
<html>
	<head>
    	<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.3,user-scalable=yes">
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<base href="<%=basePath %>">
		<title>Plant Page</title>

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
	<script type="text/javascript" src="support/plant.js"></script>

	</head>
	<body>
		<div style="clear:both;height:10px"></div>
		<div style="margin-left:20px;margin-right:20px">
			<div style=";float:left">	
		        <label for="test_default" style="float:left;;width:80px">日&nbsp;&nbsp;&nbsp;&nbsp;期</label>
		        <input type="text" name="test_default"  id="appDate" style="width:100px;float:left" />
		    </div>
			<div style="float:right;">
				<a href="javascript:submitRequest();" class="button" id="requestButton"
					style="width:50px;height:30px;font-size:18px;margin:5px 0;float:left;color:white;text-decoration:none;">查询</a>
			</div>
		</div>


		<div style="height:60px"></div>
		<div id="realTimeLineContainer" style="clear:both;width: 95%;height: 500px;margin-left:auto;margin-right:auto">
			
		</div>
		<div id="textWhenNoData" style="clear:both;height:100px;display:none;text-align:center">
		
		<img src="./support/images/noData.png" style="height:90px;width:90px"></img>
		<h6>此日期没有出力数据</h6>
		</div>
		<div style="height:40px"></div>
		<div id="columnContainer" style=" clear:both;height: 350px;width:95%"></div>
		<div style="height:40px"></div>
		<div id="stackColumnContainer" style=" clear:both;height: 400px;"></div>
	</body>
</html>
