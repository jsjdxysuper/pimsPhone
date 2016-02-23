<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
			
%>
<!DOCTYPE HTML>
<!-- <html> -->
 <html  manifest="./support/cache.manifest">
	<head>
    	<meta name="viewport" content="width=device-width,,user-scalable=no">
    	<base href="<%=basePath %>">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Generator Page</title>
	
	
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

	</head>
	<body>

		<!--时间选择器-->
		<div style="clear:both;height:10px"></div>
		<div style="margin-left:20px;margin-right:20px">
			<div style=";float:left">	
		        <label for="test_default" style="float:left;;width:80px">日&nbsp;&nbsp;&nbsp;&nbsp;期</label>
		        <input type="text" name="test_default" id="appDate" style="width:100px;float:left" />
		    </div>
			<div style="float:right;">
				<a href="javascript:submitRequest();" class="button"  id="requestButton"
					style="width:50px;height:30px;font-size:18px;margin:5px 0;float:left;color:white;text-decoration:none;">查询</a>
			</div>
		</div>
	<!-- 	<div style="clear:both;height:40px"></div> -->
	
		
		<!-- <div style="text-align:center;width:window.screen.width;"> -->
			<div style="width:100%; height: 150px; margin: 0 auto" id="gaugeParent">
				<div id="g1Container" style="float:left;width: 50%; height: 170px;"></div>
				<div id="g2Container" style="float:left;width: 50%; height: 170px;"></div>
			</div>
		<!-- </div> -->
	<div style="clear:both;height:20px"></div>
	    <!--机组的统计信息(表格)-->
		<div style="margin:0 0;">
			<table id="table_id2" class="display cell-border" cellspacing="0" style="text-align:center;width:100%">   
				<thead>
				    <tr>
				        <td colspan="4" style="text-align:center;font-size:18px;color:#000000">本厂机组统计信息</td>
				    </tr>
				    <tr style="background-color:#30a4dd;color:#ffffff">   
				    	<td></td><td>#1机</td><td>#2机</td><td >电厂</td>   
				    </tr> 
				</thead>  
				    <tbody>   
				
				        <tr>   
				            <td>容量</td><td id="g1Volume">0</td><td id="g2Volume">0</td><td id="volume">0</td>   
				        </tr>   
				        <tr>   
				            <td>发电量</td><td id="g1Energy">0</td><td id="g2Energy">0</td><td id="energy">0</td>   
				        </tr> 
				        <tr>   
				            <td>平均有功</td><td id="g1Average">0</td><td id="g2Average">0</td><td id="average_power">0</td>   
				        </tr> 	                       
				        <tr>   
				            <td>利用小时</td><td id="g1TimeUse">0</td><td id="g2TimeUse">0</td><td id="timeUse">0</td>   
				        </tr> 	        
				    </tbody> 
			</table>  
		</div>
		<div style="clear:both;height:40px"></div>
		<div id="columnContainer" style=" clear:both;height: 350px;width:95%"></div>
	</body>
</html>
