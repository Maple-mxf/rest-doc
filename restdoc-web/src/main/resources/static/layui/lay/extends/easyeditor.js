layui.define(['jquery', 'layer', 'form', 'element', 'upload', 'code', 'face'], function (exports) { //提示：模块也可以依赖其它模块，如：layui.define('layer', callback);

    var $ = layui.jquery,
        layer = layui.layer,
        form = layui.form,
        element = layui.element,
        upload = layui.upload,
        face = layui.face,
        device = layui.device();

    marked.setOptions({
        tables: true,
        breaks: true
    });

    layui.focusInsert = function (obj, str) {
        var result, val = obj.value;
        obj.focus();
        if (document.selection) { //ie
            result = document.selection.createRange();
            document.selection.empty();
            result.text = str;
        } else {
            result = [val.substring(0, obj.selectionStart), str, val.substr(obj.selectionEnd)];
            obj.focus();
            obj.value = result.join('');
        }
    };


    let easyeditor = {
        init: function (options) {
            if (options.style == 'fangge') layui.link("css/fangge-style.css");

            var html = ['<div class="layui-unselect fly-edit">',
                '<span type="face" title="插入表情"><i class="layui-icon layui-icon-face-smile" style="top: 1px;"></i></span>',
                // '<span type="picture" title="插入图片：img[src]"><i class="iconfont chengliangyun-md-icon-tupian"></i></span>',
                '<span type="href" title="超链接格式：a(href)[text]"><i class="layui-icon layui-icon-link"></i></span>',
                '<span type="code" title="插入代码"><i class="layui-icon layui-icon-fonts-code" style="top: 1px;"></i></span>',
                // '<span type="yinyong" title="引用"><i class="iconfont chengliangyun-md-icon-blockquote"></i></span>',
                '<span type="ul" title="无序列表"><i class="layui-icon layui-icon-more-vertical"></i></span>',
                '<span type="ol" title="有序列表"><i class="layui-icon layui-icon-list"></i></span>',
                '<span type="table" title="表格"><i class="layui-icon layui-icon-table"></i></span>',
                // '<span type="strong" title="加粗"><i class="layui-icon layui-icon-fonts-strong"></i></span>',
                // '<span type="h1" title="h1标题">h1</span>',
                // '<span type="h2" title="h2标题">h2</span>',
                // '<span type="h3" title="h3标题">h3</span>',
                // '<span type="h4" title="h4标题">h4</span>',
                '<span type="hr" title="分割线">hr</span>', '<div class="fly-right">',

                '<span type="yulan"  title="预览">预览</span>',
                '<span type="fullScreen"  title="全屏"><i class="iconfont chengliangyun-md-icon-quanping"></i></span>',
                '</div>'
            ].join('');

            var log = {},
                mod = {
                    face: function (editor, self) { //插入表情
                        var str = '',
                            ul, face = easyeditor.faces;
                        for (var key in face) {
                            str += '<li title="' + key + '"><img src="' + face[key] + '"></li>';
                        }
                        str = '<ul id="LAY-editface" class="layui-clear">' + str + '</ul>';
                        layer.tips(str, self, {
                            tips: 3,
                            time: 0,
                            skin: 'layui-edit-face'
                        });
                        $(document).on('click', function () {
                            layer.closeAll('tips');
                        });
                        $('#LAY-editface li').on('click', function () {
                            var title = $(this).attr('title') + ' ';
                            layui.focusInsert(editor[0], 'face' + title);

                            if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                                for (let i = 0; i < options.onchangeCallback.length; i++) {
                                    options.onchangeCallback[i]();
                                }
                            }

                        });
                    },
                    picture: function (editor) { //插入图片
                        options = options || {}
                        layer.open({
                            type: 1,
                            id: 'fly-jie-upload',
                            title: '插入图片',
                            area: 'auto',
                            shade: false,
                            area: '465px',
                            fixed: false,
                            offset: [
                                editor.offset().top - $(window).scrollTop() + 'px', editor.offset().left + 'px'
                            ],
                            skin: 'layui-layer-border',
                            content: ['<ul class="layui-form layui-form-pane" style="margin: 20px;">', '<li class="layui-form-item">',
                                '<label class="layui-form-label">URL</label>', '<div class="layui-input-inline">',
                                '<input required name="image" placeholder="支持直接粘贴远程图片地址" value="" class="layui-input">', '</div>',
                                '<button type="button" class="layui-btn layui-btn-primary" id="uploadImg"><i class="iconfont chengliangyun-md-icon-shangchuan"></i>上传图片</button>',
                                '</li>', '<li class="layui-form-item" style="text-align: center;">',
                                '<button type="button" lay-submit lay-filter="uploadImages" class="layui-btn">确认</button>', '</li>',
                                '</ul>'
                            ].join(''),
                            success: function (layero, index) {
                                var image = layero.find('input[name="image"]');

                                if (options.uploadUrl == null || options.uploadUrl == '') {
                                    layer.msg('未配置图片上传路径,图片无法保存', {
                                        icon: 5
                                    });
                                }

                                //执行上传实例
                                upload.render({
                                    elem: '#uploadImg',
                                    url: options.uploadUrl,
                                    size: options.uploadSize || 1024,
                                    done: function (res) {
                                        if (res.code == 0) {
                                            image.val(res.url);
                                        } else {
                                            layer.msg(res.msg, {
                                                icon: 5
                                            });
                                        }
                                    }
                                });
                                form.on('submit(uploadImages)', function (data) {
                                    var field = data.field;
                                    if (!field.image) return image.focus();
                                    layui.focusInsert(editor[0], '![图片加载失败](' + field.image + ')\n');

                                    if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                                        for (let i = 0; i < options.onchangeCallback.length; i++) {
                                            options.onchangeCallback[i]();
                                        }
                                    }

                                    layer.close(index);
                                });
                            }
                        });
                    },
                    href: function (editor) { //超链接
                        layer.prompt({
                            title: '请输入合法链接',
                            shade: false,
                            fixed: false,
                            id: 'LAY_flyedit_href',
                            offset: [
                                editor.offset().top - $(window).scrollTop() + 'px', editor.offset().left + 'px'
                            ]
                        }, function (val, index, elem) {
                            val = "http://www.baidu.com";
                            if (!/^http(s*):\/\/[\S]/.test(val)) {
                                layer.tips('这根本不是个链接，不要骗我。', elem, {
                                    tips: 1
                                });
                                return;
                            }
                            layui.focusInsert(editor[0], ' [' + val + '](' + val + ')');

                            if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                                for (let i = 0; i < options.onchangeCallback.length; i++) {
                                    options.onchangeCallback[i]();
                                }
                            }

                            layer.close(index);
                        });
                    },
                    code: function (editor) { //插入代码
                        layer.prompt({
                            title: '请贴入代码',
                            formType: 2,
                            maxlength: 10000,
                            shade: false,
                            id: 'LAY_flyedit_code',
                            area: ['800px', '360px']
                        }, function (val, index, elem) {
                            layui.focusInsert(editor[0], '\n~~~\n' + val + '\n~~~\n');

                            if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                                for (let i = 0; i < options.onchangeCallback.length; i++) {
                                    options.onchangeCallback[i]();
                                }
                            }

                            layer.close(index);
                        });
                    },
                    yinyong: function (editor) {
                        layer.prompt({
                            title: '请贴入引用内容',
                            formType: 2,
                            maxlength: 10000,
                            shade: false,
                            id: 'LAY_flyedit_code',
                            area: ['800px', '360px']
                        }, function (val, index, elem) {
                            layui.focusInsert(editor[0], '> ' + val + '\n');

                            if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                                for (let i = 0; i < options.onchangeCallback.length; i++) {
                                    options.onchangeCallback[i]();
                                }
                            }

                            layer.close(index);
                        });
                    },
                    hr: function (editor) { //插入水平分割线
                        layui.focusInsert(editor[0], '-----\n');

                        if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                            for (let i = 0; i < options.onchangeCallback.length; i++) {
                                options.onchangeCallback[i]();
                            }
                        }

                    },
                    // 加粗
                    strong: function (editor) {
                        layui.focusInsert(editor[0], '**加粗**\n');
                        if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                            for (let i = 0; i < options.onchangeCallback.length; i++) {
                                options.onchangeCallback[i]();
                            }
                        }
                    },

                    h1: function (editor) {
                        layui.focusInsert(editor[0], '#1  ');
                        if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                            for (let i = 0; i < options.onchangeCallback.length; i++) {
                                options.onchangeCallback[i]();
                            }
                        }
                    },
                    h2: function (editor) {
                        layui.focusInsert(editor[0], '#2  ');
                        if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                            for (let i = 0; i < options.onchangeCallback.length; i++) {
                                options.onchangeCallback[i]();
                            }
                        }
                    },

                    h3: function (editor) {
                        layui.focusInsert(editor[0], '#3  ');
                        if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                            for (let i = 0; i < options.onchangeCallback.length; i++) {
                                options.onchangeCallback[i]();
                            }
                        }
                    },

                    h4: function (editor) {
                        layui.focusInsert(editor[0], '#4   ');
                        if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                            for (let i = 0; i < options.onchangeCallback.length; i++) {
                                options.onchangeCallback[i]();
                            }
                        }
                    },

                    ul: function (editor) { //插入无序列表
                        layui.focusInsert(editor[0], '\n-  \n-  \n-  \n');

                        if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                            for (let i = 0; i < options.onchangeCallback.length; i++) {
                                options.onchangeCallback[i]();
                            }
                        }

                    }
                    ,
                    ol: function (editor) { //插入有序列表
                        layui.focusInsert(editor[0], '\n1. \n2. \n3. \n');

                        if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                            for (let i = 0; i < options.onchangeCallback.length; i++) {
                                options.onchangeCallback[i]();
                            }
                        }

                    }
                    ,
                    table: function (editor) {
                        layui.focusInsert(editor[0], '\n表头|表头|表头\n:---:|:--:|:---:\n内容|内容|内容\n');

                        if (options.onchangeCallback != null && options.onchangeCallback.length > 0) {
                            for (let i = 0; i < options.onchangeCallback.length; i++) {
                                options.onchangeCallback[i]();
                            }
                        }
                    }
                    ,
                    fullScreen: function (editor, span) { //全屏
                        $(window).resize(function () { //当浏览器大小变化时
                            //获取浏览器窗口高度
                            var winHeight = 0;
                            if (window.innerHeight)
                                winHeight = window.innerHeight;
                            else if ((document.body) && (document.body.clientHeight))
                                winHeight = document.body.clientHeight;
                            //通过深入Document内部对body进行检测，获取浏览器窗口高度
                            if (document.documentElement && document.documentElement.clientHeight)
                                winHeight = document.documentElement.clientHeight;
                            $(options.elem).css('height', winHeight - 40 + "px");
                            $(window).unbind('resize');
                        });
                        var othis = $(span);
                        othis.attr("type", "exitScreen");
                        othis.attr("title", "退出全屏");
                        othis.html('<i class="iconfont chengliangyun-md-icon-tuichuquanping"></i>');
                        var ele = document.documentElement,
                            reqFullScreen = ele.requestFullScreen || ele.webkitRequestFullScreen ||
                                ele.mozRequestFullScreen || ele.msRequestFullscreen;
                        if (typeof reqFullScreen !== 'undefined' && reqFullScreen) {
                            reqFullScreen.call(ele);
                        }
                        ;
                    }
                    ,
                    exitScreen: function (editor, span) { //退出全屏
                        var othis = $(span);
                        othis.attr("type", "fullScreen");
                        othis.attr("title", "全屏");
                        othis.html('<i class="iconfont chengliangyun-md-icon-quanping"></i>');
                        var ele = document.documentElement
                        if (document.exitFullscreen) {
                            document.exitFullscreen();
                        } else if (document.mozCancelFullScreen) {
                            document.mozCancelFullScreen();
                        } else if (document.webkitCancelFullScreen) {
                            document.webkitCancelFullScreen();
                        } else if (document.msExitFullscreen) {
                            document.msExitFullscreen();
                        }
                        //恢复初始高度
                        $(options.elem).css("height", "270px");
                    }
                    ,
                    // 预览
                    yulan: function (editor, span) {
                        var othis = $(span),
                            getContent = function () {
                                var content = editor.val();
                                return /^\{html\}/.test(content) ?
                                    content.replace(/^\{html\}/, '') :
                                    easyeditor.content(content)
                            },
                            isMobile = device.ios || device.android;

                        if (mod.yulan.isOpen) return layer.close(mod.yulan.index);

                        mod.yulan.index = layer.open({
                            type: 1,
                            title: '预览',
                            shade: false,
                            offset: 'r',
                            id: 'LAY_flyedit_yulan',
                            area: [
                                isMobile ? '100%' : '50%', '100%'
                            ],
                            scrollbar: isMobile ? false : true,
                            anim: -1,
                            isOutAnim: false,
                            content: '<div class="detail-body layui-text easyeditor-content" style="margin:20px;">' + getContent() + '</div>',
                            success: function (layero) {
                                // easyeditor.codeContent({elem: layero.find('pre')});
                                editor.on('keyup', function (val) {
                                    layero.find('.detail-body').html(getContent());
                                    // easyeditor.codeContent({elem: layero.find('pre')});
                                });
                                mod.yulan.isOpen = true;
                                othis.addClass('layui-this');
                            },
                            end: function () {
                                delete mod.yulan.isOpen;
                                othis.removeClass('layui-this');
                            }
                        });

                    }
                    ,


                }
            ;
            layui.use('face', function (face) {
                options = options || {};
                easyeditor.faces = face;
                $(options.elem).each(function (index) {
                    var that = this,
                        othis = $(that),
                        parent = othis.parent();
                    parent.prepend(html);
                    parent.find('.fly-edit span').on('click', function (event) {
                        var type = $(this).attr('type');
                        mod[type].call(that, othis, this);
                        if (type === 'face') {
                            event.stopPropagation()
                        }
                    });
                });

                $(".fly-edit span").css("color", options.buttonColor ? options.buttonColor : "");
                $(".fly-edit span").hover(function () {
                    $(this).css("color", options.hoverColor ? options.hoverColor : "").css("background-color", options.hoverBgColor ? options.hoverBgColor : "")
                }, function () {
                    $(this).css("color", options.buttonColor ? options.buttonColor : "").css("background-color", "")
                });
            });


        },
        codeContent: function (options) {
            let params = {
                elem: options.elem,
                title: 'code',
                about: false,
                encode: true
            };
            if (options.codeSkin === 'notepad') {
                params.skin = 'notepad';
            }
            layui.code(params);
        },
        content: function (content) {
            content = marked(content.replace(/  \n/g, "<br>")) //强制换行
                .replace(/#1/, "<h1>")
                .replace(/#2/, "<h2>")
                .replace(/#3/, "<h3>")
                .replace(/#4/, "<h4>")
                .replace(/\*\*/, '<strong>')
                .replace(/<code>|<\/code>/g, "") //去除代码块内侧的code标签
                .replace(/<a/g, "<a target='blank' rel='nofollow'") //转义超链接
                .replace(/<table/g, "<table class='layui-table' ") //表格样式
                .replace(/<blockquote/g, "<blockquote class='layui-elem-quote layui-text'")
                .replace(/\*\*/g, "<strong>")
                .replace(/face\[([^\s\[\]]+?)\]/g, function (face) { //转义表情
                    let alt = face.replace(/^face/g, '');
                    return '<img alt="' + alt + '" title="' + alt + '" src="' + easyeditor.faces[alt] + '">';
                });
            return content;
        },
        render: function (options) {
            options = options || {};
            $(options.elem).each(function () {
                let othis = $(this), text = othis.text();
                othis.html(easyeditor.content(text));
            });
            easyeditor.codeContent({elem: $(options.elem).find('pre')});
        }
    };

    if (!easyeditor.faces) {
        easyeditor.faces = face;
    }

    exports('easyeditor', easyeditor);
});
