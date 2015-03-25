var optionContainer =null;
var thisMonthDate = null;
var lastYearDate = null;
var lastMonthDate = null;
var tableColumnName = ["全省发电","直调火电","直调水电","直调风电","直调核电","联络线净受"];
			
$(function () {
	optionContainer={
		chart:{
			spacingLeft:0,
			renderTo: 'lineContainer'
		},
		colors:['#00ff00','#0000ff','#ff0000'],
		plotOptions:{
			line:{
				lineWidth:2,
				pointStart:1
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
//			categories: ['1', '2', '3', '4', '5', '6',
//			 '7', '8', '9', '10', '1', '12'], 
        },
        yAxis: {
            title: {
                text: '电量 (万千瓦时)',
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
				
            }],
			labels:{
			}
        },
        tooltip: {
//			valueSuffix: '万千瓦时',
//			headerFormat:"<span style=\"font-size: 10px\">$(\"#appDate\").value(),{point.key}</span><br/>",
//			pointFormat:"{series.name}:<b>aaa{point.y}</b>"
			formatter:function(){
				var temp = null;
				if(this.series.name=="同期")
					temp = lastYearDate;
				else if(this.series.name=="上月")
					temp = lastMonthDate;
				else if(this.series.name=="本月")
					temp = thisMonthDate;
				
				var s = (temp[this.x-1])+'';
				s += '<br/>'+this.y+'万千瓦时';
				return s;
			}
        },
        legend: {
//        	layout: 'vertical',
//        	align: 'right',
//        	verticalAlign:'top',
//        	floating:'true',
// 			y:50,
// 			borderWidth: 0, 
        }
//             ,
//             series: [{
//                 name: '同期',
//                 data: [12.0, 12.9, 12.5, 14.5, 18.2, 21.5, 25.2, 14.5, 16.3, 18.3, 13.9, 9.6,
//                 		12.0, 12.9, 12.5, 14.5, 18.2, 21.5, 25.2, 14.5, 16.3, 18.3, 13.9, 9.6,
//                 		12.0, 12.9, 12.5, 14.5, 18.2]
//             }]
	};//全网线图
        
    chart = new Highcharts.Chart(optionContainer);
});//end for $(function () {	


var table_example_id2 = null; 




$(document).ready(function() {

	if($("#table_example").length > 0)
	{
		table_example_id2 = $("#table_example").DataTable({
	// 		"processing": true,
		    "ajax":{
	        	url:"/pimsPhone/HoleGridServlet",
	        	type:"POST",
	        	data:function ( d ) {
	// 		        	alert($('#appDate').serialize());
	        		var date = $("#appDate").val().trim();
	        		return {date:date}; 
	        	},
		        "dataSrc":function(json){//在此操作来自服务器的数据，把表格的返回给dataTables，把页面其余部分的给拦截下来
			      	//alert(json.line1);
			      	//$("#loading").html(json.line1);
			      	thisMonthDate = json.thisMonthDate;
					lastYearDate = json.lastYearDate;
					lastMonthDate = json.lastMonthDate;
			      	
			      	optionContainer.series=[
			      	{
			      		name: '同期',
			      		data:json.lastYear
			      	},{
			      		name: '上月',
			      		data:json.lastMonth
			      	},{
			      		name: '本月',
			      		data:json.thisMonth
			      	}];
			      	chart = new Highcharts.Chart(optionContainer);
			      	
			      //平常按钮的效果
					$("#requestButton").css("position","relative").css("top","2px").css("background-color","#0080b0");
			      	return json.data;
	  			}//end for "dataSrc":function(json){
		    },//end for "ajax":{
			paging:false,
			searching:false,
			info:false,
			ordering: false,
			initComplete: function () {
	//	 		this.api().on( 'draw', function () {
	//	 			alert( 'draw' );
	//	 		});
			}
		});//end for table_example_id2 = $("#table_example").DataTable({
	}//end for if($("#table_example").length > 0)
});

submitRequest = function(){
	//按下按钮之后的效果
	$("#requestButton").css("position","relative").css("top","0px").css("background-color","#0060b0");
	
	if(table_example_id2!=null)
		table_example_id2.ajax.url("/pimsPhone/HoleGridServlet").load();				
};


