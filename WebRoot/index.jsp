
<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Highcharts Example</title>

		<script type="text/javascript" src="./support/jquery-1.11.2.min.js"></script>   
		<style type="text/css">
${demo.css}
		</style>
		<script type="text/javascript">
$(function () {

    Highcharts.data({
        csv: document.getElementById('tsv').innerHTML,
        itemDelimiter: '\t',
        parsed: function (columns) {

            var brands = {},
                brandsData = [],
                versions = {},
                drilldownSeries = [];
            
            // Parse percentage strings
            columns[1] = $.map(columns[1], function (value) {
                if (value.indexOf('%') === value.length - 1) {
                    value = parseFloat(value);
                }
                return value;
            });

            $.each(columns[0], function (i, name) {
                var brand,
                    version;

                if (i > 0) {

                    // Remove special edition notes
                    name = name.split(' -')[0];

                    // Split into brand and version
                    version = name.match(/([0-9]+[\.0-9x]*)/);
                    if (version) {
                        version = version[0];
                    }
                    brand = name.replace(version, '');

                    // Create the main data
                    if (!brands[brand]) {
                        brands[brand] = columns[1][i];
                    } else {
                        brands[brand] += columns[1][i];
                    }

                    // Create the version data
                    if (version !== null) {
                        if (!versions[brand]) {
                            versions[brand] = [];
                        }
                        versions[brand].push(['v' + version, columns[1][i]]);
                    }
                }
                
            });

            $.each(brands, function (name, y) {
                brandsData.push({ 
                    name: name, 
                    y: y,
                    drilldown: versions[name] ? name : null
                });
            });
            $.each(versions, function (key, value) {
                drilldownSeries.push({
                    name: key,
                    id: key,
                    data: value
                });
            });

            // Create the chart
            $('#container').highcharts({
                chart: {
                    type: 'column'
                },
                title: {
                    text: 'Browser market shares. November, 2013'
                },
                subtitle: {
                    text: 'Click the columns to view versions. Source: netmarketshare.com.'
                },
                xAxis: {
                    type: 'category'
                },
                yAxis: {
                    title: {
                        text: 'Total percent market share'
                    }
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    series: {
                        borderWidth: 0,
                        dataLabels: {
                            enabled: true,
                            format: '{point.y:.1f}%'
                        }
                    }
                },

//                 tooltip: {
//                     headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
//                     pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
//                 }, 

                series: [{
                    name: 'Brands',
                    colorByPoint: true,
                    data: brandsData
                }],
                drilldown: {
                    series: drilldownSeries
                }
            })

        }
    });
});
    

		</script>
	</head>
	<body>
<script src="./support/highcharts/js/highcharts.js"></script>
<script src="./support/highcharts/js/modules/data.js"></script>
<script src="./support/highcharts/js/modules/drilldown.js"></script>

<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>

<!-- Data from www.netmarketshare.com. Select Browsers => Desktop share by version. Download as tsv. -->
<pre id="tsv" style="display:none">Browser Version	Total Market Share
沈阳 龙源望海寺	26.61%
沈阳 龙源富饶山	16.96%
沈阳 龙源和平	8.01%
沈阳 龙源龙康	7.73%
沈阳 大唐五龙山	6.72%
沈阳 大唐十间房	6.40%
大连 大唐大连风电安台	4.72%
大连 大连华能赵屯（瓦房店）	3.55%
大连 大连横山	3.53%
大连 大连长海	2.16%
大连 大连东岗	1.87%
大连 大连汇鑫望山	1.30%
朝阳 朝阳龙源梨树沟	1.13%
朝阳 朝阳龙源桃花山	0.90%
朝阳 朝阳大唐中三家子	0.85%
朝阳 朝阳大唐小塔子风场	0.65%
朝阳 朝阳大唐公营子风电场	0.55%
朝阳 朝阳大唐石营子风电场	0.50%
朝阳 朝阳大唐双庙风场	0.45%
朝阳 华润北票向阳风电场	0.36%
朝阳 华润建平龙岗风电场	0.36%
朝阳 朝阳华润存珠风电场	0.32%
</pre>

	</body>
</html>
