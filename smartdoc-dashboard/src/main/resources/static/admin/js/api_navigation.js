
function setIframeHeight(id) {
    try {
        var iframe = document.getElementById(id);
        if (iframe.attachEvent) {
            iframe.attachEvent("onload", function () {
                iframe.height = iframe.contentWindow.document.documentElement.scrollHeight + 100;

            });
            return;
        } else {
            iframe.onload = function () {
                setTimeout(function () {
                }, 400)
                iframe.height = iframe.contentDocument.body.scrollHeight+ 100;

            };
            return;
        }
    } catch (e) {
        throw new Error('setIframeHeight Error');
    }
}

function getFirstDocument(array) {
    if (array == null || array.length === 0) return null;
    var length = array.length;
    var flag = 0;
    for (let i = 0; i < length; i++) {
        var node = array[i];
        if ("API" === node['type'] || "WIKI" === node['type']) {
            if (flag === 0) {
                return node['id']
            }
        } else {
            var result = findFirstChildren(node);
            if (result != null) return result;
        }
    }
    return null;
}

function findFirstChildren(parentNode) {
    if (parentNode != null && parentNode['children'].length === 0) {
        return null;
    } else {
        var children = parentNode['children'];
        var length = children.length;

        for (let i = 0; i < length; i++) {
            if (children[i]['type'] === 'WIKI' || children[i]['type'] === 'API') {
                return children[i]['id']
            } else {
                var result = findFirstChildren(children[i]);
                if (null != result) {
                    return result;
                }
            }
        }
        return null;
    }
}
