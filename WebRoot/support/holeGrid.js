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
//                  categories: ['1', '2', '3', '4', '5', '6',
//                     '7', '8', '9', '10', '1', '12'], 
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
//                 valueSuffix: '��ǧ��ʱ',
//                 headerFormat:"<span style=\"font-size: 10px\">$(\"#appDate\").value(),{point.key}</span><br/>",
//                 pointFormat:"{series.name}:<b>aaa{point.y}</b>"
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
//                  layout: 'vertical',
//                 align: 'right',
// 				verticalAlign:'top',
// 				floating:'true',
// 				y:50,
//                 borderWidth: 0, 
            }
//             ,
//             series: [{
//                 name: 'ͬ��',
//                 data: [12.0, 12.9, 12.5, 14.5, 18.2, 21.5, 25.2, 14.5, 16.3, 18.3, 13.9, 9.6,
//                 		12.0, 12.9, 12.5, 14.5, 18.2, 21.5, 25.2, 14.5, 16.3, 18.3, 13.9, 9.6,
//                 		12.0, 12.9, 12.5, 14.5, 18.2]
//             }, {
//                 name: '����',
//                 data:  [10.0, 10.9, 12.5, 12.9, 16.2, 22.5, 22.2, 12.5, 12.3, 12.3, 13.9, 16.6,
//                 		17.0, 15.9, 15.5, 15.5, 15.2, 23.5, 22.2, 15.5, 13.3, 14.3, 17.9, 15.6,
//                 		16.0, 12.9, 14.5, 13.5, 11.2]
//             }, {
//                 name: '����',
//                 data:  [12.0, 13.9, 14.5, 14.9, 19.2, 26.5, 23.2, 18.5, 19.3, 19.3, 15.9, 12.6,
//                 		19.0, 10.9, 14.5, 17.5, 19.2, 24.5, 24.2, 18.5, 19.3, 19.3, 15.9, 12.6,
//                 		14.0, 15.9, 15.5, 16.5, 15.2]
//             }]
        };//ȫ����ͼ
        
        chart = new Highcharts.Chart(optionContainer);
        
    });		


	var table_example_id2 = null; 




	$(document).ready(function() {
	
		//$('#table_id1').DataTable();
		if($("#table_example").length > 0)
		{
			table_example_id2 = $("#table_example").DataTable({
// 		        "processing": true,
		        "ajax":{
		        	url:"/pimsPhone/HoleGridServlet",
		        	type:"POST",
		        	data:function ( d ) {
// 		        	alert($('#appDate').serialize());
		        	 return $('#appDate').serialize(); },
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
			      	
			      	
			      	
			      	return json.data;
      			}
		        } ,
		        	paging:false,
					searching:false,
					info:false,
					ordering: false,
					initComplete: function () {
// 					  this.api().on( 'draw', function () {
// 					    alert( 'draw' );
// 					  } );
					}
			});
		}
	
	
	});
		submitRequest = function(){

		if(table_example_id2!=null)
			table_example_id2.ajax.url("/pimsPhone/HoleGridServlet").load();
						
	};


