//日期选择框的控制
$(function () {
	var currYear = (new Date()).getFullYear();	
	var opt={};
	opt.date = {preset : 'date'};
	opt.datetime = {preset : 'datetime'};
	opt.time = {preset : 'time'};
	opt.default = {
		theme: 'android-ics light', //皮肤样式
        display: 'modal', //显示方式 
        mode: 'mixed', //日期选择模式
		lang:'zh',
        startYear:currYear - 5, //开始年份
        endYear:currYear + 10 //结束年份
	};
	$("#appDate").val('').scroller('destroy').scroller($.extend(opt['date'], opt['default']));
});

//下拉菜单的动作
$(function() {
	$(".dropdown-menu li a").bind("click", function() {
		var $this = $(this);
		var $a = $("#menu_title");
		$a.text($this.text());
		$a.append("<span class='caret'></span>");
	});
});

setDate=function () {
	
	var now = new Date();
	var year = now.getYear();
	var month = now.getMonth();
	var day = now.getDate();
	var date_now =(year+1900)+'-'+(month+1)+'-'+day
	//alert(date_now);
	document.getElementById("appDate").value=date_now;
};
window.onload=setDate;

$(document).ready(function() {

	
	$('table').dataTable({
		paging:false,
		searching:false,
		info:false
	});

});

