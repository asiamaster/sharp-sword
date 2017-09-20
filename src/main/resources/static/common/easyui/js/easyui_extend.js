//============================  属性定义  ============================


//============================  方法定义  ============================

//表格日期格式化器
function dateFormatter(value) {
    var date = new Date(value);
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    var d = date.getDate();
    return y + '-' + m + '-' + d;
}

/**
 * 表格时间格式化
 * @param value
 * @returns {string}
 */
function datetimeFormatter (value) {
    var date = new Date(value);
    var year = date.getFullYear().toString();
    var month = (date.getMonth() + 1);
    var day = date.getDate().toString();
    var hour = date.getHours().toString();
    var minutes = date.getMinutes().toString();
    var seconds = date.getSeconds().toString();
    if (month < 10) {
        month = "0" + month;
    }
    if (day < 10) {
        day = "0" + day;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (minutes < 10) {
        minutes = "0" + minutes;
    }
    if (seconds < 10) {
        seconds = "0" + seconds;
    }
    return year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + seconds;
}

/**
 * 绑定实体的metadata信息，用于提供者转换
 * @param gridId    datagrid Id
 * @param isClearQueryParams    是否清空datagrid中原有的queryParams, 默认为false,不清空
 * @returns {queryParams|{provider}|*|string|{}}
 */
function bindMetadata(gridId, isClearQueryParams){
    var opts=$("#"+gridId).datagrid("options");
    //赋默认值
    isClearQueryParams = isClearQueryParams || false;
    var params = isClearQueryParams ? {} : opts.queryParams || {};
    //获取最后一行的列(可能是多表头)
    var lastColumns = opts.columns[opts.columns.length-1];
    params["metadata"] = {};
    for(var column in lastColumns){
        var _provider = lastColumns[column]["_provider"];
        var _data = lastColumns[column]["_data"];
        //优先解析直接数据的_data属性
        if(_data != null){
            var field = lastColumns[column]["field"];
            var fieldMetadata = {};
            fieldMetadata["provider"] = "simpleDataProvider";
            fieldMetadata["data"] = _data;
            params["metadata"][field] = JSON.stringify(fieldMetadata);
            continue;
        }
        //没有_data属性，则解析_table,_valueField和_textField等其它属性
        var _table = lastColumns[column]["_table"];
        var _valueField = lastColumns[column]["_valueField"];
        var _textField = lastColumns[column]["_textField"];
        var _queryParams = lastColumns[column]["_queryParams"];
        //如果有表信息，则使用simpleValueProvider
        if(_table != null){
            _provider = "simpleValueProvider";
        }
        if(_provider != null){
            var field = lastColumns[column]["field"];
            var fieldMetadata = {};
            fieldMetadata["provider"] = _provider;
            fieldMetadata["table"] = _table;
            fieldMetadata["valueField"] = _valueField;
            fieldMetadata["textField"] = _textField;
            fieldMetadata["queryParams"] = _queryParams;
            params["metadata"][field] = JSON.stringify(fieldMetadata);
        }
    }
    return params;
}

//树的转换加载
function convertTree(rows){
    function exists(rows, parentId){
        for(var i=0; i<rows.length; i++){
            if (rows[i].id == parentId) return true;
        }
        return false;
    }

    var nodes = [];
    // 得到顶层节点
    for(var i=0; i<rows.length; i++){
        var row = rows[i];
        if (!exists(rows, row.parentId)){
            nodes.push({
                id:row.id,
                text:row.text,
                state:row["state"],
                attributes:row["attributes"]
            });
        }
    }

    var toDo = [];
    for(var i=0; i<nodes.length; i++){
        toDo.push(nodes[i]);
    }
    while(toDo.length){
        var node = toDo.shift();    // 父节点
        // 得到子节点
        for(var i=0; i<rows.length; i++){
            var row = rows[i];
            if (row.parentId == node.id){
                var child = {
                    id:row.id,
                    text:row.text,
                    state:row["state"],
                    attributes:row["attributes"]
                };
                if (node.children){
                    node.children.push(child);
                } else {
                    node.children = [child];
                }
                toDo.push(child);
            }
        }
    }
    return nodes;
}
//树加载过滤器
var treeLoadFilter = function(data,parent){
    var idField = $(this).tree("options")["_idField"];
    var textField = $(this).tree("options")["_textField"];
    var parentIdField = $(this).tree("options")["_parentIdField"];
    if(idField && idField != null && idField != ""){
        modifyJsonKey(data,idField,"id");
    }
    if(textField && textField != null && textField != ""){
        modifyJsonKey(data,textField,"text");
    }
    if(parentIdField && parentIdField != null && parentIdField != ""){
        modifyJsonKey(data,parentIdField,"parentId");
    }
    return convertTree(data);
}

//树表加载过滤器
var treegridLoadFilter = function(data,parent){
    var parentIdField = $(this).treegrid("options")["_parentIdField"];
    if(parentIdField && parentIdField != null && parentIdField != ""){
        modifyJsonKey(data.rows,parentIdField,"_parentId");
    }
    return data;
}

//修改json对象或数组中的key
function modifyJsonKey(json,oldkey,newkey){
    if(json instanceof Array){
        for(var i in json){
            var obj = json[i];
            modifyJsonKey(json[i],oldkey,newkey);
        }
    }else{
        var val = json[oldkey];
        delete json[oldkey];
        json[newkey]=val;
    }
}

//修改表格边框
function changeBorder(gridId, cls){
    $('#'+gridId).datagrid('getPanel').removeClass('lines-both lines-no lines-right lines-bottom').addClass(cls);
}

$(function() {
    //自定义日期解析
    $.fn.datebox.defaults.parser = function(s){
        if (!s) return new Date();
        var ss = (s.split('-'));
        var y = parseInt(ss[0],10);
        var m = parseInt(ss[1],10);
        var d = parseInt(ss[2],10);
        if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
            return new Date(y,m-1,d);
        } else {
            return new Date();
        }
    }
    //自定义日期格式化
    $.fn.datebox.defaults.formatter = function(date){
        var y = date.getFullYear();
        var m = date.getMonth()+1;
        var d = date.getDate();
        return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
    }
    //自定义日期时间解析
    $.fn.datetimebox.defaults.parser = function(s){
        if (!s) return new Date();
        var y = s.substring(0,4);
        var m =s.substring(5,7);
        var d = s.substring(8,10);
        var h = s.substring(11,13);
        var min = s.substring(14,16);
        var sec = s.substring(17,19);
        if (!isNaN(y) && !isNaN(m) && !isNaN(d) && !isNaN(h) && !isNaN(min) && !isNaN(sec)){
            return new Date(y,m-1,d,h,min,sec);
        } else {
            return new Date();
        }
    }
//自定义日期时间格式化
    $.fn.datetimebox.defaults.formatter = function(date){
        var y = date.getFullYear();
        var m = date.getMonth()+1;
        var d = date.getDate();
        var h = date.getHours();
        var min = date.getMinutes();
        var sec = date.getSeconds();
        return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d)+' '+(h<10?('0'+h):h)+':'+(min<10?('0'+min):min)+':'+(sec<10?('0'+sec):sec);
    }
    //文本框添加清空按钮
    $.extend($.fn.textbox.methods, {
        addClearBtn: function(jq, iconCls){
            return jq.each(function(){
                var t = $(this);
                var opts = t.textbox('options');
                opts.icons = opts.icons || [];
                opts.icons.unshift({
                    iconCls: iconCls,
                    handler: function(e){
                        $(e.data.target).textbox('clear').textbox('textbox').focus();
                        $(this).css('visibility','hidden');
                    }
                });
                t.textbox();
                if (!t.textbox('getText')){
                    t.textbox('getIcon',0).css('visibility','hidden');
                }
                t.textbox('textbox').bind('keyup', function(){
                    var icon = t.textbox('getIcon',0);
                    if ($(this).val()){
                        icon.css('visibility','visible');
                    } else {
                        icon.css('visibility','hidden');
                    }
                });
            });
        }
    });

    /**
     * 根据字段按百分比重置列宽
     * 示例:
     * grid.datagrid('resizeColumn', [{
 *							field : 'id',
 *							width : '35%'
 *						}, {
 *							field : 'name',
 *							width : '35%'
 *						}]);
     */
    $.extend($.fn.datagrid.methods, {
        resizeColumn : function(jq, param) {
            return jq.each(function() {
                var dg = $(this);
                var fn = function(item) {
                    var col = dg.datagrid('getColumnOption', item.field);
                    col.width = item.width;
                    if (typeof(col.width) == 'string') {
                        var width = parseInt(col.width.replace('%', ''));
                        col.boxWidth = col.boxWidth * width / 100;
                    } else {
                        col.boxWidth = col.width;
                    }
                    dg.datagrid('fixColumnSize', param.field);
                };
                if (param instanceof Array) {
                    $(param).each(function(index, item) {
                        fn.call(this, item);
                    });
                } else {
                    fn.call(this, param);
                }
            })
        }
    });
});


