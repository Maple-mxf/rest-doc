db.createUser(
        {
            user: "front_biz",
            pwd: "qmbxfrontbizStorage",
            roles: [
                {
                    role: "readWrite",
                    db: "non_standard"
                }
            ]
        }
);
db.getCollection("restdoc_user").insert( {
    _id: "768659280285929472",
    account: "Maple",
    password: "fc0e0ddf1f6a4f4d26750ca8f797a4e0",
    createTime: NumberLong("1603333053419"),
    status: "NORMAL",
    teamId: "default",
    _class: "restdoc.web.model.User"
} );

db.getCollection("restdoc_project").insert( {
    _id: "769897798609932288",
    name: "一个web应用",
    desc: "普通web应用",
    createTime: NumberLong("1603628339198"),
    type: "REST_WEB",
    allowAccessNonPasswords: false,
    _class: "restdoc.web.model.Project"
} );

db.getCollection("restdoc_restweb_document").insert( {
    _id: "769897848455041024",
    projectId: "769897798609932288",
    name: "示例API文档",
    resource: "root",
    url: "/contextPath/apiPath/{uriVars}",
    requestHeaderDescriptor: [
        {
            field: "Content-Type",
            value: [
                "application/json;utf-8"
            ],
            description: "请求头格式说明",
            optional: true
        },
        {
            field: "Accept",
            value: [
                "application/json"
            ],
            description: "可以接受的数据格式",
            optional: true
        }
    ],
    requestBodyDescriptor: [
        {
            path: "firstName",
            value: "Brett",
            description: "第一个名字",
            type: "STRING",
            optional: true
        },
        {
            path: "lastName",
            value: "McLaughlin",
            description: "最后一个名字",
            type: "STRING",
            optional: true
        },
        {
            path: "email",
            value: "brett@newInstance.com",
            description: "<p>Email邮箱，比如如下几种格式</p><p>- m17793873123@163.com</p><p>- 244@6011668@qq.com</p><p>- brett@newInstance.com</p>",
            type: "STRING",
            optional: true
        }
    ],
    responseBodyDescriptors: [
        {
            path: "code",
            value: "200",
            description: "200表示响应正确，其他则响应错误",
            type: "STRING",
            optional: true
        },
        {
            path: "status",
            value: "OK",
            description: "<p><font color=\"#5fb878\">OK</font>表示正确，其他表示错误</p><p><u><font color=\"#cc0000\">NotFound</font></u>：找不到请求地址</p><p><u><font color=\"#cc0000\">BadRequest</font></u>：客户端请求的语法错误，服务器无法理解</p><p><u><font color=\"#cc0000\">Forbidden</font></u>：服务器理解请求客户端的请求，但是拒绝执行此请求</p><p><u><font color=\"#cc0000\">InvalidRequest</font></u>：请求无效，请刷新页面重试</p><p><u><font color=\"#cc0000\">Unauthorized</font></u>：请求要求用户的身份认证</p><p><u><font color=\"#cc0000\">UnsupportedMediaType</font></u>：服务器无法处理请求附带的媒体格式</p><p><u><font color=\"#cc0000\">InternalServerError</font></u>：服务器内部错误</p><p><u><font color=\"#cc0000\">ThirdServiceError</font></u>：第三方服务错误</p>",
            type: "STRING",
            optional: true
        },
        {
            path: "message",
            value: "服务器错误",
            description: "如果请求过程发生了错误，message不可为空",
            type: "STRING",
            optional: true
        },
        {
            path: "data",
            value: "{}",
            description: "响应的数据体，一般是JSON对象或者JSON数组",
            type: "OBJECT",
            optional: true
        }
    ],
    method: "POST",
    uriVarDescriptors: [
        {
            field: "uriVars",
            value: "uriPathValue",
            description: "路径参数"
        }
    ],
    docType: "API",
    _class: "restdoc.web.model.RestWebDocument",
    description: "我的第一个RESTdoc API文档"
} );