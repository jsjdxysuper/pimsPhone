var realTimeLineOption  = null;
var columnOption        = null;
var stackColumnOption   = null;
//用来提示差额、超额
var columnRangeDataFlag = new Array();

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
			renderTo: 'realTimeLineContainer'
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
//      	categories: ['1', '2', '3', '4', '5', '6',
//          	'7', '8', '9', '10', '1', '12'], 
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
				var temp = null;
				if(this.series.name=="K")
					temp = KTimes;
				else if(this.series.name=="T")
					temp = TTimes;
				else if(this.series.name=="Y")
					temp = YTimes;
				else if(this.series.name=="Z")
					temp = ZTimes;
				else if(this.series.name=="Q")
					temp = QTimes;
				else if(this.series.name=="H")
					temp = HTimes;
					
				var s = (temp[this.x-1])+'';
				s += '<br/>'+this.y+'兆瓦';
				return s;
			}
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
		url:"/pimsPhone/Plant_plant60GensPowerLineData",
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
setInterval(intervalFunction,60000);

submitRequest= function(){
	//按下按钮之后的效果
	$("#requestButton").css("position","relative").css("top","0px").css("background-color","#0060b0");
	
	var yhid = getUrlVars()["yhid"];
	var date = $("#appDate").val().trim();
	
	
	//取实时曲线数据
	$.ajax({
		url:"/pimsPhone/Plant_plant60GensPowerLineData",
		data:$.param({"date":date,"yhid":yhid}),
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
		
		if(KTimes.length==TTimes.length&&
				TTimes.length==YTimes.length&&
				YTimes.length==ZTimes.length&&
				ZTimes.length==QTimes.length&&
				QTimes.length==HTimes.length&&
				HTimes==0){
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
	      	}];
		
		chart = new Highcharts.Chart(realTimeLineOption);
		
     	//平常按钮的效果
		$("#requestButton").css("position","relative").css("top","2px").css("background-color","#0080b0");
	})
	.fail(function(){
//		alert("链接超时,请刷新");
	});
	
	
	
	//取相关电厂月度发电量柱图数据
	$.ajax({
		url:"/pimsPhone/Plant_oneMonth60GensEnergyColumnData",
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
		url:"/pimsPhone/Plant_year60GensAccuEnergyStackColumnData",
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

