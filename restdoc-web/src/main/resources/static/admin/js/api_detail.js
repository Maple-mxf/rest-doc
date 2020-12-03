var queryTableIsEditState = false, uriTableIsEditState = false, requestHeaderTableIsEditState = false,
    requestBodyTableIsEditState = false, responseBodyTableIsEditState = false;

// 保存URI数据
$('#saveURIVarTableBtn').click(function () {
    var trLines = $('#uriFieldListTableBody').children('tr');
    var array = [];
    for (var i = 0; i < trLines.length; i++) {
        var field = $(trLines[i]).find("input[name=field]").val();
        var value = $(trLines[i]).find("input[name=value]").val();
        var description = $(trLines[i]).find("input[name=description]").val();
        var map = {
            field: field,
            value: value,
            description: description
        };
        array.push(map)
    }
    $.ajax({
        url: '/restdoc/document/uridescriptor',
        method: 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify({
            documentId: documentId,
            values: array
        }),
        success: function (res) {
            if (res.code === '200') {
                lay.msg('保存成功');
                resetURITableState();
                renderURITable(res.data)
            } else {
                lay.msg(res.message);
            }
        }
    });
});

$("#editURIVarTableBtn").click(function () {
    if (uriTableIsEditState) {
        resetURITableState();
        uriTableIsEditState = false;
    } else {
        $('#saveURIVarTableBtn,#cancelURIVarTableBtn').css("display", 'block');
        renderURIVarEditStateTable(doc['uriVarDescriptors']);
        uriTableIsEditState = true;
        $('#editURIVarTableBtn').addClass('edit-state');
    }
});

$('#saveRequestBodyTableBtn').click(function () {
    var trLines = $('#requestBodyFieldListTableBody').children('tr');
    var array = [];
    for (var i = 0; i < trLines.length; i++) {
        var path = $(trLines[i]).find("input[name=path]").val();
        var value = $(trLines[i]).find("input[name=value]").val();
        var optional = $(trLines[i]).find("select[name=optional]").val();
        var type = $(trLines[i]).find("select[name=type]").val();
        var description = $(trLines[i]).find("input[name=description]").val();
        var map = {
            path: path,
            value: value,
            description: description,
            optional: optional,
            type: type
        };
        array.push(map)
    }
    $.ajax({
        url: '/restdoc/document/requestbodydescriptor',
        method: 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify({
            documentId: documentId,
            values: array
        }),
        success: function (res) {
            if (res.code === '200') {
                lay.msg('保存成功');
                resetRequestBodyTable();
            } else {
                lay.msg(res.message);
            }
        }
    });
});

function renderURIVarEditStateTable(uriFields) {
    if (uriFields !== null && uriFields.length > 0) {
        var allLine = '';
        for (var i = 0; i < uriFields.length; i++) {
            var start = "<tr>", end = "</tr>";
            var line =
                '<td><input name="field" class="layui-input" value="' + uriFields[i]['field'] + '"> </td>' +
                '<td><input name="value" class="layui-input" value="' + uriFields[i]['value'] + '"></td>' +
                '<td> <input name="description" class="layui-input" value="' + uriFields[i]['description'] + '"> </td>';

            allLine = allLine + start + line + end;
        }

        $("#uriFieldListTableBody").html(allLine);
        gform.render();
    }
}

$('#editQueryParamTableBtn').click(function () {
    if (queryTableIsEditState) {
        resetQueryParamTable();
        queryTableIsEditState = false;
    } else {
        renderQueryParamEditStateTable(doc['queryParamDescriptors']);
        $('#saveQueryParamTableBtn,#cancelQueryParamTableBtn').css("display", 'block');
        queryTableIsEditState = true;
        $('#editQueryParamTableBtn').addClass('edit-state');
    }
});

$('#cancelQueryParamTableBtn').click(function () {
    resetQueryParamTable();
});

