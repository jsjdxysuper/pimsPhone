    var columnOption = null;
    var rangeOption =null;
    var stackColumnOption = null;
    //用来提示差额、超额
    var columnRangeDataFlag = new Array();
    
//是否缓存了
  
    
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




// $(function () {
    	
// 	rangeOption = {
	
// 	    chart: {
// 	        type: 'columnrange',
// 	        inverted: true,
// 	        renderTo:'rangeContainer',
// 	        events:{
// 	        	click: function(e) {
// 				console.log(
// 					Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', e.xAxis[0].value), 
// 					e.yAxis[0].value,e.xAxis[0].value
// 				);
// 			}
// 	        },
// 	        zoomType:'xy'

// 	    },
	    
// 	    title: {
// 	        text: '本厂发电量日历进度'
// 	    },
// 	    exporting:{
// 			enabled:false
// 		},
// 		credits:{
// 			enabled:'false',
// 			text:''
// 		},
// 		labels:{
// 		},
// 	    xAxis: {
// 	        categories: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'],
// 			labels:{
// 			}
// 	    },
	    
// 	    yAxis: {
// 	        title: {
// 	            text: '发电量(亿千瓦时)'
// 	        },
// 			labels:{
// 			}
// 	    },
	
// 	    tooltip: {
// 	       valueSuffix: '亿千瓦时',
// 	        formatter:function(){
// 	        		var high = this.point.high;
// 					var low  = this.point.low;
// 					var diff = (high - low).toFixed(2);
// 					if(columnRangeDataFlag[this.x-1]==1){

// 					var s = '目标电量：'+high+"<br/>"+'完成电量：'+low+'<br/>'+'差额：'+diff;
// 					}
// 					else
// 					{var s = '目标电量：'+low+"<br/>"+'完成电量：'+high+'<br/>'+'超额：'+diff;}
// 					return s;
// 			}
// 	    },
	    
// 	    plotOptions: {
// 	        columnrange: {
// 	        	dataLabels: {
// 	        		enabled: true,
// 	        		formatter: function () {
// 	        			return this.y + '';
// 	        		}             
// 	        	},
//                 colorByPoint:true,//f15c80
//                 colors:['#8085e9','#8085e9','#8085e9', '#8085e9',
//    '#8085e9','#8085e9','#8085e9','#8085e9','#8085e9','#8085e9','#8085e9','#8085e9']
// 	        } 
// 	    },
	    
// 	    legend: {
// 	        enabled: false
// 	    },
       
	
// 	    series: [{
// 	        name: '发电量',
// 	        data: [
// 				[50, 110],
// 				[200, 245],
// 				[300, 313],
// 				[380, 400],
// 				[500, 501],
// 				[600, 613],
// 				[700, 765],
// 				[800, 812],
// 				[876, 900],
// 				[1000, 1023],
// 				[1100, 1134],
// 				[1200, 1243]
// 			]
// 	    }]
	
// 	};
//     chart = new Highcharts.Chart(rangeOption);
// });	

	var sss = 1;
	setInterval(function(){
		$.ajax({
			url:"/pimsPhone/PlantServlet",
			data:$.param({"realtime":true}),
			type:"post",
			beforeSend:function(){
				ajaxbg.show(); 
			}
		
		})
		.done(function(data1,statusText){
		
			ajaxbg.hide();
			var dataReceived = $.evalJSON(data1);
			realTimeData = dataReceived.realTimeData;
			document.getElementById('table_id2').rows[1].cells[0].innerHTML = realTimeData[2].data;
			document.getElementById('table_id2').rows[1].cells[1].innerHTML = realTimeData[3].data;
			document.getElementById('table_id2').rows[2].cells[0].innerHTML = realTimeData[5].data;
			document.getElementById('table_id2').rows[2].cells[1].innerHTML = realTimeData[1].data;
			document.getElementById('table_id2').rows[3].cells[0].innerHTML = realTimeData[4].data;
			document.getElementById('table_id2').rows[3].cells[1].innerHTML = realTimeData[0].data;
			sss++;
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
	
		ajaxbg.hide();
		var dataReceived = $.evalJSON(data1);
		
		realTimeData = dataReceived.realTimeData;
		document.getElementById('table_id2').rows[1].cells[0].innerHTML = realTimeData[2].data;
		document.getElementById('table_id2').rows[1].cells[1].innerHTML = realTimeData[3].data;
		document.getElementById('table_id2').rows[2].cells[0].innerHTML = realTimeData[5].data;
		document.getElementById('table_id2').rows[2].cells[1].innerHTML = realTimeData[1].data;
		document.getElementById('table_id2').rows[3].cells[0].innerHTML = realTimeData[4].data;
		document.getElementById('table_id2').rows[3].cells[1].innerHTML = realTimeData[0].data;
// 		dataReceived.realTimeData
// 		dataReceived.realtimeTime

     	columnOption.series[0].data=dataReceived.columnData;
     	chart = new Highcharts.Chart(columnOption);
     	
     	stackColumnOption.xAxis.categories = dataReceived.seriesPlantName;
     	stackColumnOption.series=dataReceived.yearAccumulatePlantPowerSeries;
     	chart = new Highcharts.Chart(stackColumnOption);
     	
//      	var rangeData = dataReceived.plantProgressData;
//       	for(i=0;i<rangeData.length;i++){
//       		if(rangeData[i][0]>rangeData[i][1]){
//       			var temp = rangeData[i][0];
//       			rangeData[i][0] = rangeData[i][1];
//       			rangeData[i][1] = temp;
//       			rangeOption.plotOptions.columnrange.colors[i] = '#f15c80';
//       			columnRangeDataFlag[i] = 1;
//       		}
//       		else
//       			columnRangeDataFlag[i] = 0;
//       	}
//      	rangeOption.series[0].data = rangeData;
//      	var len = rangeData.length;
//      	var min = rangeData[0][0];
//      	var max = rangeData[len-1][1];
//      	rangeOption.yAxis.min = rangeOption.series[0].data[0][0]-(max-min)/12;
//      	rangeOption.yAxis.max = rangeOption.series[0].data[len-1][1]+(max-min)/10;
//      	chart = new Highcharts.Chart(rangeOption);
     	
     	
	})
	.fail(function(){
//		alert("链接超时,请刷新");
	});
	
// 	var i = 0;
// 	setInterval(function(){
// 		console.log("来了"+(i++));
// 	},2000);
	
	
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

