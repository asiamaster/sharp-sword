//各种图形需要的数据结构：
/*
 pie图：
 [
 { name: 'Firefox', value: 45.0 },
 { name: 'IE', value: 26.8 },
 { name: 'Safari', value: 8.5 },
 { name: 'Opera', value: 6.2 },
 { name: '其他', value: 0.7 }];

 Line图、Bar图：
 [
 { group: 'Beijing', name: '1月', value: 10 },
 { group: 'Beijing', name: '2月', value: 15 },
 { group: 'Beijing', name: '3月', value: 12 },
 { group: 'Beijing', name: '4月', value: 14 },
 { group: 'Tokyo', name: '1月', value: 12 },
 { group: 'Tokyo', name: '2月', value: 15 },
 { group: 'Tokyo', name: '3月', value: 2 },
 { group: 'Tokyo', name: '4月', value: 14 }];

 //散点图
 var dataAll = [
 [
 [10.0, 8.04],
 [8.0, 6.95],
 [13.0, 7.58],
 [9.0, 8.81],
 [11.0, 8.33],
 [14.0, 9.96],
 [6.0, 7.24],
 [4.0, 4.26],
 [12.0, 10.84],
 [7.0, 4.82],
 [5.0, 5.68]
 ],
 [
 [10.0, 9.14],
 [8.0, 8.14],
 [13.0, 8.74],
 [9.0, 8.77],
 [11.0, 9.26],
 [14.0, 8.10],
 [6.0, 6.13],
 [4.0, 3.10],
 [12.0, 9.13],
 [7.0, 7.26],
 [5.0, 4.74]
 ],
 [
 [10.0, 7.46],
 [8.0, 6.77],
 [13.0, 12.74],
 [9.0, 7.11],
 [11.0, 7.81],
 [14.0, 8.84],
 [6.0, 6.08],
 [4.0, 5.39],
 [12.0, 8.15],
 [7.0, 6.42],
 [5.0, 5.73]
 ],
 [
 [8.0, 6.58],
 [8.0, 5.76],
 [8.0, 7.71],
 [8.0, 8.84],
 [8.0, 8.47],
 [8.0, 7.04],
 [8.0, 5.25],
 [19.0, 12.50],
 [8.0, 5.56],
 [8.0, 7.91],
 [8.0, 6.89]
 ]
 ];
 */

//判断数组中是否包含某个元素
Array.prototype.contains = function (obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}