/*******************************************************************************
 * 表单光标定位
 *
 * @param formId
 * @param focusInputName
 */
function formFocus(formId, focusInputId) {
    window.setTimeout(function() {
        //所有form元素都继承textbox，所以直接获取textbox对象来获取焦点
        $("#"+ focusInputId).textbox('textbox').focus();
    }, 0);
}

function formFocusTextArea(formId, focusInputNameId) {
    window.setTimeout(function() {
        $("#" + formId + " #" + focusInputName).focus();
    }, 0);
}

function formFocusTextArea(formId, focusInputName) {
    window.setTimeout(function() {
        $("#" + formId + " textarea[name=" + focusInputName + "]").focus();
    }, 0);
}

/*******************************************************************************
 * 绑定指定表单元素类型的回车事件
 *
 * @param formId
 *            表单
 * @param element
 *            元素类型 (input/select/radio/...)
 * @param subFunction
 *            回车后要执行的js函数
 * @param eventName
 *            键盘事件 (keyup/keydown/keypress...)
 */
function bindEnter(formId, element, subFunction, eventName) {
    $("#" + formId + " " + element).bind(eventName, function(event) {
        //回车
        if (event.keyCode == '13') {
            subFunction();
        }
        //下拉
        // if (event.keyCode == '40') {
        //     $("#deviceName").combobox('showPanel');
        // }
    });
}

