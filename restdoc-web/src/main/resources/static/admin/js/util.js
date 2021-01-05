function formatTimestamp(timestamp) {
    var dateObj = new Date(timestamp);

    var year = dateObj.getYear() + 1900;
    var month = dateObj.getMonth() + 1;
    var theDate = dateObj.getDate();
    var hour = dateObj.getHours();
    var minute = dateObj.getMinutes();
    var second = dateObj.getSeconds();
    return year + "-" + month + "-" + theDate + " " + hour + ":" + minute + ":" + second;
}

function formatXml(xml) {
    var formatted = '';
    var reg = /(>)(<)(\/*)/g;
    xml = xml.replace(reg, '$1\r\n$2$3');
    var pad = 0;
    jQuery.each(xml.split('\r\n'), function (index, node) {
        var indent = 0;
        if (node.match(/.+<\/\w[^>]*>$/)) {
            indent = 0;
        } else if (node.match(/^<\/\w/)) {
            if (pad != 0) {
                pad -= 1;
            }
        } else if (node.match(/^<\w[^>]*[^\/]>.*$/)) {
            indent = 1;
        } else {
            indent = 0;
        }
        var padding = '';
        for (var i = 0; i < pad; i++) {
            padding += '  ';
        }
        formatted += padding + node + '\r\n';
        pad += indent;
    });

    return formatted;
}

function formatJson(ugly) {
    if (ugly !== '') {
        try {
            var obj = JSON.parse(ugly);
            return JSON.stringify(obj, undefined, 4);
        } catch (e) {
            alert("JSON字符串错误，请检查");
            return ugly;
        }
    }
}

function initBaseInput(doc) {



    // 设定api地址
    $('#url').val(doc['url']);

    // 设定api名称
    $('#apiName').val(doc['name']);

    // 检测URL
    $.ajax({
        method: "POST",
        url: "/restdoc/httpstandard/helper/uri/var/extract",
        data: JSON.stringify({url: doc['url']}),
        contentType: 'application/json',
        success: function (res) {
            if (res.code === '200') {
                if (Object.keys(res.data).length > 0) {
                    initUriFieldDoc(res.data)
                }
            }
        }
    });
}

function projectToJson(data) {
    var json = {};
    $.ajax({
        method: "POST",
        url: "/restdoc/textprotocol/serialize2Json",
        contentType: 'application/json',
        data: data,
        async: false,
        success: function (res) {
            if (res.code === '200') {
                json = res.data;
            } else {
                alert(res.message);
            }
        }
    });
    return json;
}

function projectToXml(data) {
    var xml = "";
    $.ajax({
        method: "POST",
        url: "/restdoc/textprotocol/serialize2Xml",
        contentType: 'application/json',
        async: false,
        data: data,
        success: function (res) {
            if (res.code === '200') {
                xml = res.data;
            } else {
                alert(res.message);
            }
        }
    });
    return xml;
}

function initTestApiDoc(testLog, doc, form) {

    if (testLog != null) {

        // 设定method
        $("#method").find("option[value=" + doc['method'] + "]").attr("selected", true);

        // 设定api地址
        $('#url').val(doc['url']);

        // 设定api名称
        $('#apiName').val(doc['name']);

        if (testLog['uriParameters'] != null) {
            initUriFieldDoc(testLog['uriParameters']);
        }

        if (testLog['requestHeaderParameters'] != null) {
            initRequestHeaderFieldDoc(testLog['requestHeaderParameters']);
        }

        if (testLog['responseHeaderParameters'] != null) {
            initResponseHeaderField(testLog['responseHeaderParameters']);
        }

        if (testLog['requestBodyParameters'] != null) {
            initRequestBody(testLog['requestBodyParameters']);
        }

        if (testLog['responseBodyParameters'] != null) {
            initResponseBody(testLog['responseBodyParameters'])
        }
        form.render()
    }
}

function initRequestBody(requestBodyParameters) {
    var jsonText = formatJson(projectToJson(JSON.stringify(requestBodyParameters)));
    var xmlText = formatXml(projectToXml(JSON.stringify(requestBodyParameters)));

    $("#request_body_json_text").val(jsonText);
    $("#request_body_xml_text").val(xmlText);

    $("#body-fieldset").addClass("layui-show");
}

function initResponseBody(responseBodyParameters) {
    var jsonText = formatJson(projectToJson(JSON.stringify(responseBodyParameters)));
    var xmlText = formatXml(projectToXml(JSON.stringify(responseBodyParameters)));

    $("#response_body_json_text").val(jsonText);
    $("#response_body_xml_text").val(xmlText);

    $("#response-fieldset").addClass("layui-show");
}

function initRequestHeaderFieldDoc(headerFields) {
    var keys = Object.keys(headerFields);
    for (let i = 0; i < keys.length; i++) {
        $("#header-fieldset").append(getNewHeaderLine());
    }
    var all_input_line = $("#header-fieldset").children(".one-request-header-line");
    for (let i = 0; i < all_input_line.length; i++) {
        let line = all_input_line[i];
        $(line).find("input")
            .each(function () {
                if (this.name === 'headerKey') {
                    this.value = keys[i];
                } else if (this.name === 'headerValue') {
                    this.value = headerFields[keys[i]];
                }
            });
    }
    if (keys.length > 0) {
        $("#header-fieldset").addClass("layui-show");
    }
}

function initResponseHeaderField(headerFields) {
    var keys = Object.keys(headerFields);
    console.info(headerFields);
    for (let i = 0; i < keys.length; i++) {
        $("#response-header-field-fieldset").append(one_response_header_line);
    }
    var all_input_line = $("#response-header-field-fieldset").children(".one_response_header_line");
    for (let i = 0; i < all_input_line.length; i++) {
        let line = all_input_line[i];
        $(line).find("input")
            .each(function () {
                if (this.name === 'responseHeaderField') {
                    this.value = keys[i];
                } else if (this.name === 'responseHeaderValue') {
                    this.value = headerFields[keys[i]];
                }
            });
    }
    if (keys.length > 0) {
        $("#response-header-field-fieldset").addClass("layui-show");
    }
}

function initUriFieldDoc(uriVariables) {
    var keys = Object.keys(uriVariables);
    for (let i = 0; i < keys.length; i++) {
        $("#uri-fieldset").append(oneuriline);
    }
    var all_input_line = $("#uri-fieldset").children(".one-uri-header-line");

    for (let i = 0; i < all_input_line.length; i++) {
        let line = all_input_line[i];
        $(line).find("input")
            .each(function () {
                if (this.name === 'uriField') {
                    this.value = keys[i];
                } else if (this.name === 'uriValue') {
                    this.value = uriVariables[keys[i]];
                }
            });
    }

    if (keys.length > 0) {
        $("#uri-fieldset").addClass("layui-show");
    }
}