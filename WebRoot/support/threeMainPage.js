var UNITOFPOWER     = "兆瓦";
var UNITOFENERGY    = "万千瓦时";
var UNITOFTIMEUSE   = "小时";
var GENERATORVOLUME = 600;
var today;


$(function () {
	
	var currYear = (new Date()).getFullYear();	
	var opt={};
	opt.date = {preset : 'date'};
	opt.default = {
		theme: 'android-ics light', //皮肤样式
        display: 'modal', //显示方式 
        mode: 'mixed', //日期选择模式
		lang:'zh',
        startYear:currYear-5, //开始年份
		endYear:currYear//结束年份
	};
	var optJQuery = $.extend(opt['date'], opt['default']);
	
	$("#appDate").val('').mobiscroll(optJQuery).date(optJQuery);
//	$("#appDate").val('').scroller('destroy').scroller($.extend(opt['date'], opt['default']));

});


setDate=function () {
	
	var now = new Date();
	now.setTime(now.getTime()-24*60*60*1000);
	var year = now.getFullYear();
	var month = now.getMonth()+1;
	var day = now.getDate();
	if(0 <month&&month < 10)  month = "0"+month;
	if(0 < day&&day < 10)  day   = "0"+day;
	var date_now =(year)+'-'+month+'-'+day;
	//alert(date_now);
	$("#appDate").val(" "+date_now+" ");
	//$("#background").css("height",document.body.scrollHeight+document.body.scrollTop);
	//如果函数submitRequest已经被初始化了，就调用此函数
	if(typeof(submitRequest) == 'undefined');
	else submitRequest();
};
$(document).ready(setDate);

function getUrlVars()
{
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}