function renderQueryParamEditStateTable(queryParamFields) {
    if (queryParamFields !== null && queryParamFields.length > 0) {
        var allLine = '';
        for (var i = 0; i < queryParamFields.length; i++) {
            var start = "<tr>", end = "</tr>";
            var line =
                '<td><input name="field" class="layui-input" value="' + queryParamFields[i]['field'] + '"> </td>' +
                '<td><input name="value" class="layui-input" value="' + queryParamFields[i]['value'] + '"></td>' +
                '<td> <input name="description" class="layui-input" value="' + queryParamFields[i]['description'] + '"> </td>';

            allLine = allLine + start + line + end;
        }

        $("#queryParamListTableBody").html(allLine);
        gform.render();
    }
}

function resetQueryParamTable() {
    $('#saveQueryParamTableBtn,#cancelQueryParamTableBtn').css('display', 'none');
    $('#editQueryParamTableBtn').removeClass('edit-state');
    doc = getLastedDocument();
    renderQueryParamTable(doc['queryParamDescriptors']);
}

$('#saveQueryParamTableBtn').click(function () {
    var trLines = $('#queryParamListTableBody').children('tr');
    var array = [];
    for (var i = 0; i < trLines.length; i++) {
        var field = $(trLines[i]).find("input[name=field]").val();
        var value = $(trLines[i]).find("input[name=value]").val();
        var description = $(trLines[i]).find("input[name=description]").val();
        var map = {
            field: field,
            value: value,
            description: description
        };
        array.push(map)
    }
    $.ajax({
        url: '/restdoc/document/queryparamdescriptor',
        method: 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify({
            documentId: documentId,
            values: array
        }),
        success: function (res) {
            if (res.code === '200') {
                lay.msg('保存成功');
                resetQueryParamTable();
                renderQueryParamTable(res.data)
            } else {
                lay.msg(res.message);
            }
        }
    });
});

function resetURITableState() {
    // 重置按钮
    $('#saveURIVarTableBtn,#cancelURIVarTableBtn').css("display", 'none');
    $('#editURIVarTableBtn').removeClass('edit-state');
    doc = getLastedDocument();
    renderURITable(doc['uriVarDescriptors']);
}

// 过时
$('#cancelURIVarTableBtn').click(function () {
    resetURITableState();
});

function getLastedDocument() {
    var doc = null;
    $.ajax({
        method: 'get',
        url: '/restdoc/document/' + documentId,
        async: false,
        success: function (res) {
            if (res.code === '200') {
                doc = res.data
            }
        }
    });
    return doc;
}

$("#editRequestBodyTableBtn").click(function () {

    if (requestBodyTableIsEditState) {
        requestBodyTableIsEditState = false;
        resetRequestBodyTable();
    } else {

        $('#saveRequestBodyTableBtn,#cancelRequestBodyTableBtn').css('display', 'block');
        renderRequestBodyTableOnEditState(doc['requestBodyDescriptor']);
        $('#editRequestBodyTableBtn').addClass('edit-state');
        requestBodyTableIsEditState = true;
    }

});

