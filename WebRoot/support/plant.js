var realTimeLineOption  = null;
var columnOption        = null;
var stackColumnOption   = null;
//用来提示差额、超额
var columnRangeDataFlag = new Array();
var timeStrArray = ['00:00','00:05','00:10','00:15','00:20','00:25','00:30','00:35','00:40','00:45','00:50','00:55',
                    '01:00','01:05','01:10','01:15','01:20','01:25','01:30','01:35','01:40','01:45','01:50','01:55',
                    '02:00','02:05','02:10','02:15','02:20','02:25','02:30','02:35','02:40','02:45','02:50','02:55',
                    '03:00','03:05','03:10','03:15','03:20','03:25','03:30','03:35','03:40','03:45','03:50','03:55',
                    '04:00','04:05','04:10','04:15','04:20','04:25','04:30','04:35','04:40','04:45','04:50','04:55',
                    '05:00','05:05','05:10','05:15','05:20','05:25','05:30','05:35','05:40','05:45','05:50','05:55',
                    '06:00','06:05','06:10','06:15','06:20','06:25','06:30','06:35','06:40','06:45','06:50','06:55',
                    '07:00','07:05','07:10','07:15','07:20','07:25','07:30','07:35','07:40','07:45','07:50','07:55',
                    '08:00','08:05','08:10','08:15','08:20','08:25','08:30','08:35','08:40','08:45','08:50','08:55',
                    '09:00','09:05','09:10','09:15','09:20','09:25','09:30','09:35','09:40','09:45','09:50','09:55',
                    '10:00','10:05','10:10','10:15','10:20','10:25','10:30','10:35','10:40','10:45','10:50','10:55',
                    '11:00','11:05','11:10','11:15','11:20','11:25','11:30','11:35','11:40','11:45','11:50','11:55',
                    '12:00','12:05','12:10','12:15','12:20','12:25','12:30','12:35','12:40','12:45','12:50','12:55',
                    '13:00','13:05','13:10','13:15','13:20','13:25','13:30','13:35','13:40','13:45','13:50','13:55',
                    '14:00','14:05','14:10','14:15','14:20','14:25','14:30','14:35','14:40','14:45','14:50','14:55',
                    '15:00','15:05','15:10','15:15','15:20','15:25','15:30','15:35','15:40','15:45','15:50','15:55',
                    '16:00','16:05','16:10','16:15','16:20','16:25','16:30','16:35','16:40','16:45','16:50','16:55',
                    '17:00','17:05','17:10','17:15','17:20','17:25','17:30','17:35','17:40','17:45','17:50','17:55',
                    '18:00','18:05','18:10','18:15','18:20','18:25','18:30','18:35','18:40','18:45','18:50','18:55',
                    '19:00','19:05','19:10','19:15','19:20','19:25','19:30','19:35','19:40','19:45','19:50','19:55',
                    '20:00','20:05','20:10','20:15','20:20','20:25','20:30','20:35','20:40','20:45','20:50','20:55',
                    '21:00','21:05','21:10','21:15','21:20','21:25','21:30','21:35','21:40','21:45','21:50','21:55',
                    '22:00','22:05','22:10','22:15','22:20','22:25','22:30','22:35','22:40','22:45','22:50','22:55',
                    '23:00','23:05','23:10','23:15','23:20','23:25','23:30','23:35','23:40','23:45','23:50','23:55'];//日期选择框的控制

//realTimeLine的时间轴变量
var KTimes = null;
var TTimes = null;
var YTimes = null;
var ZTimes = null;
var QTimes = null;
var HTimes = null;
 
