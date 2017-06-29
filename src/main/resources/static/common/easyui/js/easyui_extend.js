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

//绑定实体的metadata信息，用于提供者转换
function bindMetadata(gridId){
    var opts=$("#"+gridId).datagrid("options");
    var params = {}||opts.queryParams;
    var lastColumns = opts.columns[opts.columns.length-1];
    params["metadata"] = {};
    for(var column in lastColumns){
        var _provider = lastColumns[column]["_provider"];
        var _data = lastColumns[column]["_data"];
        if(_data != null){
            var field = lastColumns[column]["field"];
            var fieldMetadata = {};
            fieldMetadata["provider"] = "simpleDataProvider";
            fieldMetadata["data"] = _data;
            params["metadata"][field] = JSON.stringify(fieldMetadata);
            continue;
        }
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

bindTreegridMetadata

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
                text:row.text
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
                var child = {id:row.id,text:row.text};
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