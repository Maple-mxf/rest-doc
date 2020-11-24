function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    var uuid = s.join("");
    return uuid;
}

var snippet1 = "<div class='layui-form-item one-request-header-line' ><div class='layui-inline'><div class='layui-input-inline'><input type='text' name='headerKey' placeholder='Key' autocomplete='off' class='layui-input'";
var snippet2 = "</div></div><div class='layui-inline'> <label>=</label> </div> <div class='layui-inline'> <div class='layui-input-inline'> <input type='text' name='headerValue' placeholder='Value' autocomplete='off'  class='layui-input'";
var snippet3 = "</div></div><div class='layui-inline'><div class='layui-input-inline layui-hide' style='width: 5%'><input type='checkbox'  checked name='headerConstraint' title='必选'> </div></div><div class='layui-inline'><button title='删除当前字段' type='button' class='layui-btn layui-btn-sm layui-btn-primary' id='deleteHeaderBtn' style='margin-left: 10px' onclick='deleteHeaderFieldLine(this)'><i class='layui-icon layui-icon-subtraction' style='font-size: 30px; color: black;'></i></button></div></div>";


function getNewHeaderLine() {

    var keyInputId = uuid();
    var valueInputId = uuid() + "-value";

    var keyInputIdParam = "'" + keyInputId + "'";
    var keyOninputFun = "tipHeaderKey(" + keyInputIdParam + ")";
    var keyOnfocusFun = "tipHeaderKey(" + keyInputIdParam + ")";

    var valueInputIdParam = "'" + valueInputId + "'";
    var valueOninputFun = "tipHeaderValue(" + keyInputIdParam + "," + valueInputIdParam + ")";
    var valueOnfocusFun = "tipHeaderValue(" + keyInputIdParam + "," + valueInputIdParam + ")";

    return snippet1 + " id='" + keyInputId + "' onfocus=\"" + keyOnfocusFun + "\"  oninput=\"" + keyOninputFun + "\">"
        + snippet2 + "id='" + valueInputId + "'  onfocus=\"" + valueOnfocusFun + "\"  oninput=\"" + valueOninputFun + "\">"
        + snippet3;
}



