var oneline = "        <div class=\"layui-form-item one-line multi-request-body\" id=\"first-request-body-field\">\n" +
    "            <div class=\"layui-inline\">\n" +
    "                <div class=\"layui-input-inline\">\n" +
    "                    <input type=\"text\"\n" +
    "                           name=\"requestFieldPath\"\n" +
    "                           placeholder=\"字段Path\"\n" +
    "                           autocomplete=\"off\"\n" +
    "                           class=\"layui-input\">\n" +
    "                </div>\n" +
    "            </div>\n" +
    "            <div class=\"layui-inline\">\n" +
    "                <label>=</label>\n" +
    "            </div>\n" +
    "\n" +
    "            <div class=\"layui-inline\">\n" +
    "                <div class=\"layui-input-inline\">\n" +
    "                    <input type=\"text\"\n" +
    "                           name=\"requestFieldValue\"\n" +
    "                           placeholder=\"字段值\"\n" +
    "                           autocomplete=\"off\"\n" +
    "                           class=\"layui-input\">\n" +
    "                </div>\n" +
    "            </div>\n" +
    "            <div class=\"layui-inline\">\n" +
    "                <div class=\"layui-input-inline\">\n" +
    "                    <select name=\"requestFieldType\" lay-verify=\"type\">\n" +
    "                        <option value=\"NON\" disabled=\"disabled\">请选择类型</option>\n" +
    "                        <option value=\"OBJECT\" selected>Object</option>\n" +
    "                        <option value=\"ARRAY\">Array</option>\n" +
    "                        <option value=\"BOOLEAN\">Boolean</option>\n" +
    "                        <option value=\"NUMBER\">Number</option>\n" +
    "                        <option value=\"STRING\">String</option>\n" +
    "                    </select>\n" +
    "                </div>\n" +
    "            </div>\n" +
    "            <div class=\"layui-inline\">\n" +
    "                <label class=\"layui-form-label\">说明</label>\n" +
    "                <div class=\"layui-input-inline\">\n" +
    "                    <input type=\"text\"\n" +
    "                           name=\"requestFieldDescription\"\n" +
    "                           placeholder=\"字段说明\"\n" +
    "                           autocomplete=\"off\"\n" +
    "                           class=\"layui-input\">\n" +
    "                </div>\n" +
    "            </div>\n" +
    "            <div class=\"layui-inline\" style=\"width: 5%\">\n" +
    "                <div class=\"layui-input-inline\">\n" +
    "                    <input type=\"checkbox\" checked\n" +
    "                           name=\"requestFieldConstraint\" title=\"必选\">\n" +
    "                </div>\n" +
    "            </div>\n" +
    "            <div class=\"layui-inline\">\n" +
    "                <button type=\"button\" class=\"layui-btn layui-btn-sm layui-btn-primary\" style=\"margin-left: 10px\"\n" +
    "                        onclick=\"deleteRequestBodyBtn(this)\">\n" +
    "                    <i class=\"layui-icon layui-icon-subtraction\" style=\"font-size: 30px; color: black;\"></i>\n" +
    "                </button>\n" +
    "            </div>\n" +
    "        </div>";