function renderRequestBodyTableOnEditState(requestBodyFields) {
    if (requestBodyFields !== null && requestBodyFields.length > 0) {
        var allLine = '';
        for (var i = 0; i < requestBodyFields.length; i++) {
            var start = "<tr>", end = "</tr>";
            var line =
                '<td><input name="path" class="layui-input" value="' + requestBodyFields[i]['path'] + '"> </td>' +
                '<td>' +
                ' <div class="layui-form-item"><select name="type">' +
                '<option value="STRING" ' + (requestBodyFields[i]['type'] === "STRING" ? "selected" : "") + '>string</option>' +
                '<option value="OBJECT" ' + (requestBodyFields[i]['type'] === "OBJECT" ? "selected" : "") + '>object</option>' +
                '<option value="NUMBER" ' + (requestBodyFields[i]['type'] === "NUMBER" ? "selected" : "") + '>number</option>' +
                '<option value="BOOLEAN" ' + (requestBodyFields[i]['type'] === "BOOLEAN" ? "selected" : "") + '>boolean</option>' +
                '<option value="ARRAY" ' + (requestBodyFields[i]['type'] === "ARRAY" ? "selected" : "") + '>array</option>' +
                '</select></div></td>' +

                '<td><input name="value" class="layui-input" value="' + requestBodyFields[i]['value'] + '"></td>' +

                '<td>' +
                ' <div class="layui-form-item"><select name="optional">' +
                '<option value="true" ' + (requestBodyFields[i]['optional'] === true ? 'selected' : '') + '>是</option>' +
                '<option value="false" ' + (requestBodyFields[i]['optional'] === false ? 'selected' : '') + '>否</option>' +
                '</select></div></td>' +

                '<td> <input name="description" class="layui-input" value="' + requestBodyFields[i]['description'] + '"> </td>';

            allLine = allLine + start + line + end;
        }

        $("#requestBodyFieldListTableBody").html(allLine);
        gform.render();
    }
}


$("#editRequestHeaderTableBtn").click(function () {

    if (requestHeaderTableIsEditState) {
        resetRequestHeaderTable();
        requestHeaderTableIsEditState = false;
    } else {
        renderRequestHeaderOnEditState(doc['requestHeaderDescriptor']);
        $('#saveRequestHeaderTableBtn,#cancelRequestHeaderTableBtn').css("display", 'block');
        $('#editRequestHeaderTableBtn').addClass('edit-state');
        requestHeaderTableIsEditState = true;
    }

});

function renderRequestHeaderOnEditState(requestHeaderFields) {
    if (requestHeaderFields !== null && requestHeaderFields.length > 0) {
        var allLine = '';
        for (var i = 0; i < requestHeaderFields.length; i++) {
            var start = "<tr>", end = "</tr>";
            var line =
                '<td><input name="field" class="layui-input" value="' + requestHeaderFields[i]['field'] + '"> </td>' +
                '<td><input name="value" class="layui-input" value="' + requestHeaderFields[i]['value'] + '"></td>' +
                '<td>  <div class="layui-form-item"><select name="optional">' +
                '<option value="true" ' + (requestHeaderFields[i]['optional'] === true ? 'selected' : '') + '>是</option>' +
                '<option value="false" ' + (requestHeaderFields[i]['optional'] === false ? 'selected' : '') + '>否</option>' +
                '</select></div></td>' +

                '<td> <input name="description" class="layui-input" value="' + requestHeaderFields[i]['description'] + '"> </td>';

            allLine = allLine + start + line + end;
        }

        $("#requestHeaderListTableBody").html(allLine);
        gform.render();
    }
}

$('#cancelRequestBodyTableBtn').click(function () {
    resetRequestBodyTable();

    doc = getLastedDocument();
    renderRequestBodyTable(doc['requestBodyDescriptor']);
});

function resetRequestBodyTable() {
    $('#saveRequestBodyTableBtn,#cancelRequestBodyTableBtn').css("display", 'none');

    doc = getLastedDocument();
    renderRequestBodyTable(doc['requestBodyDescriptor']);
    $('#editRequestBodyTableBtn').removeClass('edit-state');
}


function resetRequestHeaderTable() {
    $('#saveRequestHeaderTableBtn,#cancelRequestHeaderTableBtn').css("display", 'none');
    doc = getLastedDocument();
    renderRequestHeaderTable(doc['requestHeaderDescriptor']);
    $('#editRequestHeaderTableBtn').removeClass("edit-state");
}

