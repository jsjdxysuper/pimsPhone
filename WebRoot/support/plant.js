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
                text: '全网月度发电量',
//                  x: -20, //center 
            },
            subtitle: {
                text: '',
                x: -20
            },
            xAxis: {
//                  categories: ['1', '2', '3', '4', '5', '6',
//                     '7', '8', '9', '10', '1', '12'], 
            },
            yAxis: {
				min:276,
				max:1500,
                title: {
                    text: '电量 (万千瓦时)',
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
//                 valueSuffix: '万千瓦时',
//                 headerFormat:"<span style=\"font-size: 10px\">$(\"#appDate\").value(),{point.key}</span><br/>",
//                 pointFormat:"{series.name}:<b>aaa{point.y}</b>"
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
//                  layout: 'vertical',
//                 align: 'right',
// 				verticalAlign:'top',
// 				floating:'true',
// 				y:50,
//                 borderWidth: 0, 
            }
//             ,
//             series: [{
//                 name: '同期',
//                 data: [12.0, 12.9, 12.5, 14.5, 18.2, 21.5, 25.2, 14.5, 16.3, 18.3, 13.9, 9.6,
//                 		12.0, 12.9, 12.5, 14.5, 18.2, 21.5, 25.2, 14.5, 16.3, 18.3, 13.9, 9.6,
//                 		12.0, 12.9, 12.5, 14.5, 18.2]
//             }, {
//                 name: '上月',
//                 data:  [10.0, 10.9, 12.5, 12.9, 16.2, 22.5, 22.2, 12.5, 12.3, 12.3, 13.9, 16.6,
//                 		17.0, 15.9, 15.5, 15.5, 15.2, 23.5, 22.2, 15.5, 13.3, 14.3, 17.9, 15.6,
//                 		16.0, 12.9, 14.5, 13.5, 11.2]
//             }, {
//                 name: '本月',
//                 data:  [12.0, 13.9, 14.5, 14.9, 19.2, 26.5, 23.2, 18.5, 19.3, 19.3, 15.9, 12.6,
//                 		19.0, 10.9, 14.5, 17.5, 19.2, 24.5, 24.2, 18.5, 19.3, 19.3, 15.9, 12.6,
//                 		14.0, 15.9, 15.5, 16.5, 15.2]
//             }]
        };//全网线图
        
        chart = new Highcharts.Chart(realTimeLineOption);
        
    });	
    
    
    
    
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
//                 data: [
//                     ['Shanghai', 23.7],
//                     ['Lagos', 16,1],
//                     ['Instanbul', 14.2],
//                     ['Karachi', 14.0],
//                     ['Mumbai', 12.5],
//                     ['Moscow', 12.1],
//                     ['São Paulo', 11.8],
//                     ['Beijing', 11.7],
//                     ['Guangzhou', 11.1],
//                     ['Delhi', 11.1],
//                     ['Shenzhen', 10.5],
//                     ['Seoul', 10.4],
//                     ['Jakarta', 10.0],
//                     ['Kinshasa', 9.3],
//                     ['Tianjin', 9.3],
//                     ['Tokyo', 9.0],
//                     ['Cairo', 8.9],
//                     ['Dhaka', 8.9],
//                     ['Mexico City', 8.9],
//                     ['Lima', 8.9]
//                 ],
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
    });

	

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
//             }, {
//                 name: '4',
//                 data: [3, 4, 4, 2, 5, 4]
//             }, {
//                 name: '5',
//                 data: [3, 4, 4, 2, 5, 4]
//             }, {
//                 name: '6',
//                 data: [3, 4, 4, 2, 5, 4]
//             }, {
//                 name: '7',
//                 data: [3, 4, 4, 2, 5, 4]
//             }, {
//                 name: '8',
//                 data: [3, 4, 4, 2, 5, 4]
//             }, {
//                 name: '9',
//                 data: [3, 4, 4, 2, 5, 4]
//             }, {
//                 name: '10',
//                 data: [3, 4, 4, 2, 5, 4]
//             }, {
//                 name: '11',
//                 data: [3, 4, 4, 2, 5, 4]
//             }, {
//                 name: '12',
//                 data: [3, 4, 4, 2, 5, 4]
//             }
// 			]
        };
        
       chart = new Highcharts.Chart(stackColumnOption);
    });




	var sss = 1;
	setInterval(function(){
		$.ajax({
			url:"/pimsPhone/PlantServlet",
			data:$.param({"realtime":true}),
			type:"post",
			beforeSend:function(){
//				ajaxbg.show(); 
			}
		
		})
		.done(function(data1,statusText){
		
		})
		.fail(function(){
//			alert("链接超时,请刷新");
		});
	},60000);

submitRequest= function(){
	var date = $("#appDate").val().trim();


	//取历史数据,捎带把实时数据取回来
	$.ajax({
		url:"/pimsPhone/PlantServlet",
		data:$.param({"date":date}),
		type:"post",
		beforeSend:function(){
			ajaxbg.show(); 
		}
	
	})
	.done(function(data1,statusText){
	
//		ajaxbg.hide();
		var dataReceived = $.evalJSON(data1);
//		alert("缓存测试2");
		plant60GenPower = dataReceived.plant60GenPower;

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
      	  			      	}];
		
		realTimeLineOption.yAxis.max = plant60GenPower.maxRealTime;
		realTimeLineOption.yAxis.min = plant60GenPower.minRealtime;
		
		chart = new Highcharts.Chart(realTimeLineOption);
		
     	columnOption.series[0].data=dataReceived.columnData;
     	chart = new Highcharts.Chart(columnOption);
     	
     	stackColumnOption.xAxis.categories = dataReceived.seriesPlantName;
     	stackColumnOption.series=dataReceived.yearAccumulatePlantPowerSeries;
     	chart = new Highcharts.Chart(stackColumnOption);
     	
     	
     	
	})
	.fail(function(){
//		alert("链接超时,请刷新");
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
	
	
		
	});
};

