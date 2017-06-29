<script type="text/javascript">
    $(function(){
    /**
     * 渲染echarts图表
     *  _id 容器id， 必填
     *  _type 图表类型， 必填 (Bar, Line, Pie)
     *  _url 必填
     *  _title 必填
     *  _subTitle 选填
     *  _queryParams 查询json参数, 选填
     *  _nameField 选填
     *  _valueField 选填
     *  _groupField 选填
     */
    var _chartsParamObj = ${_queryParams!"{\}" };
    var _chartsNameField = '${_nameField!"name"}';
    var _chartsValueField = '${_valueField!"value"}';
    var _chartsGroupField = '${_groupField!"group"}';
    //_id参数必填
    var _chartsId = '${_id}';
    //_url参数必填
    var _chartsUrl = '${_url}';
    //标题参数必填
    var _chartsTitle = '${_title}';
    //副标题参数选填
    var _chartsSubTitle = '${_subTitle!}';
    //其它图表参数
    var options = ${_options!"{\}" };

    $.ajax({
        type: "POST",
        url: _chartsUrl,
        data: _chartsParamObj,
        dataType: "json",
        success: function(data){
            <% if(_type!=null && _type=="Pie"){ %>
                var opt = MyECharts.ChartOptionTemplates.Pie(_chartsTitle, _chartsSubTitle, data, _chartsNameField, _chartsValueField, options);
            <% } else if (_type!=null && _type=="Line"){ %>
                var opt = MyECharts.ChartOptionTemplates.Line(_chartsTitle, _chartsSubTitle, data, _chartsNameField, _chartsValueField, _chartsGroupField, options);
            <% } else if (_type!=null && _type=="Bar"){ %>
                var opt = MyECharts.ChartOptionTemplates.Bar(_chartsTitle, _chartsSubTitle, data, _chartsNameField, _chartsValueField, _chartsGroupField, options);
            <% } else{ %>
                var opt = {};
            <% } %>
            var ${_id} = MyECharts.RenderChart(opt, _chartsId);
        }
    });
//    var opt3 = MyECharts.ChartOptionTemplates.Bar("各个城市气温趋势", "°C", data3, _chartsNameField, _chartsValueField, _chartsGroupField);
//    var opt2 = MyECharts.ChartOptionTemplates.Pie("浏览器占比例", "%", data1, _chartsNameField, _chartsValueField, _chartsGroupField);
//    var opt1 = MyECharts.ChartOptionTemplates.Line("各个城市气温趋势", "°C", data2, _chartsNameField, _chartsValueField, _chartsGroupField);
//    var myChart = MyECharts.RenderChart(opt1, "chart");
    });
</script>