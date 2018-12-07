<style>
    .datagrid-mask-msg{
        box-sizing: content-box;
    }
</style>
<script type="text/javascript">
    function S4() {
        return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
    };
    function guid() {
        return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
    };
    //弹出加载层
    function load() {
        $("<div class=\"datagrid-mask\"></div>").css({ display: "block", width: "100%", height: $(window).height() }).appendTo("body");
        $("<div class=\"datagrid-mask-msg\"></div>").html("数据导出中，请稍候。。。").appendTo("body").css({ display: "block", left: ($(document.body).outerWidth(true) - 190) / 2, top: ($(window).height() - 45) / 2 });
    }
    //取消加载层
    function disLoad() {
        $(".datagrid-mask").remove();
        $(".datagrid-mask-msg").remove();
    }
    //当前导出令牌标识
    var token = guid();
    var timeoutId = null;
    //通过token判断导出是否完成
    function checkFinished() {
        if(isFinished(token)){
//            $.messager.progress('close');
            setTimeout(disLoad, 1);
            window.clearTimeout(timeoutId);
        }
    }
    //当前进度值
    var progressValue = 0;

    //判断导出是否完成
    function isFinished(token) {
        var flag = false;
        $.ajax({
            type: "POST",
            url: "${contextPath}/export/isFinished.action?token="+token,
            processData:true,
            dataType: "json",
            async : false,
            success: function (data) {
                if(data==true || data=="true"){
                    flag = true;
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
                $.messager.alert('导出错误','远程访问失败:'+XMLHttpRequest.status+XMLHttpRequest.statusText+","+textStatus,"error");
                flag = true;
            }
        });
        return flag;
    }
    //导出excel
    function doExport(gridId, isTreegrid, exportUrl){
        var opts;
        if(isTreegrid){
            opts=$("#"+gridId).treegrid("options");
        }else{
            opts=$("#"+gridId).datagrid("options");
        }
        //没有url就没有查询过，不作导出
        if(opts.url == null || opts.url == '')
            return;
        var param = {};
        param.columns = JSON.stringify(opts.columns);
        var _gridExportQueryParams = bindMetadata(gridId, false, isTreegrid);
        _gridExportQueryParams["sort"] = opts.sortName;
        _gridExportQueryParams["order"] = opts.sortOrder;
        param.queryParams = JSON.stringify(_gridExportQueryParams);
        param.title = opts.title;
        param.url = opts.url;
        param.token = token;
        if($("#_exportForm").length <= 0) {
            var formStr = "<div id='_exportFormDiv'><form id='_exportForm' class='easyui-form' method='post'>" +
                "<input type='hidden' id='columns' name='columns'/>" +
                "<input type='hidden' id='queryParams' name='queryParams'/>" +
                "<input type='hidden' id='title' name='title'/>" +
                "<input type='hidden' id='url' name='url'/>" +
                "<input type='hidden' id='token' name='token'/>" +
                "</form></div>";
            $(formStr).appendTo("body");
            $.parser.parse("#_exportFormDiv");
        }
        // 显示进度条
//        $.messager.progress({
//            title : "提示",
//            msg : "",
//            text : '数据导出中,请耐心等候...',
//            interval : 300
//        });
        load();
        if(!exportUrl){
            exportUrl = "${contextPath}/export/serverExport.action";
        }
        $('#_exportForm').form("load", param);
        $('#_exportForm').form("submit",{
            url: exportUrl,
            onSubmit: function(formParam) {
                //定时查看是否导出完成
                timeoutId = window.setTimeout(checkFinished, 1);
            },
            success: function(data){
//                $.messager.progress('close');	// 如果提交成功则隐藏进度条
                if(data != null && data != ''){
                    $.messager.alert('导出错误', data, "error");
                }
                disLoad();
            }
        });
    }

    /**
     * 根据controller url导出
     * controller方法调用ExportUtils完成导出, 示例:
     * @RequestMapping("/export")
     * public @ResponseBody void export( HttpServletRequest request, HttpServletResponse response, @RequestParam("queryParams") String queryParams){...}
     * @param url
     * @param params
     */
    function exportByUrl(url, params){
        //没有url就没有查询过，不作导出
        if(url == null || url == '')
            return;
        if($("#_exportByUrlForm").length <= 0) {
            var formStr = "<div id='_exportByUrlFormDiv'><form id='_exportByUrlForm' class='easyui-form' iframe='false' method='post'>" +
                "<input type='hidden' id='queryParams' name='queryParams'/>" +
                "</form></div>";
            $(formStr).appendTo("body");
            $.parser.parse("#_exportByUrlFormDiv");
        }
        var param = {};
        param.queryParams = JSON.stringify(params);
        $('#_exportByUrlForm').form("load", param);
        $('#_exportByUrlForm').form("submit",{
            url:url,
            onSubmit: function(formParam) {
                //定时查看是否导出完成
                timeoutId = window.setTimeout(checkFinished, 1);
            },
            success: function(data){
//                $.messager.progress('close');	// 如果提交成功则隐藏进度条
                if(data != null && data != ''){
                    $.messager.alert('导出错误', data, "error");
                }
                disLoad();
            }
        });
    }
</script>