$("#saveRequestHeaderTableBtn").click(function () {
    var trLines = $('#requestHeaderListTableBody').children('tr');
    var array = [];
    for (var i = 0; i < trLines.length; i++) {
        var field = $(trLines[i]).find("input[name=field]").val();
        var value = $(trLines[i]).find("input[name=value]").val();
        var optional = $(trLines[i]).find("select[name=optional]").val();
        var description = $(trLines[i]).find("input[name=description]").val();
        var map = {
            field: field,
            value: value,
            description: description,
            optional: optional
        };
        array.push(map)
    }
    $.ajax({
        url: '/restdoc/document/requestheaderdescriptor',
        method: 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify({
            documentId: documentId,
            values: array
        }),
        success: function (res) {
            if (res.code === '200') {
                lay.msg('保存成功');
                resetRequestHeaderTable();
            } else {
                lay.msg(res.message);
            }
        }
    });
});

function renderResponseBodyTableOnEditState(responseBodyFields) {
    if (responseBodyFields !== null && responseBodyFields.length > 0) {
        var allLine = '';
        for (var i = 0; i < responseBodyFields.length; i++) {
            var start = "<tr>", end = "</tr>";
            var line =
                '<td><input name="path" class="layui-input" value="' + responseBodyFields[i]['path'] + '"> </td>' +
                '<td>' +
                ' <div class="layui-form-item"><select name="type">' +
                '<option value="STRING" ' + (responseBodyFields[i]['type'] === "STRING" ? "selected" : "") + '>string</option>' +
                '<option value="OBJECT" ' + (responseBodyFields[i]['type'] === "OBJECT" ? "selected" : "") + '>object</option>' +
                '<option value="NUMBER" ' + (responseBodyFields[i]['type'] === "NUMBER" ? "selected" : "") + '>number</option>' +
                '<option value="BOOLEAN" ' + (responseBodyFields[i]['type'] === "BOOLEAN" ? "selected" : "") + '>boolean</option>' +
                '<option value="ARRAY" ' + (responseBodyFields[i]['type'] === "ARRAY" ? "selected" : "") + '>array</option>' +
                '</select></div></td>' +

                '<td><input name="value" class="layui-input" value="' + responseBodyFields[i]['value'] + '"></td>' +
                '<td> <input name="description" class="layui-input" value="' + responseBodyFields[i]['description'] + '"> </td>';

            allLine = allLine + start + line + end;
        }
        $("#responseBodyFieldListTableBody").html(allLine);
        gform.render();
    }
}

$('#editResponseBodyTableBtn').click(function () {
    if (responseBodyTableIsEditState) {
        resetResponseBodyTable();
        responseBodyTableIsEditState = false;
    } else {
        $('#saveResponseBodyTableBtn,#cancelResponseBodyTableBtn').css("display", 'block');
        $('#editResponseBodyTableBtn').addClass('edit-state');
        renderResponseBodyTableOnEditState(doc['responseBodyDescriptors'])
        responseBodyTableIsEditState = true;
    }

});

function resetResponseBodyTable() {
    $('#saveResponseBodyTableBtn,#cancelResponseBodyTableBtn').css("display", 'none');
    doc = getLastedDocument();
    renderResponseBodyTable(doc['responseBodyDescriptors']);
    $('#editResponseBodyTableBtn').removeClass('edit-state');
}

$('#saveResponseBodyTableBtn').click(function () {
    var trLines = $('#responseBodyFieldListTableBody').children('tr');
    var array = [];
    for (var i = 0; i < trLines.length; i++) {
        var path = $(trLines[i]).find("input[name=path]").val();
        var value = $(trLines[i]).find("input[name=value]").val();
        var type = $(trLines[i]).find("select[name=type]").val();
        var description = $(trLines[i]).find("input[name=description]").val();
        var map = {
            path: path,
            value: value,
            description: description,
            type: type
        };
        array.push(map)
    }
    $.ajax({
        url: '/restdoc/document/responsebodydescriptor',
        method: 'PATCH',
        contentType: 'application/json',
        data: JSON.stringify({
            documentId: documentId,
            values: array
        }),
        success: function (res) {
            if (res.code === '200') {
                lay.msg('保存成功');
                resetResponseBodyTable();
            } else {
                lay.msg(res.message);
            }
        }
    });
});

