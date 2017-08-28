<script type="text/javascript">
    <% if(has(_data)){ %>
    $("#${_id}").combobox({
        data:${_data}
        ,editable : false
        ,valueField:'${_valueField!"value"}'
        ,textField:'${_textField!"text"}'
    })
    <% }else{ %>
    var _comboProviderParamObj_${_id} = {};
    _comboProviderParamObj_${_id}.queryParams = '${_queryParams!"{\}" }';
    _comboProviderParamObj_${_id}.valueField = '${_valueField!"value"}';
    _comboProviderParamObj_${_id}.textField = '${_textField!"text"}';
    _comboProviderParamObj_${_id}.table = '${_table!""}';
    _comboProviderParamObj_${_id}.provider = '${_provider!"simpleValueProvider"}';
    //注意，这里只能取到value属性中的值，而无法取到combobox的当前值，因为还没有渲染，渲染以后应该使用getValue方法取值
    _comboProviderParamObj_${_id}.value = $("#${_id}").val();
    $("#${_id}").combobox({
        url:"${contextPath}/provider/getLookupList"
        ,method:"POST"
        ,valueField:"value"
        ,textField:"text"
        ,queryParams:_comboProviderParamObj_${_id}
    })
    <% } %>
</script>