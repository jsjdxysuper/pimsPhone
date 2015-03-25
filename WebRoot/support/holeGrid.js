var optionContainer =null;
var thisMonthDate = null;
var lastYearDate = null;
var lastMonthDate = null;
var tableColumnName = ["ȫʡ����","ֱ�����","ֱ��ˮ��","ֱ�����","ֱ���˵�","�����߾���"];
			
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
            text: 'ȫ���¶ȷ�����',
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
                text: '���� (��ǧ��ʱ)',
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
//			valueSuffix: '��ǧ��ʱ',
//			headerFormat:"<span style=\"font-size: 10px\">$(\"#appDate\").value(),{point.key}</span><br/>",
//			pointFormat:"{series.name}:<b>aaa{point.y}</b>"
			formatter:function(){
				var temp = null;
				if(this.series.name=="ͬ��")
					temp = lastYearDate;
				else if(this.series.name=="����")
					temp = lastMonthDate;
				else if(this.series.name=="����")
					temp = thisMonthDate;
				
				var s = (temp[this.x-1])+'';
				s += '<br/>'+this.y+'��ǧ��ʱ';
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
//                 name: 'ͬ��',
//                 data: [12.0, 12.9, 12.5, 14.5, 18.2, 21.5, 25.2, 14.5, 16.3, 18.3, 13.9, 9.6,
//                 		12.0, 12.9, 12.5, 14.5, 18.2, 21.5, 25.2, 14.5, 16.3, 18.3, 13.9, 9.6,
//                 		12.0, 12.9, 12.5, 14.5, 18.2]
//             }]
	};//ȫ����ͼ
        
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
		        "dataSrc":function(json){//�ڴ˲������Է����������ݣ��ѱ��ķ��ظ�dataTables����ҳ�����ಿ�ֵĸ���������
			      	//alert(json.line1);
			      	//$("#loading").html(json.line1);
			      	thisMonthDate = json.thisMonthDate;
					lastYearDate = json.lastYearDate;
					lastMonthDate = json.lastMonthDate;
			      	
			      	optionContainer.series=[
			      	{
			      		name: 'ͬ��',
			      		data:json.lastYear
			      	},{
			      		name: '����',
			      		data:json.lastMonth
			      	},{
			      		name: '����',
			      		data:json.thisMonth
			      	}];
			      	chart = new Highcharts.Chart(optionContainer);
			      	
			      //ƽ����ť��Ч��
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
	//���°�ť֮���Ч��
	$("#requestButton").css("position","relative").css("top","0px").css("background-color","#0060b0");
	
	if(table_example_id2!=null)
		table_example_id2.ajax.url("/pimsPhone/HoleGridServlet").load();				
};


