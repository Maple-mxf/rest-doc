var responseoneline = "            <div class=\"layui-form-item one-response-body-line\" id=\"first-response-field\">\n" +
    "                <div class=\"layui-inline\">\n" +
    "                    <label class=\"layui-form-label\">字段Path</label>\n" +
    "                    <div class=\"layui-input-inline\">\n" +
    "                        <input type=\"text\"\n" +
    "                               name=\"responseFieldPath\"\n" +
    "                               autocomplete=\"off\"\n" +
    "                               class=\"layui-input\">\n" +
    "                    </div>\n" +
    "                </div>\n" +
    "                <div class=\"layui-inline\">\n" +
    "                    <div class=\"layui-input-inline\">\n" +
    "                        <select name=\"responseFieldType\" lay-verify=\"type\">\n" +
    "                            <option value=\"NON\" disabled=\"disabled\">请选择类型</option>\n" +
    "                            <option value=\"OBJECT\" selected>Object</option>\n" +
    "                            <option value=\"ARRAY\">Array</option>\n" +
    "                            <option value=\"BOOLEAN\">Boolean</option>\n" +
    "                            <option value=\"NUMBER\">Number</option>\n" +
    "                            <option value=\"STRING\">String</option>\n" +
    "                        </select>\n" +
    "                    </div>\n" +
    "                </div>\n" +
    "<textarea name='responseFieldDescription' style='display: none' cols='30' rows='10'></textarea>" +
    "                <div class=\"layui-inline\" style=\"width: 5%\">\n" +
    "                    <div class=\"layui-input-inline\">\n" +
    "                        <input type=\"checkbox\" checked\n" +
    "                               name=\"responseFieldConstraint\" title=\"必选\">\n" +
    "                    </div>\n" +
    "                </div>\n" +
    "                <div class=\"layui-inline\" style=\"margin-left: 10px\">\n" +
    "<button title='点击显示字段备注' type=\"button\" class=\"layui-btn layui-btn-sm layui-btn-primary\" id=\"deleteHeaderBtn\"\n" +
    "                            style=\"margin-left: 10px\"\n" +
    "                            onclick=\"addResponseParamDescBtn(this)\">\n" +
    "                        <i class=\"layui-icon layui-icon-tips\" style=\"font-size: 30px; color: black;\"></i></button>" +
    "                    <button title='删除当前字段' type=\"button\" class=\"layui-btn layui-btn-sm layui-btn-primary\" onclick=\"deleteResponseBodyBtn(this)\">\n" +
    "                        <i class=\"layui-icon layui-icon-subtraction\" style=\"font-size: 30px; color: black;\"></i>\n</button>" +
    "                </div>\n" +
    "            </div>";