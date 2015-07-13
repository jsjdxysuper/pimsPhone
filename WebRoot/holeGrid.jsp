<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
			
%>
<!DOCTYPE HTML>
 <html>
	<head>
    	<meta name="viewport" content="width=device-width,,user-scalable=no">
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<base href="<%=basePath %>">
		<title>HoleGrid Page</title>

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
	<script type="text/javascript" src="support/holeGrid.js"></script>
	</head>
<body>
		
<div style="clear:both;height:10px"></div>
<div style="margin-left:20px;margin-right:20px">
	<div style=";float:left">	
        <label for="test_default" style="float:left;;width:80px">日&nbsp;&nbsp;&nbsp;&nbsp;期</label>
        <input type="text" name="date" id="appDate" style="width:100px;float:left" />
    </div>
	<div style="float:right;">
		<a href="javascript:submitRequest();" class="button"  id="requestButton"
			style="width:50px;height:30px;font-size:18px;margin:5px 0;float:left;color:white;text-decoration:none;">查询</a>
	</div>
</div>

<div style="width:100%">
<div style="text-align:center">
<div id="lineContainer" style="width: 100%;height: 400px;"></div>
</div>
</div>

<br>
<br>
<br>

<div style="width:100%">
<div style="text-align:center">

	<table id="table_example" class="display cell-border"  style="height:300px;">   
		<caption style="text-align:center;font-size:18px;color:#000000">当日电网发电情况</caption>
	
	    <thead style="background-color:#30a4dd;color:#ffffff;">   
	        <tr>   
	            <th >&nbsp;&nbsp;&nbsp;&nbsp;</th>   
	            <th >日发电(万)</th>   
	            <th >月发电(亿)</th> 
	            <th >年发电(亿)</th>   
	            <th >月同比</th>   
<!-- 	            <th >年同比</th>     -->
	        </tr>   
	    </thead>   
	    <tbody >   
	        <tr>   
	        	<td>全省发电</td><td>0</td><td>0</td><td>0</td><td>0</td>
	        </tr>   
	        <tr>   
	        	<td>直调火电</td><td>0</td><td>0</td><td>0</td><td>0</td>
	        </tr>
	        <tr>   
	            <td>直调水电</td><td>0</td><td>0</td><td>0</td><td>0</td>
	        </tr>
	        <tr>   
	            <td>直调风电</td><td>0</td><td>0</td><td>0</td><td>0</td>
	        </tr>
	        <tr>   
	            <td>直调核电</td><td>0</td><td>0</td><td>0</td><td>0</td>
	        </tr>
	        <tr>   
	            <td>联络线净受</td><td>0</td><td>0</td><td>0</td><td>0</td>
	        </tr>
	    </tbody>  
	</table>  

</div>

</div>
	<div style="clear:both;height:20px"></div>

</body>
</html>
