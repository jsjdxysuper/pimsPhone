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

        <script type="text/javascript" src="./support/bootstrap/js/bootstrap.min.js"></script>    
        <link href="./support/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
        
        <script type="text/javascript" src="./support/ladda/js/spin.min.js"></script>   
        <script type="text/javascript" src="./support/ladda/js/ladda.min.js"></script> 
<script type="text/javascript" src="./support/ladda/js/jquery.jribbble.min.js"></script> 

      	<link href="./support/ladda/css/ladda-themeless.min.css" rel="stylesheet" />
      	<link href="./support/ladda/css/gbtags.css" rel="stylesheet" />
<script type="text/javascript">




$(document).ready(function(){
    //定义相关变量
    $showmore = $('#showmore');
    
    function loadshots(){
	    var l =Ladda.create(this);
	    l.start();
	    l.setProgress(0.1);
	    $showmore.find('.ladda-label').text('loading...');
	    //调用jdribbble相关API获取远程数据内容
	    $.jribbble.getShotsByList('popular',function(data){
	    	var items =[];
		    $.each(data.shots,function(i, shot){
			    items.push('<article class="col-md-2 col-sm-3 col-xs-4">');
			    items.push('<a href="'+ shot.url +'" target="_blank" class="linkc">');
			    items.push('<img class="img-responsive" src="'+ shot.image_teaser_url +'" alt="'+ shot.title +'">');
			    items.push('</a>');
			    items.push('</article>');
			    l.setProgress(0.1+0.02*i);
		    });
		    var newEls = items.join(''), tmpcontent = $(newEls);
		    l.setProgress(0.9);
		    //以上代码生成了需要显示的dirbbble设计内容，下面代码中我们将这些内容添加到HTML容器中
		    $wallcontent.append(tmpcontent);
		    $showmore.find('.ladda-label').text('更多设计');
		    l.setProgress(1);
		    l.stop();
    	},{page:pagenum, per_page:24}
    	);
    	pagenum++;
    }
    //绑定方法到加载更多按钮
    $showmore.bind('click', loadshots);
    $showmore.trigger('click');
    });
    $(document).ready(function() {
    var l = Ladda.create(this);
    l.start();
    l.stop();
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
<button type="button" class="btn btn-info btn-lg ladda-button center-block" id="showmore" title="显示更多前端代码回放" data-style="slide-down">	
	<span class="ladda-label">更多设计</span>
</button>
	

  </body>
</html>
