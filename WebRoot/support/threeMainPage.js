var UNITOFPOWER     = "兆瓦";
var UNITOFENERGY    = "万千瓦时";
var UNITOFTIMEUSE   = "小时";
var GENERATORVOLUME = 600;
var today;
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
        endYear:currYear + 1 //结束年份
	};
	$("#appDate").val('').scroller('destroy').scroller($.extend(opt['date'], opt['default']));
});




setDate=function () {
	
	var now = new Date();
	var year = now.getYear();
	var month = now.getMonth()+1;
	var day = now.getDate();
	if(0 <month&&month < 10)  month = "0"+month;
	if(0 < day&&day < 10)  day   = "0"+day;
	var date_now =(year+1900)+'-'+month+'-'+day;
	//alert(date_now);
	document.getElementById("appDate").value=date_now;
	//$("#background").css("height",document.body.scrollHeight+document.body.scrollTop);
	ajaxbg = $("#background,#progressBar"); 
	//如果函数submitRequest已经被初始化了，就调用此函数
	if(typeof(submitRequest) == 'undefined');
	else submitRequest();
};
$(document).ready(setDate);