var MyECharts = {
    //格式化数据
    ChartDataFormat: {
        FormatNOGroupData: function (data, nameField, valueField) {
            if(data == null || data == "" || data.length == 0){
                return {};
            }
            nameField = nameField ||"name";
            valueField = valueField ||"value";
            var categories = [];
            var datas = [];
            for (var i = 0; i < data.length; i++) {
                categories.push(data[i][nameField] || '');
                temp_series = { value: data[i][valueField] || 0, name: data[i][nameField] };
                datas.push(temp_series);
            }
            return { category: categories, data: datas };
        },
        //处理分组数据，数据格式：group：XXX，name：XXX，value：XXX用于折线图、柱形图（分组，堆积）
        //参数：数据、展示类型
        FormatGroupData: function (data, type, nameField, valueField, groupField, smooth) {
            if(data == null || data == "" || data.length == 0){
                return {};
            }
            if(smooth == null){
                smooth = true;
            }
            nameField = nameField ||"name";
            valueField = valueField ||"value";
            groupField = groupField ||"group";
            var groups = new Array();
            var names = new Array();
            var series = new Array();
            for (var i = 0; i < data.length; i++) {
                if (!groups.contains(data[i][groupField])) {
                    groups.push(data[i][groupField]);
                }
                if (!names.contains(data[i][nameField])) {
                    names.push(data[i][nameField]);
                }
            }
            for (var i = 0; i < groups.length; i++) {
                var temp_series = {};
                var temp_data = new Array();
                temp_series = { name: groups[i], type: type, data: temp_data, smooth:smooth };
                for (var k = 0; k < names.length; k++){
                    //判断datas中是否包含name
                    var containsName = false;
                    for (var j = 0; j < data.length; j++) {
                        if (groups[i] == data[j][groupField] && data[j][nameField] == names[k]){
                            containsName = true;
                            temp_data.push(data[j][valueField]);
                        }
                    }
                    //不包含则默认填0
                    if(!containsName){
                        temp_data.push(0);
                    }
                }
                series.push(temp_series);
            }
            return { legend: groups,category: names, series: series };
        }
    },
    //生成图形
    ChartOptionTemplates: {
        //柱状图
        Bar: function (title, subtext, data, nameField, valueField, groupField, opts) {
            var datas = MyECharts.ChartDataFormat.FormatGroupData(data, 'bar', nameField, valueField, groupField);
            var option = {
                title: {
                    text: title || '',
                    subtext: subtext || '',
                    x:'center'
                },
                legend: {
                    data:datas.legend,
                    x:'left',
                    orient: 'vertical'
                },
                tooltip: {
                    trigger: 'axis'
                },
                calculable : true,
                toolbox: {
                    show : true,
                    feature : {
                        mark : {show: true},
                        dataView : {show: true, readOnly: false},
                        magicType : {show: true, type: ['line']},
                        restore : {show: true},
                        saveAsImage : {show: true}
                    }
                },
                xAxis: [
                    {
                        type: 'category',
                        data: datas.category
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: datas.series
            };
            $.extend(true, option, opts);
            return option;
        },
        //折线图
        Line: function (title, subtext, data, nameField, valueField, groupField, opts, smooth) {
            var datas = MyECharts.ChartDataFormat.FormatGroupData(data, 'line', nameField, valueField, groupField, smooth);
            var option = {
                title: {
                    text: title || '',
                    subtext: subtext || '',
                    x:'center'
                },
                legend: {
                    data:datas.legend,
                    x:'left',
                    orient: 'vertical'
                },
                tooltip: {
                    show: true
                },
                toolbox: {
                    show : true,
                    feature : {
                        mark : {show: true},
                        dataView : {show: true, readOnly: false},
                        magicType : {show: true, type: [ 'bar']},
                        restore : {show: true},
                        saveAsImage : {show: true}
                    }
                },
                xAxis: [
                    {
                        type: 'category',
                        data: datas.category
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: datas.series
            };
            $.extend(true, option, opts);
            return option;
        },
        //饼图
        Pie: function (title, subtext, data, nameField, valueField, opts) {
            var datas = MyECharts.ChartDataFormat.FormatNOGroupData(data, nameField, valueField);
            var option = {
                title: {
                    text: title || '',
                    subtext: subtext || '',
                    x:'center'
                },
                tooltip: {
                    show: true,
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                toolbox: {
                    show : true,
                    feature : {
                        mark : {show: true},
                        dataView : {show: true, readOnly: false},
                        magicType : {show: true, type: ['pie']},
                        restore : {show: true},
                        saveAsImage : {show: true}
                    }
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    data: datas.category
                },
                series: [
                    {
                        name: title,
                        type: 'pie',
                        radius: '75%',
                        center: ['50%', '60%'],
                        data: datas.data,
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            };
            $.extend(true, option, opts);
            return option;
        },
        //散点图
        Scatter: function (title, subtext, data, opts) {
            var markLineOpt = {
                animation: false,
                label: {
                    normal: {
                        formatter: 'y = 0.5 * x + 3',
                        textStyle: {
                            align: 'right'
                        }
                    }
                },
                lineStyle: {
                    normal: {
                        type: 'solid'
                    }
                },
                tooltip: {
                    formatter: 'y = 0.5 * x + 3'
                },
                data: [[{
                    coord: [0, 3],
                    symbol: 'none'
                }, {
                    coord: [20, 13],
                    symbol: 'none'
                }]]
            };
            var option = {
                title: {
                    text: title || '',
                    subtext: subtext || '',
                    x: 'center',
                    y: 0
                },
                grid: [
                    {x: '7%', y: '7%', width: '38%', height: '38%'},
                    {x2: '7%', y: '7%', width: '38%', height: '38%'},
                    {x: '7%', y2: '7%', width: '38%', height: '38%'},
                    {x2: '7%', y2: '7%', width: '38%', height: '38%'}
                ],
                tooltip: {
                    formatter: 'Group {a}: ({c})'
                },
                xAxis: [
                    {gridIndex: 0, min: 0, max: 20},
                    {gridIndex: 1, min: 0, max: 20},
                    {gridIndex: 2, min: 0, max: 20},
                    {gridIndex: 3, min: 0, max: 20}
                ],
                yAxis: [
                    {gridIndex: 0, min: 0, max: 15},
                    {gridIndex: 1, min: 0, max: 15},
                    {gridIndex: 2, min: 0, max: 15},
                    {gridIndex: 3, min: 0, max: 15}
                ],
                series: [
                    {
                        name: 'I',
                        type: 'scatter',
                        xAxisIndex: 0,
                        yAxisIndex: 0,
                        data: data[0],
                        markLine: markLineOpt
                    },
                    {
                        name: 'II',
                        type: 'scatter',
                        xAxisIndex: 1,
                        yAxisIndex: 1,
                        data: data[1],
                        markLine: markLineOpt
                    },
                    {
                        name: 'III',
                        type: 'scatter',
                        xAxisIndex: 2,
                        yAxisIndex: 2,
                        data: data[2],
                        markLine: markLineOpt
                    },
                    {
                        name: 'IV',
                        type: 'scatter',
                        xAxisIndex: 3,
                        yAxisIndex: 3,
                        data: data[3],
                        markLine: markLineOpt
                    }
                ]
            };
            // 深浅拷贝对应的参数就是[deep]，是可选的，为true或false。默认情况是false（浅拷贝），并且false是不能够显示的写出来的。如果想写，只能写true（深拷贝）
            $.extend(true, option, opts);
            return option;
        }
    },
    //图形展示
    //参数：图形option、图形显示区域id
    RenderChart: function (option, containerId) {
        var container = eval("document.getElementById('" + containerId + "');");//获取图形显示区域
        var myChart = echarts.init(container);
        myChart.setOption(option);// 为echarts对象加载数据
        return myChart;
    }
};

/**
 * 查询图表
 * @param chartObj echart对象
 * @param type 图表类型，三种:Pie  Line  Bar 默认为Pie
 * @param url   restful url
 * @param queryParams   查询参数 json
 * @param nameField
 * @param valueField
 * @param groupField
 * @param opts
 */
function queryChart(chartObj, type, url, queryParams, nameField, valueField, groupField, opts) {
    $.ajax({
        type: "POST",
        url: url,
        data: queryParams,
        dataType: "json",
        success: function(data){
            //判断如果是BaseOutput，就再取data
            if(data.code != null && data.result != null){
                if(data.code != "200"){
                    alert(data.result);
                    return;
                }
                data = data.data;
            }
            if(data == null || data == ""){
                data = [];
                // chartObj.clear();
            }
            if(type == null){
                type = "Pie";
            }
            var oriOpts = chartObj.getOption();
            // //清空数据,保留画布配置
            // if(oriOpts["legend"]){
            //     oriOpts["legend"]["data"]=[];
            // }
            // if(oriOpts["xAxis"] != null){
            //     oriOpts["xAxis"][0]["data"]=[];
            // }
            // oriOpts["series"]=[];
            if(type == "Pie"){
                opts = MyECharts.ChartOptionTemplates.Pie(oriOpts.title[0].text, oriOpts.title[0].subtext, data, nameField, valueField, opts);
            } else if (type=="Line"){
                opts = MyECharts.ChartOptionTemplates.Line(oriOpts.title[0].text, oriOpts.title[0].subtext, data, nameField, valueField, groupField, opts);
            } else if (type=="Bar"){
                opts = MyECharts.ChartOptionTemplates.Bar(oriOpts.title[0].text, oriOpts.title[0].subtext, data, nameField, valueField, groupField, opts);
            } else{
                opts = {};
            }
            chartObj.setOption(opts, true);
        },error: function () {
            alert('远程访问失败');
        }
    });
}