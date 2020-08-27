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
    "\n" +
    "                <div class=\"layui-inline\">\n" +
    "                    <label class=\"layui-form-label\">说明</label>\n" +
    "                    <div class=\"layui-input-inline\">\n" +
    "                        <input type=\"text\"\n" +
    "                               name=\"responseFieldDescription\"\n" +
    "                               autocomplete=\"off\"\n" +
    "                               class=\"layui-input\">\n" +
    "                    </div>\n" +
    "                </div>\n" +
    "                <div class=\"layui-inline\" style=\"width: 5%\">\n" +
    "                    <div class=\"layui-input-inline\">\n" +
    "                        <input type=\"checkbox\" checked\n" +
    "                               name=\"responseFieldConstraint\" title=\"必选\">\n" +
    "                    </div>\n" +
    "                </div>\n" +
    "                <div class=\"layui-inline\" style=\"margin-left: 10px\">\n" +
    "                    <button type=\"button\" class=\"layui-btn layui-btn-danger\" onclick=\"deleteResponseBodyBtn(this)\">\n" +
    "                        <i class=\"layui-icon layui-icon-subtraction\" style=\"font-size: 30px; color: white;\"></i>\n" +
    "                    </button>\n" +
    "                </div>\n" +
    "            </div>";