<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
  		<script type="text/javascript" src="./support/jquery-1.11.2.min.js"></script>

        
		<script type="text/javascript" src="./support/datatable/media/js/jquery.dataTables.min.js"></script> 
        <link rel="stylesheet" type="text/css" href="./support/datatable/media/css/jquery.dataTables.min.css"/>
      
<script type="text/javascript">
$(document).ready(function() {
    $('#table_id4').DataTable();
} );
</script>
		

    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
  </head>
  
  <body>
    This is my JSP page. <br>

	<table id="table_id4" class="display cell-border"  style="width:100%;height:300px">   
		<caption style="text-align:center"><b>当日电网发电情况</b></caption>
	
	    <thead>   
	        <tr>   
	            <th >类别</th>   
	            <th >日发电(万)</th>   
	            <th >月发电(亿)</th> 
	            <th >年发电(亿)</th>   
	            <th >月同比</th>   
	            <th >年同比</th>    
	        </tr>   
	    </thead>   
	    <tbody>   
	        <tr>   
	        	<td>全省发电</td><td>1245</td><td>2323</td><td>27233</td><td>1.1%</td><td>1.1%</td>   
	        </tr>   
	        <tr>   
	        	<td>直调发电</td><td>1545</td><td>4612</td><td>2323</td><td>1.1%</td><td>1.1%</td>   
	        </tr>   
	        <tr>   
	        	<td>直调火电</td><td>1915</td><td>4612</td><td>2323</td><td>2.1%</td><td>-1.3%</td>   
	        </tr>
	        <tr>   
	            <td>直调水电</td><td>245</td><td>4612</td><td>2123</td><td>1.4%</td><td>1.1%</td>   
	        </tr>
	        <tr>   
	            <td>直调风电</td><td>125</td><td>4612</td><td>1323</td><td>3.1%</td><td>-1.1%</td>   
	        </tr>
	        <tr>   
	            <td>直调核电</td><td>2245</td><td>4612</td><td>2373</td><td>1.6%</td><td>1.1%</td>   
	        </tr>
	        <tr>   
	            <td>其他</td><td>1735</td><td>4612</td><td>2823</td><td>4.1%</td><td>-1.1%</td>   
	        </tr>
	        <tr>   
	            <td>联络线净受</td><td>1744</td><td>4612</td><td>2313</td><td>6.1%</td><td>1.1%</td>   
	        </tr>
	    </tbody>  

	</table>  


  </body>
</html>
