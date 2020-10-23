var oneheaderline = "\n" +
    "            <div class=\"layui-form-item one-request-header-line\" id=\"first-header-field\">\n" +
    "                <div class=\"layui-inline\">\n" +
    "<!--                    <label class=\"layui-form-label\">请求头</label>-->\n" +
    "                    <div class=\"layui-input-inline\">\n" +
    "                        <input type=\"text\" name=\"headerKey\" placeholder=\"Key\" autocomplete=\"off\"\n" +
    "                               value=\"Content-Type\" class=\"layui-input\">\n" +
    "                    </div>\n" +
    "                </div>\n" +
    "\n" +
    "                <div class=\"layui-inline\">\n" +
    "                    <label>=</label>\n" +
    "                </div>\n" +
    "                <div class=\"layui-inline\">\n" +
    "<!--                    <label class=\"layui-form-label\">值</label>-->\n" +
    "                    <div class=\"layui-input-inline\">\n" +
    "                        <input type=\"text\" name=\"headerValue\" placeholder=\"Value\" autocomplete=\"off\"\n" +
    "                               value=\"application/json;utf-8\"\n" +
    "                               class=\"layui-input\">\n" +
    "                    </div>\n" +
    "                </div>\n" +
    "                <div class=\"layui-inline\">\n" +
    "                    <div class=\"layui-input-inline\" style=\"width: 5%\">\n" +
    "                        <input type=\"checkbox\" checked name=\"headerConstraint\" title=\"必选\">\n" +
    "                    </div>\n" +
    "                </div>\n" +
    "\n" +
    "                <div class=\"layui-inline\">\n" +
    "                    <button title='删除当前字段' type=\"button\" class=\"layui-btn layui-btn-sm layui-btn-primary\" id=\"deleteHeaderBtn\"\n" +
    "                            style=\"margin-left: 10px\"\n" +
    "                            onclick=\"deleteHeaderFieldLine(this)\">\n" +
    "                        <i class=\"layui-icon layui-icon-subtraction\" style=\"font-size: 30px; color: black;\"></i></button>" +
    "                </div>\n" +
    "            </div>\n" +
    "\n" +
    "        ";