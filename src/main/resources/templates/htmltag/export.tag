<script type="text/javascript">
    //导出excel
    function doExport(gridId){
        var opts=$("#"+gridId).datagrid("options");
        //没有url就没有查询过，不作导出
        if(opts.url == null || opts.url == '')
            return;
        var param = {};
        param.columns = JSON.stringify(opts.columns);
        param.queryParams = JSON.stringify(bindMetadata(gridId));
        param.title = opts.title;
        param.url = opts.url;
        if($("#_exportForm").length <= 0) {
            var formStr = "<div id='_exportFormDiv'><form id='_exportForm' class='easyui-form' iframe='false' method='post'>" +
                "<input type='hidden' id='columns' name='columns'/>" +
                "<input type='hidden' id='queryParams' name='queryParams'/>" +
                "<input type='hidden' id='title' name='title'/>" +
                "<input type='hidden' id='url' name='url'/>" +
                "</form></div>";
            $(formStr).appendTo("body");
            $.parser.parse("#_exportFormDiv");
        }
//            $.messager.progress();	// 显示进度条
        $('#_exportForm').form("load", param);
        $('#_exportForm').form("submit",{
            url:"${contextPath}/export/serverExport",
            onSubmit: function(formParam) {
                //作一些验证
            },
            success: function(){
                $.messager.progress('close');	// 如果提交成功则隐藏进度条
            }
        });
    }
</script>