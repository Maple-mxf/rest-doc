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

function initBaseInput(doc) {
    // 设定method
    $("#method").find("option[value=" + doc['method'] + "]").attr("selected", true);

    // 设定api地址
    $('#url').val(doc['url']);

    // 设定api名称
    $('#apiName').val(doc['name']);
}

function initTestApiDoc(testLog, doc, form, one_uri_line, one_request_header_line,
                        one_request_param_line, one_response_param_line
) {

    if (testLog != null) {

        initBaseInput(doc);

        if (testLog['uriParameters'] != null) {
            initUriFieldDoc(testLog['uriParameters'], one_uri_line);
        }

        if (testLog['requestHeaderParameters'] != null) {
            initRequestHeaderFieldDoc(testLog['requestHeaderParameters'], one_request_header_line);
        }

        if (testLog['requestBodyParameters'] != null) {
            initRequestParamDoc(testLog['requestBodyParameters'], one_request_param_line);
        }

        if (testLog['responseBodyParameters'] != null && testLog['responseBodyParameters'].length > 0) {
            initResponseParamDoc(testLog['responseBodyParameters'], one_response_param_line);
        }
        form.render()
    }
}

function initResponseParamDoc(responseFields, one_response_param_line) {
    for (let i = 0; i < responseFields.length; i++) {
        $("#response-fieldset").append(one_response_param_line);
    }
    var all_input_line = $("#response-fieldset").children(".one-response-body-line");
    for (let i = 0; i < all_input_line.length; i++) {
        let line = all_input_line[i];


        $(line).find("input").each(function () {
            if (this.name === 'responseFieldPath') {
                this.value = responseFields[i]['path']
            }
        });

        $(line).find("textarea").text(responseFields[i]['description']);

        //
        $($(line).find("select:first-child"))
            .find("option[value=" + responseFields[i]['type'] + "]")
            .attr("selected", true);
    }

    $("#response-fieldset").addClass("layui-show");
}

function initRequestParamDoc(requestFields, one_request_param_line) {
    for (let i = 0; i < requestFields.length; i++) {
        $("#body-fieldset").append(one_request_param_line);
    }

    var all_input_line = $("#body-fieldset").children(".one-line");

    for (let i = 0; i < all_input_line.length; i++) {
        let line = all_input_line[i];
        $(line).find("input").each(function () {
            if (this.name === 'requestFieldPath') {
                this.value = requestFields[i]['path']
            } else if (this.name === 'requestFieldValue') {
                this.value = requestFields[i]['value']
            }
        });
        $(line).find("textarea").text(requestFields[i]['description'])
        $($(line).find("select:first-child"))
            .find("option[value=" + requestFields[i]['type'] + "]")
            .attr("selected", true);
    }

    $("#body-fieldset").addClass("layui-show");
}


function initRequestHeaderFieldDoc(headerFields, one_request_header_line) {
    var keys = Object.keys(headerFields);
    for (let i = 0; i < keys.length; i++) {
        $("#header-fieldset").append(one_request_header_line);
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
    $("#header-fieldset").addClass("layui-show");
}

function initUriFieldDoc(uriVariables, oneuriline) {
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
        $("#uri-fieldset").addClass("layui-show");
    }
}