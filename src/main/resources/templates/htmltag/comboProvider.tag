<script type="text/javascript">
    <% if(has(_data)){ %>
    $("#${_id}").combobox({
        data:${_data}
        ,valueField:'${_valueField!"value"}'
        ,textField:'${_textField!"text"}'
    })
    <% }else{ %>
    var _comboProviderParamObj = ${_queryParams!"{\}" };
    _comboProviderParamObj.valueField = '${_valueField!"value"}';
    _comboProviderParamObj.textField = '${_textField!"text"}';
    _comboProviderParamObj.table = '${_table}';
    _comboProviderParamObj.provider = '${_provider!"simpleValueProvider"}';
    $("#${_id}").combobox({
        url:"${contextPath}/provider/getLookupList"
        ,method:"POST"
        ,valueField:"value"
        ,textField:"text"
        ,queryParams:_comboProviderParamObj
    })
    <% } %>
</script>