/*******************************************************************************
 * 绑定指定表单元素类型的ESC事件
 *
 * @param formId
 *            表单
 * @param element
 *            元素类型 (input/select/radio/...)
 * @param subFunction
 *            回车后要执行的js函数
 * @param eventName
 *            键盘事件 (keyup/keydown/keypress...)
 */
function bindEsc(formId, element, subFunction, eventName) {
    $("#" + formId + " " + element).bind(eventName, function(event) {
        //ESC
        if (event.keyCode == '27') {
            subFunction();
        }
    });
}

/*******************************************************************************
 * 表单回车事件绑定 表单光标定位
 *
 * @param formId
 * @param focusInputId
 * @param subFunction 回车要执行的函数
 * @param subFunction Esc要执行的函数
 *
 */
function bindFormEvent(formId, focusInputId, subFunction, escFunction) {
    if(subFunction && subFunction != null) {
        bindEnter(formId, 'input', subFunction, 'keyup');
        bindEnter(formId, 'select', subFunction, 'keyup');
    }
    if(escFunction && escFunction != null){
        bindEsc(formId, 'input', escFunction, 'keyup');
        bindEsc(formId, 'select', escFunction, 'keyup');
    }
    formFocus(formId, focusInputId);
}

/**
 * 单独实现一个combobox的keyHandler的回车(enter)事件函数，
 * 用于解决如果输入的值不是下拉中的值，按回车键会被清空问题
 * @param target
 */
function _doComboboxEnter(target){
    var t = $(target);
    var opts = t.combobox('options');
    var panel = t.combobox('panel');
    var item = panel.children('div.combobox-item-hover');
    if (item.length){
        item.removeClass('combobox-item-hover');
        var row = opts.finder.getRow(target, item);
        var value = row[opts.valueField];
        if (opts.multiple){
            if (item.hasClass('combobox-item-selected')){
                t.combobox('unselect', value);
            } else {
                t.combobox('select', value);
            }
        } else {
            t.combobox('select', value);
        }
    }
    if (!opts.multiple){
        t.combobox('hidePanel');
    }
}