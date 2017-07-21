<script type="text/javascript">
    <% if(has(_data)){ %>
    $("#${_id}").combobox({
        data:${_data}
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
    $("#${_id}").combobox({
        url:"${contextPath}/provider/getLookupList"
        ,method:"POST"
        ,valueField:"value"
        ,textField:"text"
        ,queryParams:_comboProviderParamObj_${_id}
    })
    <% } %>
</script>