$(function () {	
	
	realTimeLineOption={
		chart:{
			spacingLeft:0,
			renderTo: 'realTimeLineContainer'//,
            //zoomType: "x"
		},
		//colors:['#00ff00','#0000ff','#ff0000'],
		plotOptions:{
			line:{
				lineWidth:2,
				pointStart:1,
				marker:{
					enabled:false
				}
			}
		},
		exporting:{
			enabled:false
		},
		credits:{
			enabled:'false',
			text:''
		},
		labels:{
		},
        title: {
        	text: '',
//          x: -20, //center 
        },
        subtitle: {
        	text: '',
            x: -20
        },
        xAxis: {
//      	categories: ['0', '4', '8', '12', '16', '20',
//          	'24'], 
//            labels: {
//                step: 50
//            }
            },
        yAxis: {
            title: {
            	text: '',
            },
            tickInterval:50,
            plotLines: [{
            	value: 0,
            	width: 1,
            	color: '#808080'
            }],
			labels:{
			}
        },
        tooltip: {
//      	valueSuffix: '万千瓦时',
//      	headerFormat:"<span style=\"font-size: 10px\">$(\"#appDate\").value(),{point.key}</span><br/>",
//      	pointFormat:"{series.name}:<b>aaa{point.y}</b>"
			formatter:function(){
				//(timeStrArray[this.x-1])
				 //= +'';
				//'<br/>'+
				var s = this.x+'';
				for(i=0;i<this.points.length;i++){
					s+= '<br/>'+this.points[i].series.name+':'+this.points[i].y+'兆瓦';
				}
				return s;
			},
			crosshairs:[true,false],
			shared:true
        },
        legend: {
//			layout: 'vertical',
//			align: 'right',
//			verticalAlign:'top',
// 			floating:'true',
// 			y:50,
//      	borderWidth: 0, 
        },
//      series: [{
//      	name: '同期',
//      	data: [12.0, 12.9, 12.5, 14.5, 18.2, 21.5, 25.2, 14.5, 16.3, 18.3, 13.9, 9.6,
//      	12.0, 12.9, 12.5, 14.5, 18.2, 21.5, 25.2, 14.5, 16.3, 18.3, 13.9, 9.6,
//      	12.0, 12.9, 12.5, 14.5, 18.2]
//          },]
        };//全网线图
        
        chart = new Highcharts.Chart(realTimeLineOption);
});//end for $(function () {
    
    
    
    
$(function () {
	columnOption ={
		chart: {
            type: 'column',
            renderTo: 'columnContainer'

        },
        title: {
            text: '相关电厂月度发电量'
        },
        subtitle: {
            text: ''
        },
        exporting:{
			enabled:false
		},
		credits:{
			enabled:'false',
			text:''
		},
        xAxis: {
            type: 'category',
            labels: {
                rotation: -45,
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: {
            min: 0,
            title: {
                text: null
            }
        },
        legend: {
            enabled: false
        },
        tooltip: {
            //pointFormat: '月度发电量<b>{point.y:.1f} (万千瓦时)</b>',
            formatter:function(){
				var month = $("#appDate").val().substr(5,2);
				var s = this.series.data[this.x].name+month+'月份';
				s += '<br/>'+this.y+'万千瓦时';
				return s;
			}
        },
        series: [{
            name: 'Population',
//          data: [
//          	['Shanghai', 23.7],
//          	['Lagos', 16,1]
//          	],
            dataLabels: {
                enabled: true,
                rotation: -90,
                color: '#FFFFFF',
                align: 'right',
                x: 4,
                y: 10,
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif',
                    textShadow: '0 0 3px black'
                }
            }
        }]
	};
	chart = new Highcharts.Chart(columnOption);
});//end for $(function () {

	

$(function () {
	stackColumnOption ={
        chart: {
            type: 'bar',
            renderTo:'stackColumnContainer'
        },
        title: {
            text: '相关电厂年累积发电量对标'
        },
		exporting:{
			enabled:false
		},
		credits:{
			enabled:'false',
			text:''
		},
		labels:{
		},
		tooltip: {
    	},
        xAxis: {
            categories: ['K', 'Z', 'Y', 'T', 'Q','Y'],
			labels:{
			}
        },
        yAxis: {
            min: 0,
            title: {
                text: null
            },
			labels:{
			}
        },
        tooltip:{
        	formatter:function(){
				
				var s = this.x+this.series.name+'份'+'<br/>';
				s += this.y+'万千瓦时';
				return s;
			}
        },
        legend: {
            reversed: false
        },
        plotOptions: {
            series: {
                stacking: 'normal'
            }
        }
//                 series: [{
//                 name: '1',
//                 data: [5, 3, 4, 7, 2,7]
//             }, {
//                 name: '2',
//                 data: [2, 2, 3, 2, 1, 3]
//             }, {
//                 name: '3',
//                 data: [3, 4, 4, 2, 5, 2]
//             }
// 			]
	};
        
	chart = new Highcharts.Chart(stackColumnOption);
});//end for $(function () {



intervalFunction = function(){
	var yhid = getUrlVars()["yhid"];
	if($("#appDate").length>0)
		var date = $("#appDate").val().trim();
	else
		var date = null;
	$.ajax({
		url:"Plant_plant60GensPowerLineData",
		data:$.param({"realtime":true,"date":date,"yhid":yhid}),
		type:"post",
		beforeSend:function(){
		}
	})
	.done(function(plant60GenPower,statusText){

		//给实时曲线的时间轴变量赋值
		KTimes = plant60GenPower.KTimes;
		TTimes = plant60GenPower.TTimes;
		YTimes = plant60GenPower.YTimes;
		ZTimes = plant60GenPower.ZTimes;
		QTimes = plant60GenPower.QTimes;
		HTimes = plant60GenPower.HTimes;
//		var xAxis = null;
//		for(i=0;i<YTimes.length;i++){
//			xAxis[i] = YTimes[i].substring(0,2);
//		}
		//构造实时有功曲线的数据
		realTimeLineOption.series=[
	      	{
	      		name: 'K',
	      		data:plant60GenPower.K
	      	},{
	      		name: 'T',
	      		data:plant60GenPower.T
	      	},{
	      		name: 'Y',
	      		data:plant60GenPower.Y
	      	},{
	      		name: 'Z',
	      		data:plant60GenPower.Z
	      	},{
	      		name: 'Q',
	      		data:plant60GenPower.Q
	      	},{
	      		name: 'H',
	      		data:plant60GenPower.H
	      	}
	      	];
		
		chart = new Highcharts.Chart(realTimeLineOption);

	})
	.fail(function(){
	});
};

var sss = 1;
//setInterval(intervalFunction,3000);

submitRequest= function(){
	//按下按钮之后的效果
	$("#requestButton").css("position","relative").css("top","0px").css("background-color","#0060b0");
	
	var yhid = getUrlVars()["yhid"];
	var date = $("#appDate").val().trim();
	sessionStorage.setItem("date",date);
	
	//取实时曲线数据
	$.ajax({
		url:"Plant_plant60GensPowerLineData",
		data:$.param({"date":date,"yhid":yhid}),
		type:"post",
		beforeSend:function(){
		}
	})
	.done(function(lineData,statusText){

		//给实时曲线的时间轴变量赋值

		var xAxis = new Array();
		for(i=0;i<timeStrArray.length;i++){
			xAxis[i] = timeStrArray[i].substring(0,2);
		}
		plant60GenPower = $.parseJSON(lineData.plantLinePower);
		var lineDatat = plant60GenPower.lineData;
		if(lineDatat[0].powers.length==lineDatat[1].powers.length&&
				lineDatat[1].powers.length==lineDatat[2].powers.length&&
				lineDatat[2].powers.length==lineDatat[3].powers.length&&
				lineDatat[3].powers.length==lineDatat[4].powers.length&&
				lineDatat[4].powers.length==lineDatat[5].powers.length&&
				lineDatat[5].powers.length==0){
			$("#textWhenNoData").css("display","block");
			$("#realTimeLineContainer").css("display","none");
		}
		else{
			$("#textWhenNoData").css("display","none");
			$("#realTimeLineContainer").css("display","block");
		}
		//构造实时有功曲线的数据
		realTimeLineOption.series=[
	      	{
	      		name: lineDatat[0].nickName,
	      		data:lineDatat[0].powers
	      	},{
	      		name: lineDatat[1].nickName,
	      		data:lineDatat[1].powers
	      	},{
	      		name: lineDatat[2].nickName,
	      		data:lineDatat[2].powers
	      	},{
	      		name: lineDatat[3].nickName,
	      		data:lineDatat[3].powers
	      	},{
	      		name: lineDatat[4].nickName,
	      		data:lineDatat[4].powers
	      	},{
	      		name: lineDatat[5].nickName,
	      		data:lineDatat[5].powers
	      	}];
		realTimeLineOption.xAxis.categories=timeStrArray;
		realTimeLineOption.xAxis.tickInterval=40;
		chart = new Highcharts.Chart(realTimeLineOption);
		
     	//平常按钮的效果
		$("#requestButton").css("position","relative").css("top","2px").css("background-color","#0080b0");
	})
	.fail(function(){
//		alert("链接超时,请刷新");
	});
	
	
	
	//取相关电厂月度发电量柱图数据
	$.ajax({
		url:"Plant_oneMonth60GensEnergyColumnData",
		data:$.param({"date":date,"yhid":yhid}),
		type:"post",
		beforeSend:function(){
		}
	})
	.done(function(recData,statusText){
	
		
     	columnOption.series[0].data=recData.oneMonth60GensEnergyColumnData;
     	chart = new Highcharts.Chart(columnOption);
		
     	//平常按钮的效果
		$("#requestButton").css("position","relative").css("top","2px").css("background-color","#0080b0");
	})
	.fail(function(){
	});
	
	//取相关电厂年累计发电量数据
	$.ajax({
		url:"Plant_year60GensAccuEnergyStackColumnData",
		data:$.param({"date":date,"yhid":yhid}),
		type:"post",
		beforeSend:function(){
		}
	})
	.done(function(oneMonth60GensEnergyColumnData,statusText){
	
     	stackColumnOption.xAxis.categories = oneMonth60GensEnergyColumnData.seriesPlantName;
     	stackColumnOption.series=oneMonth60GensEnergyColumnData.yearAccumulatePlantPowerSeries;
     	chart = new Highcharts.Chart(stackColumnOption); 
		
     	//平常按钮的效果
		$("#requestButton").css("position","relative").css("top","2px").css("background-color","#0080b0");
	})
	.fail(function(){
	});
	
	$(document).ready(function() {
	
		//$('#table_id1').DataTable();
		if($("table").length > 0)
		{
			$("table").dataTable({
				paging:false,
				searching:false,
				info:false,
				ordering: false,
			});
		}
	});//end for $(document).ready(function() {
};//end for submitRequest= function(){
//默认日期设置为当天
$(document).ready(function(){
	var sessionDate = sessionStorage.getItem("date");
	if(sessionDate== null)
		setDate(0);
	else
		$("#appDate").val(sessionDate);
	});
$(document).ready(submitRequest);