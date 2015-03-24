var g1Chart;
var g1Option;
var g2Chart;
var g2Option;
var ajaxbg;						
var columnOption = null;
		
$(function () {
	columnOption ={
        chart: {
            type: 'column',
            renderTo: 'columnContainer'
        },
        title: {
            text: '值间月度负荷率对比'
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
				s += '<br/>'+this.y+'%';
				return s;
			}
        },
        series: [{
            name: 'Population',
//                 data: [
//                     ['Shanghai', 23.7],
//                     ['Lagos', 16,1]
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
});//end for $(function () {
		
		
			
$(function () {
					
	var gaugeOptions = {
			
	    chart: {
	        type: 'solidgauge'
	    },
	    
	    title: null,
	    
	    pane: {
	    	center: ['50%', '85%'],
	    	size: '130%',//中间表盘与pane的大小对比，数值越大，表盘就越大
	        startAngle: -90,
	        endAngle: 90,
            background: {
                backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
                innerRadius: '60%',
                outerRadius: '100%',
                shape: 'arc'
            }
	    },
		
	    tooltip: {
	    	enabled: false
	    },
	    credits:{
			enabled:'false',
			text:''
		}, 
			    // the value axis
	    yAxis: {
			stops: [
				[0.1, '#55BF3B'], // green
	        	[0.5, '#DDDF0D'], // yellow
	        	[0.9, '#DF5353'] // red
			],
			lineWidth: 0,
            minorTickInterval: null,
            tickPixelInterval: 400,
            tickWidth: 0,
	        title: {
                y: -40//标题的位置
	        },
            labels: {
                y: 10//表盘刻度位置

            }   
	    },
		        
        plotOptions: {
            solidgauge: {
                dataLabels: {
                    y: 5,
                    borderWidth: 0,
                    useHTML: true
                }
            }
        }
	};//end for var gaugeOptions = {
			    
			    // The speed gauge
			
			    
				//generator1对应的表盘的option
	g1Option = Highcharts.merge(gaugeOptions, {
		chart:{
	    	renderTo: 'g1Container'
	    },
	    yAxis: {
	        min: 0,
	        max: 100,
	        title: {
	            text: '#1机'
	        }       
	    },
	    credits: {
	    	enabled: false
	    },
	    series: [{
	        name: 'rate',
	        data: [0],
	        dataLabels: {
	        	format: '<div style="text-align:center"><span style="font-size:14px;color:' + 
	                ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'gray') + '">{y}</span>' + 
	               	'<span style="font-size:10px;color:gray">%</span></div>'
	        },
	        tooltip: {
	            valueSuffix: ' km/h'
	        }
	    }]
				
	});//end for g1Option
			
			
				//generator2对应的表盘的option
	g2Option = Highcharts.merge
		(gaugeOptions, 
				{
			    	chart:{
			    		renderTo: 'g2Container'
			    	},
			        yAxis:{
			        	min: 0,
			        	max: 100,
				        title:{
				            text: '#2机'
				        },    
				    },
					
				    series:[{
					        name: 'rate',
					        data: [0],
					        dataLabels:{
					        	format: '<div style="text-align:center"><span style="font-size:14px;color:' + 
				                    ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'gray') + '">{y}</span>' + 
				                   	'<span style="font-size:10px;color:gray">%</span></div>'
					        },
					        tooltip:{
					            valueSuffix: ' revolutions/min'
					        }      
					}]
				}
		);//end for g2Option			
});//end for $(function () {
    
submitRequest = function(){
	var date = $("#appDate").val().trim();
	var time_span = $("#menu_title").text().trim();
	$.ajax({
		url:"/pimsPhone/GeneratorServlet",
		data:$.param({"date":date}),
		type:"post",
		beforeSend:function(){
		}
	})
    .done(function(data,statusText){

		var dataReceived = $.evalJSON(data);
		
		columnOption.series[0].data=dataReceived.monthLoadRate;
 		chart = new Highcharts.Chart(columnOption);

		$("#average_power").text(dataReceived.average);
		$("#energy").text(dataReceived.energy);
		$("#timeUse").text(dataReceived.timeUse);
		$("#volume").text(GENERATORVOLUME*2);
		
		$("#g1Volume").text(GENERATORVOLUME);
		$("#g1Energy").text(dataReceived.g1Energy);
		$("#g1Average").text(dataReceived.g1Average);
		$("#g1TimeUse").text(dataReceived.g1TimeUse);
		
		$("#g2Volume").text(GENERATORVOLUME);
		$("#g2Energy").text(dataReceived.g2Energy);
		$("#g2Average").text(dataReceived.g2Average);
		$("#g2TimeUse").text(dataReceived.g2TimeUse);
		
		g1Option.series[0].data[0] = parseFloat((dataReceived.g1Average/GENERATORVOLUME*100).toFixed(2));
		chart = new Highcharts.Chart(g1Option);
		
		g2Option.series[0].data[0] = parseFloat((dataReceived.g2Average/GENERATORVOLUME*100).toFixed(2));
		chart = new Highcharts.Chart(g2Option);
    })
    .fail(function(){
//    	alert("链接超时,请刷新");
    });//end for 	$.ajax({
};//end for submitRequest = function(){


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