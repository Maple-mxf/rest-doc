var responseoneline = "            <div class='layui-form-item one-response-body-line' id='first-response-field'>" +
    "                <div class='layui-inline'>" +
    "                    <div class='layui-input-inline'>" +
    "                        <input type='text'" +
    "                               name='responseFieldPath' placeholder='字段Path'" +
    "                               autocomplete='off'" +
    "                               class='layui-input'>" +
    "                    </div>" +
    "                </div>" +
    "            <div class='layui-inline'>" +
    "                <label>=</label>" +
    "            </div>" +
    "            <div class='layui-inline'>" +
    "                <div class='layui-input-inline'>" +
    "                    <input type='text'" +
    "                           name='requestFieldValue'" +
    "                           placeholder='字段值'" +
    "                           autocomplete='off'" +
    "                           class='layui-input'>" +
    "                </div>" +
    "            </div>" +
    "                <div class='layui-inline layui-hide'>" +
    "                    <div class='layui-input-inline'>" +
    "                        <select name='responseFieldType' lay-verify='type'>" +
    "                            <option value='NON' disabled='disabled'>请选择类型</option>" +
    "                            <option value='OBJECT' selected>Object</option>" +
    "                            <option value='ARRAY'>Array</option>" +
    "                            <option value='BOOLEAN'>Boolean</option>" +
    "                            <option value='NUMBER'>Number</option>" +
    "                            <option value='STRING'>String</option>" +
    "                        </select>" +
    "                    </div>" +
    "                </div>" +
    "                <div class='layui-inline layui-hide' style='width: 5%'>" +
    "                    <div class='layui-input-inline'>" +
    "                        <input type='checkbox' checked" +
    "                               name='responseFieldConstraint' title='必选'>" +
    "                    </div>" +
    "                </div>" +
    "                <div class='layui-inline' style='margin-left: 40px'>" +
    "                    <button title='删除当前字段' type='button' class='layui-btn layui-btn-sm layui-btn-primary' onclick='deleteResponseBodyBtn(this)'>" +
    "                        <i class='layui-icon layui-icon-subtraction' style='font-size: 30px; color: black;'></i></button>" +
    "                </div>" +
    "            </div>";