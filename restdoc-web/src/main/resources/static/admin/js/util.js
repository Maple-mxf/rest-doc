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

function initTestApiDoc(document, form, one_uri_line, one_request_header_line,
                        one_request_param_line, one_response_param_line
) {

    if (document != null) {

        // 设定method
        $("#method").find("option[value=" + document['method'] + "]")
            .attr("selected", true);

        // 设定api地址
        $('#url').val(document['url']);

        // 设定api名称
        $('#apiName').val(document['name']);

        // 设定描述
        $('#apiDescription').text(document['description']);

        if (document['uriVarDescriptors'] != null && document['uriVarDescriptors'].length > 0) {
            initUriFieldDoc(document['uriVarDescriptors'], one_uri_line);
            form.render()
        }

        if (document['requestHeaderDescriptor'] != null && document['requestHeaderDescriptor'].length > 0) {
            initHeaderFieldDoc(document['requestHeaderDescriptor'], one_request_header_line);
            form.render()
        }

        if (document['requestBodyDescriptor'] != null && document['requestBodyDescriptor'].length > 0) {
            initRequestParamDoc(document['requestBodyDescriptor'], one_request_param_line);
            form.render()
        }

        if (document['responseBodyDescriptors'] != null && document['responseBodyDescriptors'].length > 0) {
            initResponseParamDoc(document['responseBodyDescriptors'], one_response_param_line);
            form.render()
        }
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


function initHeaderFieldDoc(headerFields, one_request_header_line) {
    for (let i = 0; i < headerFields.length; i++) {
        $("#header-fieldset").append(one_request_header_line);
    }

    var all_input_line = $("#header-fieldset").children(".one-request-header-line");

    for (let i = 0; i < all_input_line.length; i++) {
        let line = all_input_line[i];
        $(line).find("input").each(function () {
            if (this.name === 'headerKey') {
                this.value = headerFields[i]['field']
            } else if (this.name === 'headerValue') {
                this.value = headerFields[i]['value']
            }
        });
        $(line).find("textarea:first-child").text(headerFields[i]['description'])
    }

    $("#header-fieldset").addClass("layui-show");
}


function initUriFieldDoc(uriVariables, oneuriline) {
    for (let i = 0; i < uriVariables.length; i++) {
        $("#uri-fieldset").append(oneuriline);
    }
    var all_input_line = $("#uri-fieldset").children(".one-uri-header-line");

    for (let i = 0; i < all_input_line.length; i++) {
        let line = all_input_line[i];

        console.info($(line).find("input").length);

        $(line).find("input").each(function () {
            if (this.name === 'uriField') {
                this.value = uriVariables[i]['field']
            } else if (this.name === 'uriValue') {
                this.value = uriVariables[i]['value']
            }
        });

        $(line).find("textarea:first-child").html(uriVariables[i]['description']);

        $("#uri-fieldset").addClass("layui-show");
    }
}