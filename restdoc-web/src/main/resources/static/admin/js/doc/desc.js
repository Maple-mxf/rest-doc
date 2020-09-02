var pre_textarea_html_code = "<!DOCTYPE html>\n" +
    "<html xmlns:th=\"http://www.thymeleaf.org\">\n" +
    "<head>\n" +
    "    <meta charset=\"UTF-8\">\n" +
    "    <title>Title</title>\n" +
    "\n" +
    "    <link type=\"text/css\" rel=\"stylesheet\" th:href=\"@{/admin/css/materialize.min.css}\" media=\"screen,projection\"/>\n" +
    "\n" +
    "    <!--Let browser know website is optimized for mobile-->\n" +
    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
    "\n" +
    "    <link rel=\"stylesheet\" th:href=\"@{/layui/css/layui.css}\"/>\n" +
    "    <link rel=\"stylesheet\" th:href=\"@{/admin/css/pearForm.css}\"/>\n" +
    "    <link rel=\"stylesheet\" th:href=\"@{/admin/css/easyeditor.css}\"/>\n" +
    "    <link rel=\"stylesheet\" th:href=\"@{/admin/css/fangge-style.css}\"/>\n" +
    "    <link href=\"https://fonts.googleapis.com/css2?family=Roboto&display=swap\" rel=\"stylesheet\">\n" +
    "\n" +
    "</head>\n" +
    "<body>\n" +
    "<!-- Markdown编辑器元素 -->\n" +
    "<textarea id=\"L_content\" name=\"content\" style=\"\" required lay-verify=\"required\" placeholder=\"请输入字段备注\"\n" +
    "          class=\"layui-textarea editor\">";


function getDescHtmlString(value) {
    return pre_textarea_html_code + value + suffix_textarea_html_code;
}


var suffix_textarea_html_code = "</textarea>\n" +
    "\n" +
    "<script th:src=\"@{/layui/layui.js}\"></script>\n" +
    "<script th:src=\"@{/admin/js/jquery.js}\"></script>\n" +
    "<script th:src=\"@{/admin/js/marked.min.js}\"></script>\n" +
    "\n" +
    "<script>\n" +
    "    layui.config({\n" +
    "        restdoc.web.base: '/restdoc/layui/lay/extends/'\n" +
    "    }).extend({\n" +
    "        easyeditor: 'easyeditor'\n" +
    "    }).use(['easyeditor'], function () {\n" +
    "        var easyeditor = layui.easyeditor;\n" +
    "        easyeditor.init({\n" +
    "            elem: '.editor'\n" +
    "            , uploadUrl: '/upload'\n" +
    "            , videoUploadUrl: '/upload'\n" +
    "            , videoUploadSize: '102400'\n" +
    "            , uploadSize: ''\n" +
    "        });\n" +
    "    });\n" +
    "\n" +
    "    function getDescValue() {\n" +
    "        return $(\"#L_content\").val()\n" +
    "    }\n" +
    "</script>\n" +
    "</body>\n" +
    "</html>";